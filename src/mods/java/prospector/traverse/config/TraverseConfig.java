package prospector.traverse.config;

import java.io.File;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

public class TraverseConfig {
   public static final String CATEGORY_BIOMES = "Biomes";
   public static File configDir;
   public static File mainConfig;
   public static Configuration config;
   public static TraverseConfig traverseConfiguration;
   public static boolean useVanillaWood = false;
   public static boolean registerBiomesRegardless = false;
   public static boolean disableCustomSkies = false;
   public static boolean vanillaCanyonBlocks = false;
   public static int findBiomeCommandTimeout = 30000;
   public static boolean disallowVillages = false;
   public static boolean disallowBoulders = false;
   public static boolean disableAutumnalWoods = false;
   public static boolean disableWoodlands = false;
   public static boolean disableMiniJungle = false;
   public static boolean disableMeadow = false;
   public static boolean disableLushSwamp = false;
   public static boolean disableRedDesert = false;
   public static boolean disableTemperateRainforest = false;
   public static boolean disableBadlands = false;
   public static boolean disableMountainousDesert = false;
   public static boolean disableRockyPlateau = false;
   public static boolean disableForestedHills = false;
   public static boolean disableBirchForestedHills = false;
   public static boolean disableAutumnalWoodedHills = false;
   public static boolean disableCliffs = false;
   public static boolean disableGlacier = false;
   public static boolean disableGlacierSpikes = false;
   public static boolean disableSnowyConiferousForest = false;
   public static boolean disableLushHills = false;
   public static boolean disableCanyon = true;
   public static boolean disableCragCliffs = true;
   public static boolean disableDesertShrubland = false;
   public static boolean disableThicket = false;
   public static boolean disableAridHighland = false;
   public static boolean disableRockyPlains = false;
   public static boolean enableNewTextures = false;
   private static TraverseConfig instance = null;

