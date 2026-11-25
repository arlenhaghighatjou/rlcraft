package com.github.alexthe666.iceandfire.integration;

import net.minecraftforge.fml.common.Loader;

public class ThaumcraftCompatBridge {
   private static final String TC_MOD_ID = "thaumcraft";

   public ThaumcraftCompatBridge() {
   }

   public static void loadThaumcraftCompat() {
      if (Loader.isModLoaded("thaumcraft")) {
         ThaumcraftCompat.register();
      }

   }
}
