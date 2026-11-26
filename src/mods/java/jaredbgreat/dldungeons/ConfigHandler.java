package jaredbgreat.dldungeons;

import net.lax1dude.eaglercraft.v1_8.internal.vfs2.VFile2;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import jaredbgreat.dldungeons.builder.Builder;
import jaredbgreat.dldungeons.builder.RegisteredBlock;
import jaredbgreat.dldungeons.pieces.chests.BasicChest;
import jaredbgreat.dldungeons.pieces.chests.TreasureChest;
import jaredbgreat.dldungeons.planner.mapping.MapMatrix;
import jaredbgreat.dldungeons.rooms.Room;
import jaredbgreat.dldungeons.setup.Externalizer;
import jaredbgreat.dldungeons.themes.ThemeReader;
import jaredbgreat.dldungeons.util.debug.Logging;
import java.io.BufferedWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import net.minecraft.block.Block;
import net.minecraft.entity.EntityList;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.common.BiomeDictionary.Type;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.common.registry.ForgeRegistries;

public final class ConfigHandler {
   private static VFile2 mainConfig;
   private static VFile2 themesDir;
   private static VFile2 listsDir;
   protected static VFile2 configDir;
   private static final int DEFAULT_SCALE = 8;
   private static final int DEFAULT_MINXZ = 16;
   private static final int DEFAULT_DIF = 3;
   private static final int[] DEFAULT_DIMS = new int[]{0, -1};
   private static final boolean DEFAULT_WRITE_LISTS = false;
   private static final boolean DEFAULT_NATURAL_SPAWN = true;
   private static final boolean DEFAULT_OBEY_RULE = true;
   private static final boolean DEFAULT_POSITIVE_DIMS = true;
   private static final boolean DEFAULT_ANNOUNCE_COMMANDS = true;
   private static final boolean DEFAULT_THIN_SPAWNERS = true;
   private static final boolean DEFAULT_VANILLA_LOOT = false;
   private static final boolean EASY_FIND = true;
   private static final boolean SINGLE_ENTRANCE = true;
   private static final boolean BIG_HUBS = false;
   private static final boolean ALL_HUGE = false;
   private static final boolean DISABLE_API = false;
   private static final boolean NO_MOB_CHANGES = false;
   public static boolean disableAPI = false;
   public static boolean noMobChanges = false;
   private static final String[] NEVER_IN_BIOMES = new String[]{"END"};
   private static String[] neverInBiomes = NEVER_IN_BIOMES;
   public static HashSet<Type> biomeExclusions = new HashSet();
   protected static boolean writeLists = false;
   protected static boolean naturalSpawn = true;
   protected static boolean obeyRule = true;
   protected static boolean positiveDims = true;
   public static boolean easyFind = true;
   public static boolean singleEntrance = true;
   public static boolean bigHubs = false;
   public static boolean allHuge = false;
   public static boolean announceCommands = true;
   public static boolean vanillaLoot = false;
   public static boolean thinSpawners = true;
   private static final boolean PROFILE = false;
   protected static boolean profile;
   private static final boolean FAILFAST = false;
   public static boolean failfast = false;
   private static final boolean INSTALL_THEMES = true;
   public static boolean installThemes = true;
   private static final boolean INSTALL_CMD = true;
   public static boolean installCmd = true;
   public static Difficulty difficulty;

   private ConfigHandler() {
   }