   private TraverseConfig() {
      config = new Configuration(mainConfig);
      config.load();
      useVanillaWood = config.get("general", "useVanillaWood", useVanillaWood, "Use vanilla logs for Traverse trees (might not look as nice)").getBoolean();
      registerBiomesRegardless = config.get("general", "registerBiomesRegardless", registerBiomesRegardless, "All biomes will always be registered, ignoring the instance traverse_world_data (WARNING: This will cause ugly world generation borders at the edge of what has previously been generated and what is new!!)").getBoolean();
      disableCustomSkies = config.get("general", "disableCustomSkies", disableCustomSkies, "When true, Traverse will only use the default sky colour.").getBoolean();
      vanillaCanyonBlocks = config.get("general", "vanillaCanyonBlocks", vanillaCanyonBlocks, "Use vanilla Red Sandstone instead of Red Rock in the Canyon biome.").getBoolean();
      findBiomeCommandTimeout = config.get("general", "findBiomeCommandTimeout", findBiomeCommandTimeout, "How long, in ms, the /findbiome command will search for biomes before timing out, normally 20000ms").getInt();
      disallowVillages = config.get("general", "disallowVillages", disallowVillages, "With this enabled, villages will not spawn in Traverse biomes").getBoolean();
      disallowBoulders = config.get("general", "disallowBoulders", disallowBoulders, "With this enabled, cobblestone boulder formations will not spawn in Traverse biomes").getBoolean();
      disableAutumnalWoods = config.get("Biomes", "disableAutumnalWoods", disableAutumnalWoods, "Force disable the Autumnal Woods biome").getBoolean();
      disableWoodlands = config.get("Biomes", "disableWoodlands", disableWoodlands, "Force disable the Woodlands biome").getBoolean();
      disableMiniJungle = config.get("Biomes", "disableMiniJungle", disableMiniJungle, "Force disable the Mini Jungle biome").getBoolean();
      disableMeadow = config.get("Biomes", "disableMeadow", disableMeadow, "Force disable the Meadow biome").getBoolean();
      disableLushSwamp = config.get("Biomes", "disableLushSwamp", disableLushSwamp, "Force disable the Lush Swamp biome").getBoolean();
      disableRedDesert = config.get("Biomes", "disableRedDesert", disableRedDesert, "Force disable the Red Desert biome").getBoolean();
      disableTemperateRainforest = config.get("Biomes", "disableTemperateRainforest", disableTemperateRainforest, "Force disable the Temperate Rainforest biome").getBoolean();
      disableBadlands = config.get("Biomes", "disableBadlands", disableBadlands, "Force disable the Badlands biome").getBoolean();
      disableMountainousDesert = config.get("Biomes", "disableMountainousDesert", disableMountainousDesert, "Force disable the Mountainous Desert biome").getBoolean();
      disableRockyPlateau = config.get("Biomes", "disableRockyPlateau", disableRockyPlateau, "Force disable the Rocky Plateau biome").getBoolean();
      disableForestedHills = config.get("Biomes", "disableForestedHills", disableForestedHills, "Force disable the Forested Hills biome").getBoolean();
      disableBirchForestedHills = config.get("Biomes", "disableBirchForestedHills", disableBirchForestedHills, "Force disable the Birch Forested Hills biome").getBoolean();
      disableAutumnalWoodedHills = config.get("Biomes", "disableAutumnalWoodedHills", disableAutumnalWoodedHills, "Force disable the Autumnal Wooded Hills biome").getBoolean();
      disableCliffs = config.get("Biomes", "disableCliffs", disableCliffs, "Force disable the Cliffs biome").getBoolean();
      disableGlacier = config.get("Biomes", "disableGlacier", disableGlacier, "Force disable the Glacier biome").getBoolean();
      disableGlacierSpikes = config.get("Biomes", "disableGlacierSpikes", disableGlacierSpikes, "Force disable the Glacier Spikes biome").getBoolean();
      disableSnowyConiferousForest = config.get("Biomes", "disableSnowyConiferousForest", disableSnowyConiferousForest, "Force disable the Snowy Coniferous Forest biome").getBoolean();
      disableLushHills = config.get("Biomes", "disableLushHills", disableLushHills, "Force disable the Lush Hills biome").getBoolean();
      disableCanyon = config.get("Biomes", "disableCanyon", disableCanyon, "Force disable the Canyon biome").getBoolean();
      disableCragCliffs = config.get("Biomes", "disableCragCliffs", disableCragCliffs, "Force disable the Crag Cliffs biome").getBoolean();
      disableDesertShrubland = config.get("Biomes", "disableDesertShrubland", disableDesertShrubland, "Force disable the Desert Shrubland biome").getBoolean();
      disableThicket = config.get("Biomes", "disableThicket", disableThicket, "Force disable the Thicket biome").getBoolean();
      disableAridHighland = config.get("Biomes", "disableAridHighland", disableAridHighland, "Force disable the Arid Highland biome").getBoolean();
      disableRockyPlains = config.get("Biomes", "disableRockyPlains", disableRockyPlains, "Force disable the Rocky Plains biome").getBoolean();
      enableNewTextures = config.get("client", "enableNewTextures", enableNewTextures, "Enable the new Traverse textures in the newer minecraft style").getBoolean();
      config.save();
   }

   public static TraverseConfig initialize() {
      if (instance == null) {
         instance = new TraverseConfig();
         return instance;
      } else {
         throw new IllegalStateException("Cannot initialize config twice");
      }
   }

   public static TraverseConfig getInstance() {
      if (instance == null) {
         throw new IllegalStateException("Instance of config requested before initialization");
      } else {
         return instance;
      }
   }

   public static void initialize(FMLPreInitializationEvent event) {
      configDir = new File(event.getModConfigurationDirectory(), "traverse");
      if (!configDir.exists()) {
         configDir.mkdir();
      }

      mainConfig = new File(configDir, "traverse.cfg");
      traverseConfiguration = initialize();
   }
}
