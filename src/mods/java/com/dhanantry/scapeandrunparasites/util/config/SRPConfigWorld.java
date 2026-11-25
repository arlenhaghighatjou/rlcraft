package com.dhanantry.scapeandrunparasites.util.config;

import com.dhanantry.scapeandrunparasites.SRPMain;
import com.dhanantry.scapeandrunparasites.proxy.CommonProxy;
import java.io.File;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.lax1dude.eaglercraft.v1_8.log4j.Level;

public class SRPConfigWorld {
   private static final String CATEGORY_GENERAL_WORLD = "configuration_world";
   private static final String WORLDBIOME_CATEGORY = "world_biome";
   public static int biomeweight = 0;
   public static boolean biomeRegster = true;
   public static boolean nodesActivated = true;
   public static int maximumNumberNodes = 20;
   public static int minimumDistanceBetweenNodes = 10000;
   public static int minimumDistanceFromSpawnPoint = 1;
   public static int[] blackListedDimensionsNodes = new int[]{0, 1, -1};
   public static String[] blockBBiomeList = new String[0];
   public static boolean blockBBiomeListWhite = false;
   public static float biomeBlockIMaxH = 5.0F;
   public static String nodeWarning = "Node";
   public static boolean venkrolNode = true;
   public static int biomeSkyColor = 3080192;
   public static int biomeGrassColor = 10878976;
   public static int biomeFoliageColor = 10878976;
   public static int biomeWaterColor = 10878976;
   public static float biomeFogDensity = 0.06F;
   public static float biomeFogRed = 38.0F;
   public static float biomeFogBlue = 36.0F;
   public static float biomeFogGreen = 36.0F;
   public static float biomeHealPenalty = 0.5F;
   public static String[] biomeHealPenaltyBlackList = new String[]{"minecraft:villager_golem"};
   public static boolean biomeHealPenaltyBlackListWhite = false;
   public static double biomeTree = 0.0015D;
   public static double biomeFlower = 0.001D;
   public static double biomeSpine = 5.0E-4D;
   public static double biomeTenflower = 1.0E-4D;
   public static double biomeBall = 7.0E-5D;
   public static double biomeBigBall = 2.0E-5D;
   public static double biomeMouth = 3.0E-4D;
   public static int biomeHeartFreq = 30;
   public static float biomeHeartVol = 0.4F;
   public static String[] biomeSpawnEntry = new String[]{"srparasites:sim_bigspider;1;1;60;0", "srparasites:sim_human;1;1;60;0", "srparasites:sim_cow;1;1;60;0", "srparasites:sim_sheep;1;1;60;0", "srparasites:sim_wolf;1;1;60;0", "srparasites:sim_pig;1;1;60;0", "srparasites:sim_villager;1;1;60;0", "srparasites:sim_adventurer;1;1;60;0", "srparasites:sim_horse;3;1;60;0", "srparasites:sim_bear;2;1;60;0", "srparasites:sim_enderman;1;1;60;0", "srparasites:pri_summoner;1;1;10;0", "srparasites:pri_longarms;1;1;10;0", "srparasites:pri_reeker;1;1;10;0", "srparasites:pri_manducater;1;1;10;0", "srparasites:pri_bolster;1;1;10;0", "srparasites:pri_yelloweye;1;1;10;0", "srparasites:pri_arachnida;1;1;10;0", "srparasites:ada_summoner;1;1;10;0", "srparasites:ada_longarms;1;1;10;0", "srparasites:ada_reeker;1;1;10;0", "srparasites:ada_manducater;1;1;10;0", "srparasites:ada_bolster;1;1;10;0", "srparasites:ada_yelloweye;1;1;10;0", "srparasites:ada_arachnida;1;1;10;0", "srparasites:overseer;2;4;5;0", "srparasites:vigilante;1;3;5;0", "srparasites:warden;1;1;5;0", "srparasites:marauder;2;3;5;0", "srparasites:bomber_light;2;5;5;0", "srparasites:monarch;2;5;5;0", "srparasites:grunt;1;1;20;0", "srparasites:bomber_heavy;1;1;5;0", "srparasites:wraith;2;2;5;0", "srparasites:bogle;1;1;5;0", "srparasites:haunter;1;1;5;0", "srparasites:carrier_colony;1;1;5;0", "srparasites:worker;1;1;10;0"};
   public static String[] biomeBlackList = new String[0];
   public static boolean biomeBlackListInverted = false;
   public static String[] potionEffectForNodes = new String[]{"3;minecraft:speed;2", "4;minecraft:fire_resistance;1", "7;minecraft:invisibility;1"};
   public static int nodeRangeSpreadOne = 200;
   public static int nodeRangeSpreadTwo = 800;
   public static int nodeRangeSpreadThree = 4000;
   public static int nodeRangeEffectsOne = 400;
   public static int nodeRangeEffectsTwo = 1600;
   public static int nodeRangeEffectsThree = 8000;
   public static float nodeCropStopNodeOne = 0.3F;
   public static float nodeCropStopNodeTwo = 0.6F;
   public static float nodeCropStopNodeThree = 1.0F;
   public static int timeNeedeToNodeOne = 10;
   public static int timeNeedeToNodeTwo = 30;
   public static int timeNeedeToNodeThree = 60;
   private static final String WORLDCOLONY_CATEGORY = "world_colony";
   public static boolean coloniesActivated = false;
   public static int maximumNumberColonies = 20;
   public static int minimumDistanceBetweenColonies = 2000;
   public static int[] blackListedDimensionsColonies = new int[]{0, 1, -1};
   public static int colonySpreadPoint = 2;
   public static int colonySpreadValue = 20;
   public static int colonyBaseRadiusValue = 60;
   public static int colonySpreadEffectPoint = 1;
   public static int colonySpreadEffectValue = 40;
   public static int colonyBaseEffectRadiusValue = 120;
   public static int colonyPointCap = 100;
   public static int colonyTotalPointCap = 100;
   public static String colonyWarning = "Colony";
   public static float colonyExtraHealthPoint = 20.0F;
   public static float colonyExtraHealthValue = 0.1F;
   public static float colonyExtraArmorPoint = 20.0F;
   public static float colonyExtraArmorValue = 0.1F;
   public static float colonyExtraKDResPoint = 20.0F;
   public static float colonyExtraKDResValue = 0.1F;
   public static float colonyExtraDamagePoint = 20.0F;
   public static float colonyExtraDamageValue = 0.1F;
   public static float colonyDamageCapPoint = 15.0F;
   public static float colonyDamageCapValue = 0.5F;
   public static float colonyPointReductionPoint = 10.0F;
   public static float colonyPointReductionValue = 0.05F;
   public static float colonyPointBoostPoint = 10.0F;
   public static float colonyPointBoostValue = 0.05F;
   public static boolean dodColony = true;
   public static String[] preeValues = new String[]{"65;1", "85;1", "86;1", "87;1", "88;1", "89;1"};