   public static void init() {
      VFile2 file = new VFile2(configDir, "DLDungeonsJBG" + ".cfg");
      Configuration config = new Configuration(file);
      config.load();
      config.addCustomCategoryComment("General", "Main config settings");
      int freqScale = config.get("General", "FrequencyScale", 8, "Determines the average distance between dungeons (+2 is twice as far appart)").getInt();
      if (freqScale > 30 || freqScale < 6) {
         freqScale = 8;
      }

      GenerationHandler.setFrequency(freqScale);
      Logging.logInfo("Frequency Scaling Factor Set To: " + freqScale);
      int minXZ = config.get("General", "MinChunkXY", 16, "Spawn protection: this is the minimum number of chunks from spawn before dungeon generate").getInt();
      if (minXZ < 0) {
         minXZ = 16;
      }

      GenerationHandler.setMinXZ(minXZ);
      Logging.logInfo("Minimum X Factor Set To: " + minXZ);
      int diff = config.get("General", "Difficulty", 3, "How hard: 0 = empty, 1 = baby, 2 = easy, 3 = normal, 4= hard, 5 = nightmare").getInt();
      if (diff < 0 || diff > 5) {
         diff = 3;
      }

      parseDiff(diff);
      Logging.logInfo("Difficulty set to: " + difficulty.label);
      int[] dims = config.get("General", "Dimensions", DEFAULT_DIMS, "These dimensions either lack dungeons or only they have them (see OnlySpawnInListedDimensions)").getIntList();
      GenerationHandler.setDimensions(dims);
      System.out.print("Dimensions listed in config file: ");

      for(int i = 0; i < dims.length; ++i) {
         System.out.print(dims[i] + ", ");
      }

      System.out.println();
      naturalSpawn = config.get("General", "SpawnWithWordgen", true, "True to have dungeons generate naturally, false otherwise.").getBoolean(true);
      Logging.logInfo("Will spawn dungeons in with world generation? " + naturalSpawn);
      obeyRule = config.get("General", "ObeyFeatureSpawningRule", true, "If true worlds that are set to not generate (vanilla) structures will also not have these dungesons").getBoolean(true);
      Logging.logInfo("Will spawn dungeons even with structures disabled? " + !obeyRule);
      positiveDims = config.get("General", "OnlySpawnInListedDims", true, "Determines if the dimesions list is black list of white list; " + System.lineSeparator() + "if true only listed dimensions have dungeons, " + System.lineSeparator() + "otherswise all but those will have them").getBoolean(true);
      if (positiveDims) {
         System.out.print("Will only spawn in these dimensions: ");
      } else {
         System.out.print("Will never spawn in these dimensions: ");
      }

      for(int i = 0; i < dims.length; ++i) {
         System.out.print(dims[i] + ", ");
      }

      System.out.println();
      writeLists = config.get("General", "ExportLists", false, "If true a \"lists\" folder will be created and files showing names and ideas for all " + System.lineSeparator() + "mobs / items / blocks will be made.  This is good for editing " + System.lineSeparator() + "themes.").getBoolean(false);
      Logging.logInfo("Will export item, block, and mob lists? " + writeLists);
      announceCommands = config.get("General", "AnnounceCommands", true, "If true, console commands from the mod will be annouced in chat").getBoolean(true);
      Logging.logInfo("Will announce use of OP/cheat commands? " + announceCommands);
      easyFind = config.get("General", "EasyFind", true, "If true dungeons will all have an entrance with a room or ruin, unless the theme " + System.lineSeparator() + "is one that never has entrances.").getBoolean(true);
      Logging.logInfo("Will dungeons be easy to find? " + easyFind);
      bigHubs = config.get("General", "BigHubs", false, "If true entrances and \"boss\" rooms will have extra high ceilings, good with tall mobs").getBoolean(false);
      Logging.logInfo("Hub room (entrances and boss rooms) will have high ceiling? " + bigHubs);
      allHuge = config.get("General", "AllHuge", false, "If true all the dungeons will be in the huge catagory.").getBoolean(false);
      Logging.logInfo("All dungeon will be huge? " + allHuge);
      singleEntrance = config.get("General", "SingleEntrances", true, "If true all dungeons will exactly one entrance (if the theme allows entrances), " + System.lineSeparator() + "like a typical dungeon crawler (Dungeons will be harder but much no more, no less, " + System.lineSeparator() + "better loot.) If false entrance are random (how the mod originally worked). ").getBoolean(true);
      Logging.logInfo("Will dungeon all have single entrances? " + singleEntrance);
      installThemes = config.get("General", "InstallThemes", true, "If true themes will automaticall be (re)installed if the themes folder is empty.").getBoolean(true);
      Logging.logInfo("Will themes be automatically install if themes folder is empty? " + installThemes);
      installCmd = config.get("General", "InstallThemesByCommand", true, "If true themes can be (re)installed by the console commands \"\\dldInstallThemes\" " + System.lineSeparator() + "and \"\\dldForceInstallThemes.\"").getBoolean(true);
      Logging.logInfo("Can themes be (re)installed by command? " + installCmd);
      thinSpawners = config.get("General", "ThinSpawners", true, "If true smaller dungeons will have some of there spawners removed to make them more like larger dungeons.").getBoolean(true);
      neverInBiomes = config.get("General", "NeverInBiomeTypes", NEVER_IN_BIOMES, "Dungeons will not generate in these biome types (uses Forge biome dictionary").getStringList();
      processBiomeExclusions(neverInBiomes);
      config.addCustomCategoryComment("API", "Turns some API calls on or off");
      disableAPI = config.get("API", "DisableApiCalls", false, "If true the API is disabled").getBoolean(false);
      Logging.logInfo("Will use API? " + !disableAPI);
      noMobChanges = config.get("API", "DontAllowApiOnMobs", false, "If false other mods that use the API can add mobs to the dungeons, if true they cannot.  " + System.lineSeparator() + "(This is good if hand designing custom themes for a mod pack.)").getBoolean(false);
      Logging.logInfo("Will allow API base mob change? " + !noMobChanges);
      config.addCustomCategoryComment("Debugging", "Things for debugging the mod; you should probably leave these all off, " + System.lineSeparator() + "some are cheats, so cause lots of lag.");
      Builder.setDebugPole(config.get("Debugging", "BuildPole", false, "CHEAT: If true all dungeons will have a giant quarts pole through the middle and be " + System.lineSeparator() + "surrounded by a boarder of floating llapis,  This is to make " + System.lineSeparator() + "them easy to find when testing.").getBoolean(false));
      MapMatrix.setDrawFlyingMap(config.get("Debugging", "BuildFlyingMap", false, "CHEAT: If true the layout of the dungeon will be built from floating glowstone (etc.) " + System.lineSeparator() + "-- on some versions it also causes *EXTREME LAG*!").getBoolean(false));
      failfast = config.get("Debugging", "FailfastLoot", false, "If true this will cause the system to crash on loot config errors." + System.lineSeparator() + "This is good for debugging modpac.").getBoolean(false);
      profile = config.get("Debugging", "AutoProfilingOn", false, "If true reports on dungeon planning and build times will be exproted to files and the commandline").getBoolean(false);
      Logging.logInfo("Will self-profile? " + profile);
      config.addCustomCategoryComment("Loot", "This will change the value and quantity of loo; be careful, too " + System.lineSeparator() + "could cause a crash if there isn't enough room in the chest.  " + System.lineSeparator() + "The basic chest formula is for the whole cheset, the treasure " + System.lineSeparator() + "chest formula is per loot normal category -- you get 3 time that " + System.lineSeparator() + "many items.");
      int a = config.getInt("A1", "Loot", 3, 0, 9, "Part of the loot quantity numbers for basic chests; " + System.lineSeparator() + " random.nextInt(A1 + (RoomDifficulty / B1)) + C1");
      int b = config.getInt("B1", "Loot", 1, 0, 9, "Part of the loot quantity numbers for basic chests; " + System.lineSeparator() + " random.nextInt(A1 + (RoomDifficulty / B1)) + C1");
      int c = config.getInt("C1", "Loot", 3, 0, 9, "Part of the loot quantity numbers for basic chests; " + System.lineSeparator() + " random.nextInt(A1 + (RoomDifficulty / B1)) + C1");
      BasicChest.setBasicLootNumbers(a, b, c);
      a = config.getInt("A2", "Loot", 2, 0, 9, "Part of the loot quantity numbers for treasure chests; " + System.lineSeparator() + " random.nextInt(A2 + (RoomDifficulty / B2)) + C2");
      b = config.getInt("B2", "Loot", 3, 0, 9, "Part of the loot quantity numbers for treasure chests; " + System.lineSeparator() + " random.nextInt(A2 + (RoomDifficulty / B2)) + C2");
      c = config.getInt("C2", "Loot", 2, 0, 9, "Part of the loot quantity numbers for treasure chests; " + System.lineSeparator() + " random.nextInt(A2 + (RoomDifficulty / B2)) + C2");
      boolean nerf = config.getBoolean("NerfEpicLoot", "Loot", false, "If true it will limit the amount of level 8 loot place with most spawners " + System.lineSeparator() + "(anything less than a full boss); enable if want to have truly epic items " + System.lineSeparator() + "like indestructable god picks) but don't want too many of them around.");
      TreasureChest.setBasicLootNumbers(a, b, c, nerf);
      Room.setLootBonus(config.getInt("Loot Bonus", "Loot", 1, -9, 9, "This modifies the value of the loot, in case you think default is " + System.lineSeparator() + "too generous or too stingy."));
      openThemesDir();
      config.save();
   }

