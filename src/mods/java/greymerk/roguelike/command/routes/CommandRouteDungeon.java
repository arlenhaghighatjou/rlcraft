package greymerk.roguelike.command.routes;

import greymerk.roguelike.command.CommandBase;
import greymerk.roguelike.command.CommandContext1_12;
import greymerk.roguelike.command.CommandRouteBase;
import greymerk.roguelike.command.routes.exception.NoValidLocationException;
import greymerk.roguelike.command.routes.exception.SettingNameNotFoundException;
import greymerk.roguelike.dungeon.Dungeon;
import greymerk.roguelike.dungeon.settings.DungeonSettings;
import greymerk.roguelike.dungeon.settings.SettingsRandom;
import greymerk.roguelike.dungeon.settings.SettingsResolver;
import greymerk.roguelike.dungeon.settings.TestDungeonSettings;
import greymerk.roguelike.util.ArgumentParser;
import greymerk.roguelike.worldgen.Coord;
import greymerk.roguelike.worldgen.WorldEditor;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.function.Supplier;

public class CommandRouteDungeon extends CommandRouteBase {
   public CommandRouteDungeon(CommandBase commandBase) {
      super(commandBase);
   }

   public Coord getLocation(CommandContext1_12 context, List<String> args) {
      ArgumentParser argumentParser = new ArgumentParser(args);
      if (!argumentParser.match(0, "here") && !argumentParser.match(0, "nearby")) {
         try {
            int x = this.commandBase.parseInt(argumentParser.get(0));
            int z = this.commandBase.parseInt(argumentParser.get(1));
            return new Coord(x, 0, z);
         } catch (RuntimeException var6) {
            context.sendFailure("Invalid Coords: X Z");
            throw var6;
         }
      } else {
         Coord coord = context.getPos();
         return new Coord(coord.getX(), 0, coord.getZ());
      }
   }

   public void execute(CommandContext1_12 context, List<String> args) {
      ArgumentParser argumentParser = new ArgumentParser(args);
      if (!argumentParser.hasEntry(0)) {
         context.sendInfo("Usage: roguelike dungeon {X Z | here} [setting]");
      } else {
         String settingName = this.getSettingName(argumentParser);

         try {
            Coord pos = this.getLocation(context, args);
            WorldEditor editor = context.createEditor();
            DungeonSettings dungeonSettings = this.chooseDungeonSettings(settingName, pos, editor);
            this.generateDungeon(context, pos, editor, dungeonSettings);
         } catch (Exception var8) {
            context.sendFailure(var8);
         }

      }
   }

   private DungeonSettings chooseDungeonSettings(String settingName, Coord pos, WorldEditor editor) throws Exception {
      if (settingName == null) {
         return this.resolveAnyCustomDungeonSettings(pos, editor);
      } else if (settingName.equals("test")) {
         return this.resolveTestDungeon(editor, pos);
      } else {
         return settingName.equals("random") ? this.resolveRandomDungeon(editor, pos) : this.resolveNamedDungeonSettings(settingName);
      }
   }

   private DungeonSettings resolveAnyCustomDungeonSettings(Coord pos, WorldEditor editor) throws Exception {
      SettingsResolver settingsResolver = SettingsResolver.initSettingsResolver();
      Optional<DungeonSettings> dungeonSettings = settingsResolver.chooseDungeonSetting(editor, pos);
      return (DungeonSettings)dungeonSettings.orElseThrow(() -> {
         return new NoValidLocationException(pos);
      });
   }

   private DungeonSettings resolveTestDungeon(WorldEditor editor, Coord pos) throws Exception {
      Dungeon.initResolver();
      Random random = editor.getRandom();
      random.setSeed((long)editor.getSeed(pos));
      return new TestDungeonSettings(random);
   }

   private DungeonSettings resolveRandomDungeon(WorldEditor editor, Coord pos) throws Exception {
      Dungeon.initResolver();
      Random random = editor.getRandom();
      random.setSeed((long)editor.getSeed(pos));
      return new SettingsRandom(random);
   }

   private DungeonSettings resolveNamedDungeonSettings(String settingName) throws Exception {
      Dungeon.initResolver();
      DungeonSettings dungeonSettings = SettingsResolver.initSettingsResolver().getByName(settingName);
      return (DungeonSettings)Optional.ofNullable(dungeonSettings).orElseThrow(() -> {
         return new SettingNameNotFoundException(settingName);
      });
   }

   private void generateDungeon(CommandContext1_12 context, Coord coord, WorldEditor editor, DungeonSettings dungeonSettings) {
      Dungeon dungeon = new Dungeon(editor);
      dungeon.generate(dungeonSettings, coord);
      context.sendSuccess(String.format("Successfully generated dungeon with id %s at %s.%n", dungeonSettings.getId(), coord));
   }

   private String getSettingName(ArgumentParser argumentParser) {
      int index = !argumentParser.match(0, "here") && !argumentParser.match(0, "nearby") ? 2 : 1;
      return argumentParser.get(index);
   }
}