   public SRPConfigWorld() {
   }

   private static void initGeneralWorldConfig(Configuration cfg) {
      String u = "";
      cfg.addCustomCategoryComment("configuration_world", "World configuration \nVersion:1.9.11\n \nBlocks IDs \n \n \n \n \n \n \n \n \n ");
   }

   private static void initworldbiomeConfig(Configuration cfg) {
      String description = " \n A Node is created when an infested area has been laying around for some time, a node\n will change the biome and will have bonuses to the parasites.";
      String entry = " Ex. \"minecraft:zombie;1;3;90;0\"  Where: \n \"minecraft:zombie\" is for the entity, \n \"1\" is the minimum group count. \n \"3\" is the maximum group count. \n \"90\" is spawn weight. \n \"0\" is the type (0 for entityType.MONSTER, 1 for entityType.CREATURE). \n";
      String entry2 = " Ex. \"2;minecraft:speed;3\"  Where: \n \"2\" is the required total of node levels, \n \"minecraft:speed\" is the potion itself (the potion will apply regardless of whether the parasite is near a node or not), \n \"3\" is the amplifier of the effect ";
      cfg.addCustomCategoryComment("world_biome", "World Biome" + description);
      biomeRegster = cfg.getBoolean("Biome Acivated", "world_biome", biomeRegster, "Set to false if you dont want to register the biome into your game (Needed for this category to work).");
      nodesActivated = cfg.getBoolean("Creation of Nodes", "world_biome", nodesActivated, "Set to false if you dont want Nodes to be created in your world.");
      maximumNumberNodes = cfg.getInt("Maximum Node Number", "world_biome", maximumNumberNodes, 1, 100, "Maximum number of nodes in a world.");
      minimumDistanceBetweenNodes = cfg.getInt("Minimum Distance Between Nodes", "world_biome", minimumDistanceBetweenNodes, 0, 100000000, "Minimum distance that one node needs to be away from the other to appear.");
      minimumDistanceFromSpawnPoint = cfg.getInt("Minimum Distance From SpawnPoint", "world_biome", minimumDistanceFromSpawnPoint, 0, 100000000, "Minimum distance one node needs to be away from SpawnPoint to appear.");
      blackListedDimensionsNodes = cfg.get("world_biome", "WhiteList Dimensions", blackListedDimensionsNodes, "Nodes can only be made in these dimensions").getIntList();
      blockBBiomeList = cfg.getStringList("Biome Blocks BlackList", "world_biome", blockBBiomeList, "List of block that can't be infested, Ex: \"minecraft:stonebrick\" or just \"minecraft\" for a whole mod");
      blockBBiomeListWhite = cfg.getBoolean("Biome Blocks BlackList Inverted", "world_biome", blockBBiomeListWhite, "Set to true if you want to use the list as a WhiteList.");
      biomeBlockIMaxH = cfg.getFloat("Biome Hardness", "world_biome", biomeBlockIMaxH, 0.01F, 100.0F, "Max Hardness of the block it can spread into.");
      nodeWarning = cfg.getString("Node Warning Message", "world_biome", nodeWarning, "Message sent to all players in the current world when a Node is placed");
      biomeSkyColor = cfg.getInt("Biome Color Sky", "world_biome", biomeSkyColor, 0, 2147483640, "Decimal color code for the Sky of the Biome.");
      biomeGrassColor = cfg.getInt("Biome Color Grass", "world_biome", biomeGrassColor, 0, 2147483640, "Decimal color code for the Grass of the Biome.");
      biomeFoliageColor = cfg.getInt("Biome Color Foliage", "world_biome", biomeFoliageColor, 0, 2147483640, "Decimal color code for the Foliage of the Biome.");
      biomeWaterColor = cfg.getInt("Biome Color Water", "world_biome", biomeWaterColor, 0, 2147483640, "Decimal color code for the Water of the Biome.");
      biomeFogDensity = cfg.getFloat("Biome Fog Density", "world_biome", biomeFogDensity, 0.0F, 1.0F, "Amount of fog the biome will have.");
      biomeFogRed = cfg.getFloat("Biome Fog Color Red Value", "world_biome", biomeFogRed, 0.0F, 255.0F, "Red color value for the fog of the Biome.");
      biomeFogGreen = cfg.getFloat("Biome Fog Color Green Value", "world_biome", biomeFogGreen, 0.0F, 255.0F, "Green color value for the fog of the Biome.");
      biomeFogBlue = cfg.getFloat("Biome Fog Color Blue Value", "world_biome", biomeFogBlue, 0.0F, 255.0F, "Blue color value for the fog of the Biome.");
      biomeSpawnEntry = cfg.getStringList("Biome Spawn Entity List", "world_biome", biomeSpawnEntry, "Entity List that will spawn at the Biome." + entry);
      biomeHealPenalty = cfg.getFloat("Biome Heal Penalty", "world_biome", biomeHealPenalty, 0.0F, 1.0F, "Amount of health reduced (1=100%) for mobs and player healing in the biome.");
      biomeHealPenaltyBlackList = cfg.getStringList("Biome Heal Penalty BlackList", "world_biome", biomeHealPenaltyBlackList, "Mobs that are immune to this penalty. Ex: \"minecraft:zombie\" or just \"minecraft\" for a whole mod.");
      biomeHealPenaltyBlackListWhite = cfg.getBoolean("Biome Heal Penalty BlackList Inverted", "world_biome", biomeHealPenaltyBlackListWhite, "Set to true if you want to use the list as a WhiteList.");
      potionEffectForNodes = cfg.getStringList("Node Potion Effect List", "world_biome", potionEffectForNodes, "List of potion effects that the parasites will spawn with." + entry2);
      biomeweight = cfg.getInt("Biome Weight", "world_biome", biomeweight, 0, 10000, "Biome weight, registered as a SPOOKY type biome");
      biomeBlackList = cfg.getStringList("Biome Spread BlackList", "world_biome", biomeBlackList, "List of biomes that cannot be converted. Ex: minecraft:plains or just minecraft");
      biomeBlackListInverted = cfg.getBoolean("Biome Spread BlackList Inverted", "world_biome", biomeBlackListInverted, "\"Set to true if you want to use the list as a WhiteList.");
      biomeTree = (double)cfg.getFloat("Biome Tree chance", "world_biome", (float)biomeTree, 0.0F, 1.0F, "(1 = 100%) Generation chance for tree feature.");
      biomeFlower = (double)cfg.getFloat("Biome Flower chance", "world_biome", (float)biomeFlower, 0.0F, 1.0F, "(1 = 100%) Generation chance for flower feature.");
      biomeSpine = (double)cfg.getFloat("Biome Spine chance", "world_biome", (float)biomeSpine, 0.0F, 1.0F, "(1 = 100%) Generation chance for spine feature.");
      biomeTenflower = (double)cfg.getFloat("Biome Tenflower chance", "world_biome", (float)biomeTenflower, 0.0F, 1.0F, "(1 = 100%) Generation chance for tenflower feature.");
      biomeBall = (double)cfg.getFloat("Biome Ball chance", "world_biome", (float)biomeBall, 0.0F, 1.0F, "(1 = 100%) Generation chance for ball feature.");
      biomeBigBall = (double)cfg.getFloat("Biome Bigball chance", "world_biome", (float)biomeBigBall, 0.0F, 1.0F, "(1 = 100%) Generation chance for bigball feature.");
      biomeMouth = (double)cfg.getFloat("Biome Mouth chance", "world_biome", (float)biomeMouth, 0.0F, 1.0F, "(1 = 100%) Generation chance for mouth feature.");
      biomeHeartVol = cfg.getFloat("Biome Node Heart Volume", "world_biome", biomeHeartVol, 0.0F, 1000.0F, "Volume of the Core Block Node Sound.");
      biomeHeartFreq = cfg.getInt("Biome Node Heart Frequency", "world_biome", biomeHeartFreq, 0, 100, "Frequency of the Core Block Node Sound.");
      venkrolNode = cfg.getBoolean("Nodes Beckons", "world_biome", venkrolNode, "Set to false if you dont want Colonies to be created by a Beckon SIV. (This option is ignored if Evolution Phases are enabled, it has its own option)");
      nodeRangeSpreadOne = cfg.getInt("Node 1 Spread Range", "world_biome", nodeRangeSpreadOne, 0, 2147483640, "Spread range of a Node 1.");
      nodeRangeEffectsOne = cfg.getInt("Node 1 Effect Range", "world_biome", nodeRangeEffectsOne, 0, 2147483640, "Effect range of a Node 1.");
      timeNeedeToNodeOne = cfg.getInt("Node 1 Time Needed", "world_biome", timeNeedeToNodeOne, 0, 2147483640, "Days needed from a sss to a Node 1.");
      nodeRangeSpreadTwo = cfg.getInt("Node 2 Spread Range", "world_biome", nodeRangeSpreadTwo, 0, 2147483640, "Spread range of a Node 2.");
      nodeRangeEffectsTwo = cfg.getInt("Node 2 Effect Range", "world_biome", nodeRangeEffectsTwo, 0, 2147483640, "Effect range of a Node 2.");
      timeNeedeToNodeTwo = cfg.getInt("Node 2 Time Needed", "world_biome", timeNeedeToNodeTwo, 0, 2147483640, "Days needed from a Node 1 to a Node 2.");
      nodeRangeSpreadThree = cfg.getInt("Node 3 Spread Range", "world_biome", nodeRangeSpreadThree, 0, 2147483640, "Spread range of a Node 3.");
      nodeRangeEffectsThree = cfg.getInt("Node 3 Effect Range", "world_biome", nodeRangeEffectsThree, 0, 2147483640, "Effect range of a Node 3.");
      timeNeedeToNodeThree = cfg.getInt("Node 3 Time Needed", "world_biome", timeNeedeToNodeThree, 0, 2147483640, "Days needed from a Node 2 to a Node 3.");
      nodeCropStopNodeOne = cfg.getFloat("Node 1 Crop Stop", "world_biome", nodeCropStopNodeOne, 0.0F, 100.0F, "Chance (1 = 100%) to stop crops/trees from growing naturally.(for a tick) for a Node 1");
      nodeCropStopNodeTwo = cfg.getFloat("Node 2 Crop Stop", "world_biome", nodeCropStopNodeTwo, 0.0F, 100.0F, "Chance (1 = 100%) to stop crops/trees from growing naturally.(for a tick) for a Node 2");
      nodeCropStopNodeThree = cfg.getFloat("Node 3 Crop Stop", "world_biome", nodeCropStopNodeThree, 0.0F, 100.0F, "Chance (1 = 100%) to stop crops/trees from growing naturally.(for a tick) for a Node 3");
   }

