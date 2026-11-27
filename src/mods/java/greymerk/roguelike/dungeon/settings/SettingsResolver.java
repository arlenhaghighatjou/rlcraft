package greymerk.roguelike.dungeon.settings;

import net.lax1dude.eaglercraft.v1_8.internal.vfs2.VFile2;
import greymerk.roguelike.config.RogueConfig;
import greymerk.roguelike.util.WeightedChoice;
import greymerk.roguelike.util.WeightedRandomizer;
import greymerk.roguelike.worldgen.Coord;
import greymerk.roguelike.worldgen.WorldEditor;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.BinaryOperator;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import net.minecraft.client.Minecraft;
import org.apache.commons.io.FilenameUtils;

public class SettingsResolver {
   private final SettingsContainer settingsContainer;

   public SettingsResolver(SettingsContainer settingsContainer) {
      this.settingsContainer = settingsContainer;
   }

   public static SettingsResolver initSettingsResolver() throws Exception {
      VFile2 settingsDirectoryFile = new VFile2("config/roguelike_dungeons/settings");
      Map<String, String> fileByName = collectSettingsFiles(settingsDirectoryFile);
      SettingsContainer settings = new SettingsContainer(fileByName);
      return new SettingsResolver(settings);
   }

   private static Map<String, String> collectSettingsFiles(VFile2 settingsDirectory) {
      List<VFile2> files = listFilesRecursively(settingsDirectory);
      return mapContentByFilename(files);
   }

   private static List<VFile2> listFilesRecursively(VFile2 settingsDirectory) {
      return (List)settingsDirectory.listFiles(true).stream().filter((file) -> {
         return FilenameUtils.getExtension(file.getName()).equals("json");
      }).collect(Collectors.toList());
   }

   private static Map<String, String> mapContentByFilename(List<VFile2> files) {
      return (Map)files.stream().collect(Collectors.toMap(VFile2::getPath, SettingsResolver::getFileContent));
   }

   private static String getFileContent(VFile2 file) {
      String content = file.getAllChars();
      if (content == null) {
         throw new RuntimeException("Error reading file : " + file.getName());
      } else {
         return content;
      }
   }

   public Optional<DungeonSettings> chooseDungeonSetting(WorldEditor editor, Coord coord) {
      Optional<DungeonSettings> customDungeon = this.chooseRandomCustomDungeonIfPossible(editor, coord);
      return customDungeon.isPresent() ? customDungeon : this.chooseOneBuiltinSettingAtRandom(editor, coord);
   }

   public DungeonSettings getByName(String name) {
      try {
         SettingIdentifier id = new SettingIdentifier(name);
         DungeonSettings dungeonSettings = this.settingsContainer.get(id);
         return this.processInheritance(dungeonSettings);
      } catch (Exception var4) {
         Minecraft.getMinecraft().player.sendChatMessage(Arrays.toString(var4.getStackTrace()));
         throw new RuntimeException(var4);
      }
   }

   public DungeonSettings processInheritance(DungeonSettings dungeonSettings) {
      DungeonSettings accumulatedInheritedSettings = (DungeonSettings)dungeonSettings.getInherit().stream().map(this.settingsContainer::get).map(this::processInheritance).reduce(new DungeonSettings(), (accumulation, toInherit) -> {
         return toInherit.inherit(accumulation);
      });
      return dungeonSettings.inherit(accumulatedInheritedSettings);
   }

   private Optional<DungeonSettings> chooseOneBuiltinSettingAtRandom(WorldEditor editor, Coord coord) {
      return !RogueConfig.SPAWNBUILTIN.getBoolean() ? Optional.empty() : this.chooseRandomValid(editor, coord, this.settingsContainer.getBuiltinSettings());
   }

   private Optional<DungeonSettings> chooseRandomCustomDungeonIfPossible(WorldEditor editor, Coord coord) {
      return this.chooseRandomValid(editor, coord, this.settingsContainer.getCustomSettings());
   }

   private Optional<DungeonSettings> chooseRandomValid(WorldEditor editor, Coord coord, Collection<DungeonSettings> settings) {
      List<DungeonSettings> inflatedSettings = (List)settings.stream().map(this::processInheritance).filter(this.isValid(editor, coord)).collect(Collectors.toList());
      WeightedRandomizer<DungeonSettings> settingsRandomizer = this.newWeightedRandomizer(inflatedSettings);
      return Optional.ofNullable(settingsRandomizer.get(editor.getRandom()));
   }

   private Predicate<DungeonSettings> isValid(WorldEditor editor, Coord coord) {
      return (setting) -> {
         return setting.isExclusive() && setting.isValid(editor, coord);
      };
   }

   private WeightedRandomizer<DungeonSettings> newWeightedRandomizer(List<DungeonSettings> dungeonSettings) {
      WeightedRandomizer<DungeonSettings> settingsRandomizer = new WeightedRandomizer<DungeonSettings>();
      dungeonSettings.stream().map((setting) -> {
         return new WeightedChoice<DungeonSettings>(setting, setting.getCriteria().getWeight());
      }).forEach(settingsRandomizer::add);
      return settingsRandomizer;
   }

   public String toString(String namespace) {
      return (String)this.settingsContainer.getByNamespace(namespace).stream().map(DungeonSettings::getId).map(SettingIdentifier::toString).collect(Collectors.joining(" "));
   }

   public String toString() {
      return this.settingsContainer.toString();
   }
}
