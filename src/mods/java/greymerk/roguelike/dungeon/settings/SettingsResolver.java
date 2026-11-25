package greymerk.roguelike.dungeon.settings;

import com.google.common.base.Charsets;
import com.google.common.collect.Lists;
import com.google.common.io.Files;
import greymerk.roguelike.config.RogueConfig;
import greymerk.roguelike.util.WeightedChoice;
import greymerk.roguelike.util.WeightedRandomizer;
import greymerk.roguelike.worldgen.Coord;
import greymerk.roguelike.worldgen.WorldEditor;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
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
      File settingsDirectoryFile = new File("config/roguelike_dungeons/settings");
      if (settingsDirectoryFile.exists() && !settingsDirectoryFile.isDirectory()) {
         throw new Exception("Settings directory is a file");
      } else {
         if (!settingsDirectoryFile.exists()) {
            settingsDirectoryFile.mkdir();
         }

         Map<String, String> fileByName = collectSettingsFiles(settingsDirectoryFile);
         SettingsContainer settings = new SettingsContainer(fileByName);
         return new SettingsResolver(settings);
      }
   }

   private static Map<String, String> collectSettingsFiles(File settingsDirectory) {
      List<File> files = listFilesRecursively(settingsDirectory);
      return mapContentByFilename(files);
   }

   private static List<File> listFilesRecursively(File settingsDirectory) {
      File[] files = settingsDirectory.listFiles();
      return Optional.ofNullable(files).isPresent() ? (List)Lists.newArrayList(files).stream().flatMap((file) -> {
         return file.isDirectory() ? listFilesRecursively(file).stream() : Lists.newArrayList(new File[]{file}).stream();
      }).filter((file) -> {
         return FilenameUtils.getExtension(file.getName()).equals("json");
      }).collect(Collectors.toList()) : Collections.emptyList();
   }

   private static Map<String, String> mapContentByFilename(List<File> files) {
      return (Map)files.stream().collect(Collectors.toMap(File::getAbsolutePath, SettingsResolver::getFileContent));
   }

   private static String getFileContent(File file) {
      try {
         return Files.toString(file, Charsets.UTF_8);
      } catch (IOException var2) {
         throw new RuntimeException("Error reading file : " + file.getName());
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
      Stream var10000 = dungeonSettings.getInherit().stream();
      SettingsContainer var10001 = this.settingsContainer;
      this.settingsContainer.getClass();
      DungeonSettings accumulatedInheritedSettings = (DungeonSettings)var10000.map(var10001::get).map(this::processInheritance).reduce(new DungeonSettings(), (accumulation, toInherit) -> {
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
