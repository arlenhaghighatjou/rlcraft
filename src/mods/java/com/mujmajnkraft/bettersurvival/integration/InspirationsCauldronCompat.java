package com.mujmajnkraft.bettersurvival.integration;

import knightminer.inspirations.common.Config;
import knightminer.inspirations.library.InspirationsRegistry;

public class InspirationsCauldronCompat {
   public InspirationsCauldronCompat() {
   }

   public static void initCauldronRecipes() {
      if (Config.enableCauldronPotions) {
         InspirationsRegistry.addCauldronRecipe(new PotionTippedSwordRecipe());
      }

   }

   public static boolean inspirationsExtendedCauldron() {
      return Config.enableExtendedCauldron;
   }
}