   protected static void reload() {
      init();
   }

   public static void generateLists() {
      if (writeLists) {
         listsDir = new VFile2(configDir, "lists");
         listMobs();
         listItems();
         listBlocks();
         listBiomes();

      }
   }

   public static void listMobs() {
      Set<ResourceLocation> mobrl = EntityList.getEntityNameList();
      ArrayList<String> mobs = new ArrayList();

      for(ResourceLocation mob : mobrl) {
         mobs.add(mob.toString());
      }

      Collections.sort(mobs);
      BufferedWriter outstream = null;
      VFile2 moblist = new VFile2(listsDir, "entities.txt");

      try {
         outstream = new BufferedWriter(new OutputStreamWriter(moblist.getOutputStream(), StandardCharsets.UTF_8));

         for(String mob : mobs) {
            outstream.write(mob.toString());
            outstream.newLine();
         }

         if (outstream != null) {
            outstream.close();
         }
      } catch (IOException var6) {
         var6.printStackTrace();
      }

   }

   public static void listItems() {
      BufferedWriter outstream = null;
      VFile2 itemlist = new VFile2(listsDir, "items.txt");

      try {
         outstream = new BufferedWriter(new OutputStreamWriter(itemlist.getOutputStream(), StandardCharsets.UTF_8));

         for(Object item : Item.REGISTRY) {
            String name = ((ResourceLocation)Item.REGISTRY.getNameForObject((Item)item)).toString();
            outstream.write(name);
            outstream.newLine();
         }

         if (outstream != null) {
            outstream.close();
         }
      } catch (IOException var5) {
         System.err.println("Error: Could not write file items.txt");
         var5.printStackTrace();
      }

   }

