package lykrast.defiledlands.common.util;

import java.util.function.Supplier;
import java.util.HashMap;
import java.util.Map.Entry;
import lykrast.defiledlands.common.compat.ModCompat;
import lykrast.defiledlands.core.CommonProxy;
import lykrast.defiledlands.core.DefiledLands;
import net.minecraftforge.common.config.Configuration;

public class Config {
   private static final String CATEGORY_GENERAL = "General";
   private static final String CATEGORY_MOBS = "Mobs";
   private static final String CATEGORY_BOOK_WYRM = "Book Wyrms";
   private static final String CATEGORY_WORLD = "World";
   private static final String CATEGORY_COMPAT = "Compatibility";
   public static boolean confinedSpread;
   public static boolean scuttlerSpawnInLight;
   public static int weightShambler;
   public static int weightTwistedShambler;
   public static int weightScuttler;
   public static int weightHost;
   public static boolean bookWyrmSpawn;
   public static float conversionRate;
   public static int weightDesertDefiled;
   public static int weightPlainsDefiled;
   public static int weightForestTenebra;
   public static int weightForestVilespine;
   public static int weightHillsDefiled;
   public static int weightSwampDefiled;
   public static int weightIcePlainsDefiled;
   public static HashMap<String, Boolean> compatEnabled = new HashMap();

   public Config() {
   }

   public static void readConfig() {
      Configuration cfg = CommonProxy.config;

      try {
         cfg.load();
         initGeneralConfig(cfg);
      } catch (Exception var5) {
         DefiledLands.LOGGER.warn("Problem loading config file!", var5);
      } finally {
         if (cfg.hasChanged()) {
            cfg.save();
         }

      }

   }

   private static void initGeneralConfig(Configuration cfg) {
      cfg.addCustomCategoryComment("General", "General configuration");
      cfg.addCustomCategoryComment("Mobs", "Mobs");
      cfg.addCustomCategoryComment("Book Wyrms", "Book Wyrms");
      cfg.addCustomCategoryComment("World", "World generation");
      cfg.addCustomCategoryComment("Compatibility", "Mod Compatibility");
      confinedSpread = cfg.getBoolean("confinedSpread", "General", true, "If true, defiled blocks won't spread corruption when outside defiled biomes");
      scuttlerSpawnInLight = cfg.getBoolean("scuttlerSpawnInLight", "Mobs", true, "If true, Scuttlers will also spawn in lit places");
      weightShambler = cfg.getInt("weightShambler", "Mobs", 80, 0, 1000, "Weight of Shamblers when spawning with greater weight meaning more common, 0 prevents spawning; this is only relevant in relation to other defiled mobs");
      weightTwistedShambler = cfg.getInt("weightTwistedShambler", "Mobs", 80, 0, 1000, "Weight of Twisted Shamblers when spawning with greater weight meaning more common, 0 prevents spawning; this is only relevant in relation to other defiled mobs");
      weightScuttler = cfg.getInt("weightScuttler", "Mobs", 100, 0, 1000, "Weight of Scuttlers when spawning with greater weight meaning more common, 0 prevents spawning; this is only relevant in relation to other defiled mobs");
      weightHost = cfg.getInt("weightHost", "Mobs", 100, 0, 1000, "Weight of Hosts when spawning with greater weight meaning more common, 0 prevents spawning; this is only relevant in relation to other defiled mobs");
      bookWyrmSpawn = cfg.getBoolean("bookWyrmSpawn", "Book Wyrms", true, "Can Book Wyrms naturally spawn");
      conversionRate = cfg.getFloat("conversionRate", "Book Wyrms", 1.0F, 0.01F, 10.0F, "Multiplier applied to enchanted books's level fed to Book Wyrms, highly recommended to keep below 1");
      weightDesertDefiled = cfg.getInt("weightDesertDefiled", "World", 3, 0, 100, "Weight of Defiled Deserts in generation with greater weight meaning more common, 10 is most vanilla biomes, 0 prevents generation");
      weightPlainsDefiled = cfg.getInt("weightPlainsDefiled", "World", 3, 0, 100, "Weight of Defiled Plains in generation with greater weight meaning more common, 10 is most vanilla biomes, 0 prevents generation");
      weightForestTenebra = cfg.getInt("weightForestTenebra", "World", 3, 0, 100, "Weight of Tenebra Forests in generation with greater weight meaning more common, 10 is most vanilla biomes, 0 prevents generation");
      weightForestVilespine = cfg.getInt("weightForestVilespine", "World", 3, 0, 100, "Weight of Vilespine Forests in generation with greater weight meaning more common, 10 is most vanilla biomes, 0 prevents generation");
      weightHillsDefiled = cfg.getInt("weightHillsDefiled", "World", 3, 0, 100, "Weight of Defiled Hills in generation with greater weight meaning more common, 10 is most vanilla biomes, 0 prevents generation");
      weightSwampDefiled = cfg.getInt("weightSwampDefiled", "World", 3, 0, 100, "Weight of Defiled Swamps in generation with greater weight meaning more common, 10 is most vanilla biomes, 0 prevents generation");
      weightIcePlainsDefiled = cfg.getInt("weightIcePlainsDefiled", "World", 3, 0, 100, "Weight of Defiled Ice Plains in generation with greater weight meaning more common, 10 is most vanilla biomes, 0 prevents generation");

      for(Entry<String, Supplier<? extends ModCompat>> e : ModCompat.compat.entrySet()) {
         compatEnabled.put(e.getKey(), Boolean.valueOf(cfg.getBoolean((String)e.getKey(), "Compatibility", true, "Enables compatibility with " + (String)e.getKey())));
      }

   }
}
