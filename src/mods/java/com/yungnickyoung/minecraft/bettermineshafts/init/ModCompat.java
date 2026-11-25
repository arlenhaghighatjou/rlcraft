package com.yungnickyoung.minecraft.bettermineshafts.init;

import com.yungnickyoung.minecraft.bettermineshafts.integration.Integrations;

public class ModCompat {
   public ModCompat() {
   }

   public static void postInit() {
      Integrations.update();
   }
}
