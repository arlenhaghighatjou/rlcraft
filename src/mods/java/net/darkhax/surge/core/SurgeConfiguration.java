package net.darkhax.surge.core;

import java.io.File;
import net.minecraftforge.common.config.Configuration;

public class SurgeConfiguration {
   public static Configuration config;
   public static final String LOAD_TIME = "loadtime";
   public static boolean fastPrefixChecking;
   public static boolean checkForAnimatedModels;
   public static boolean disableAnimatedModels;
   public static boolean disableDebugSoundInfo;
   public static final String PERFORMANCE = "performance";
   public static boolean sheepDyeBlendTable;
   public static final String BUG_FIX = "bugfix";
   public static int maxRenameLength;
   public static boolean fixWallGlitch = true;
   public static final String MISC = "misc";
   public static boolean showTotalLoadtime;

   public SurgeConfiguration() {
   }

   public static void init(File file) {
      if (config == null) {
         config = new Configuration(file);
         syncConfigData();
      }

   }

   public static void syncConfigData() {
      fastPrefixChecking = config.getBoolean("fastPrefixChecking", "loadtime", true, "Optimizes Forge's id prefix checking. Also removes prefix warnings which significantly impact load time in large quantities.");
      checkForAnimatedModels = config.getBoolean("checkForAnimatedModels", "loadtime", true, "Improves model load times, by checking if an animated model exists before trying to load it.");
      disableAnimatedModels = config.getBoolean("disableAnimatedModels", "loadtime", false, "Improves model load times by completely removing Forge's animated models. This is a faster version of checkForAnimatedModels");
      disableDebugSoundInfo = config.getBoolean("disableDebugSoundInfo", "loadtime", true, "Improves sound loading times by removing debug code for missing sounds and missing subtitles.");
      sheepDyeBlendTable = config.getBoolean("sheepDyeBlendTable", "performance", true, "Replace sheep breeding to check a predefined table rather than querying the recipe registry.");
      maxRenameLength = config.getInt("maxRenameLength", "bugfix", 256, 32, 1024, "The maximum number of characters that can be entered in an anvil.");
      fixWallGlitch = config.getBoolean("fixMobsWallGlitching", "bugfix", true, "Attempts to fix MC-2025 by saving entity AABB to NBT.");
      showTotalLoadtime = config.getBoolean("showTotalLoadTime", "misc", true, "If true, the total load time will be printed in the console.");
      if (config.hasChanged()) {
         config.save();
      }

   }
}