   private static void initworldcolonyConfig(Configuration cfg) {
      String description = " \n A Node is created when an infested area has been laying around for some time, a node\n will change the biome and will have bonuses to the parasites.";
      String entry = " Ex. \"minecraft:zombie;1;3;90;0\"  Where: \n \"minecraft:zombie\" is for the entity, \n \"1\" is for the minimum group count. \n \"3\" is for the maximum group count. \n \"90\" is for spawn weight. \n \"0\" is for the type (0 for entityType.MONSTER, 1 for entityType.CREATURE). \n";
      String valll = " Ex. \"50:1\"  Where: \n \"50\" is the parasite second id, \n \"1\" are the points required (from colonies) to spawn. \n";
      cfg.addCustomCategoryComment("world_colony", "World Colony" + description);
      coloniesActivated = cfg.getBoolean("Colonies Activated", "world_colony", coloniesActivated, "Set to false if you dont want Colonies to be created in your world.");
      maximumNumberColonies = cfg.getInt("Maximum Colony Number", "world_colony", maximumNumberColonies, 1, 100, "Maximum number of Colonies in a world.");
      minimumDistanceBetweenColonies = cfg.getInt("Minimum Distance Between Colonies", "world_colony", minimumDistanceBetweenColonies, 0, 100000000, "Minimum distance that one Colony needs to be away from the other to appear.");
      blackListedDimensionsColonies = cfg.get("world_colony", "WhiteList Dimensions", blackListedDimensionsColonies, "Colonies can only be made in these dimensions").getIntList();
      colonyPointCap = cfg.getInt("Colonies Points Cap", "world_colony", colonyPointCap, 1, 10000, "Cap of points for a single Colony.");
      colonyTotalPointCap = cfg.getInt("Colonies Total Points Cap", "world_colony", colonyTotalPointCap, 1, 10000, "Total cap of points for all Colonies.");
      colonySpreadPoint = cfg.getInt("Colonies Spread Point", "world_colony", colonySpreadPoint, 1, 10000, "Every x points, the colony radius will get bigger.");
      colonySpreadValue = cfg.getInt("Colonies Spread Value", "world_colony", colonySpreadValue, 1, 10000, "How much the radius will grow.");
      colonyBaseRadiusValue = cfg.getInt("Colonies Base Radius Value", "world_colony", colonyBaseRadiusValue, 1, 10000, "Value of the base radius of a Colony.");
      colonySpreadEffectPoint = cfg.getInt("Colonies Spread Effect Point", "world_colony", colonySpreadEffectPoint, 1, 10000, "Every x points, the colony effect radius will get bigger.");
      colonySpreadEffectValue = cfg.getInt("Colonies Spread Effect Value", "world_colony", colonySpreadEffectValue, 1, 10000, "How much the effect radius will grow.");
      colonyBaseEffectRadiusValue = cfg.getInt("Colonies Base Effect Radius Value", "world_colony", colonyBaseEffectRadiusValue, 1, 10000, "Value of the base effect radius of a Colony.");
      colonyWarning = cfg.getString("Colonies Warning Message", "world_colony", colonyWarning, "Message sent to all players in the current world when a Colony is placed");
      dodColony = cfg.getBoolean("Colonies Dispatcher", "world_colony", dodColony, "Set to false if you dont want Colonies to be created by a Dispatcher SIV. (This option is ignored if Evolution Phases are enabled, it has its own option)");
      colonyExtraHealthPoint = cfg.getFloat("Colony Bonus Health Point", "world_colony", colonyExtraHealthPoint, 1.0F, 10000.0F, "Every x points, the colony will grant a bonus of health.");
      colonyExtraHealthValue = cfg.getFloat("Colony Bonus Health Value", "world_colony", colonyExtraHealthValue, 0.0F, 10000.0F, "Value of the bonus.");
      colonyExtraArmorPoint = cfg.getFloat("Colony Bonus Armor Point", "world_colony", colonyExtraArmorPoint, 1.0F, 10000.0F, "Every x points, the colony will grant a bonus of armor.");
      colonyExtraArmorValue = cfg.getFloat("Colony Bonus Armor Value", "world_colony", colonyExtraArmorValue, 0.0F, 10000.0F, "Value of the bonus.");
      colonyExtraDamagePoint = cfg.getFloat("Colony Bonus Damage Point", "world_colony", colonyExtraDamagePoint, 1.0F, 10000.0F, "Every x points, the colony will grant a bonus of damage.");
      colonyExtraDamageValue = cfg.getFloat("Colony Bonus Damage Value", "world_colony", colonyExtraDamageValue, 0.0F, 10000.0F, "Value of the bonus.");
      colonyExtraKDResPoint = cfg.getFloat("Colony Bonus KDR Point", "world_colony", colonyExtraKDResPoint, 1.0F, 10000.0F, "Every x points, the colony will grant a bonus of KDR.");
      colonyExtraKDResValue = cfg.getFloat("Colony Bonus KDR Value", "world_colony", colonyExtraKDResValue, 0.0F, 10000.0F, "Value of the bonus.");
      colonyDamageCapPoint = cfg.getFloat("Colony Damage Cap Point", "world_colony", colonyDamageCapPoint, 1.0F, 10000.0F, "Every x points, the colony will increase their damage cap.");
      colonyDamageCapValue = cfg.getFloat("Colony Damage Cap Value", "world_colony", colonyDamageCapValue, 0.0F, 10000.0F, "Value (in %) of the bonus.");
      colonyPointReductionPoint = cfg.getFloat("Colony EV Reduction Point", "world_colony", colonyPointReductionPoint, 1.0F, 10000.0F, "Every x points, the colony will reduce the number of points lost in the evolution phases");
      colonyPointReductionValue = cfg.getFloat("Colony EV Reduction Value", "world_colony", colonyPointReductionValue, 0.0F, 1.0F, "Value (in %) of the bonus.");
      colonyPointBoostPoint = cfg.getFloat("Colony EV Boost Point", "world_colony", colonyPointBoostPoint, 1.0F, 10000.0F, "Every x points, the colony will boost the number of points earned in the evolution phases");
      colonyPointBoostValue = cfg.getFloat("Colony EV Boost Value", "world_colony", colonyPointBoostValue, 0.0F, 1.0F, "Value (in %) of the bonus.");
      preeValues = cfg.getStringList("Colony Parasite Values", "world_colony", preeValues, "List of parasites that require Colony points to appear, parasites that are not in the list will not be affected." + valll);
   }

   public static void initConfig(FMLPreInitializationEvent e) {
      File directory = e.getModConfigurationDirectory();
      CommonProxy.configWorld = new Configuration(new File(directory.getPath(), "srparasites/SRParasitesWorld.cfg"));
      readConfig();
   }

   public static boolean readConfig() {
      Configuration cfgW = CommonProxy.configWorld;

      try {
         cfgW.load();
         initGeneralWorldConfig(cfgW);
         initworldbiomeConfig(cfgW);
         initworldcolonyConfig(cfgW);
         boolean e = true;
         return e;
      } catch (Exception var5) {
         SRPMain.logger.log(Level.ERROR, "Problem loading configuration file", var5);
      } finally {
         if (cfgW.hasChanged()) {
            cfgW.save();
         }

      }

      return false;
   }
}
