package net.darkhax.enchdesc.handler;

import net.lax1dude.eaglercraft.v1_8.internal.vfs2.VFile2;
import net.minecraftforge.common.config.Configuration;

public final class ConfigurationHandler {
   private static Configuration config = null;
   private static boolean exploreMode = false;

   private ConfigurationHandler() {
   }

   public static void initConfig(VFile2 file) {
      config = new Configuration(file);
      syncConfig();
   }

   public static void syncConfig() {
      setExploreMode(config.getBoolean("exploreMode", "general", false, "Should the mod generate a list of enchantments from the instance that have no description?"));
      if (config.hasChanged()) {
         config.save();
      }

   }

   public static boolean isExploreMode() {
      return exploreMode;
   }

   public static void setExploreMode(boolean exploreMode) {
      exploreMode = exploreMode;
   }
}