   public static void listBlocks() {
      BufferedWriter outstream = null;
      VFile2 itemlist = new VFile2(listsDir, "blocks.txt");

      try {
         outstream = new BufferedWriter(new OutputStreamWriter(itemlist.getOutputStream(), StandardCharsets.UTF_8));

         for(Object block : Block.REGISTRY) {
            String name = ((ResourceLocation)Block.REGISTRY.getNameForObject((Block)block)).toString();
            outstream.write(name);
            outstream.newLine();
         }

         if (outstream != null) {
            outstream.close();
         }
      } catch (IOException var5) {
         System.err.println("Error: Could not write file blocks.txt");
         var5.printStackTrace();
      }

   }

   public static void listBiomes() {
      BufferedWriter outstream = null;
      VFile2 itemlist = new VFile2(listsDir, "biomes.txt");

      try {
         outstream = new BufferedWriter(new OutputStreamWriter(itemlist.getOutputStream(), StandardCharsets.UTF_8));

         for(Biome biome : ForgeRegistries.BIOMES) {
            String name = ForgeRegistries.BIOMES.getKey(biome).toString();
            outstream.write(name);
            outstream.newLine();
         }

         if (outstream != null) {
            outstream.close();
         }
      } catch (IOException var5) {
         System.err.println("Error: Could not write file blocks.txt");
         var5.printStackTrace();
      }

   }

   private static void openThemesDir() {
      String themesDirName = configDir.toString() + "/" + "themes" + "/";
      Logging.logInfo("themesdir will be set to " + themesDirName);
      themesDir = new VFile2(configDir, "themes");
      Logging.logInfo("themesdir File is be set to " + themesDir);
      ThemeReader.setThemesDir(themesDir);
      Externalizer exporter = new Externalizer(configDir.toString() + "/");
      exporter.makeChestCfg();
   }

   protected static void parseDiff(int diff) {
      switch(diff) {
      case 0:
         difficulty = Difficulty.NONE;
         break;
      case 1:
         difficulty = Difficulty.BABY;
         break;
      case 2:
         difficulty = Difficulty.NOOB;
         break;
      case 3:
      default:
         difficulty = Difficulty.NORM;
         break;
      case 4:
         difficulty = Difficulty.HARD;
         break;
      case 5:
         difficulty = Difficulty.NUTS;
      }

   }

   public static VFile2 findConfigDir(VFile2 fd) {
      VFile2 out = new VFile2(fd, "DLDungeonsJBG");
      configDir = out;
      ThemeReader.setConfigDir(out);

      return out;
   }

   private static void processBiomeExclusions(String[] array) {
      for(String str : array) {
         str = str.toUpperCase();
         Logging.logInfo("adding " + str + " to excusion list");

         try {
            Type value = Type.getType(str, new Type[0]);
            if (value != null) {
               biomeExclusions.add(value);
            }
         } catch (Exception var6) {
            System.err.println("Error in config! " + str + " is not valid biome dictionary type!");
            var6.printStackTrace();
         }
      }

   }

   public static String getConfigDir() {
      return configDir + "/";
   }

   public static void listDBlocks() {
      RegisteredBlock.listDBlocks(listsDir);
   }
}
