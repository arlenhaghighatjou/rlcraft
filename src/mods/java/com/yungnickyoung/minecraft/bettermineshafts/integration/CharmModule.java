package com.yungnickyoung.minecraft.bettermineshafts.integration;

import com.yungnickyoung.minecraft.bettermineshafts.config.Configuration;
import svenhjol.charm.crafting.block.BlockLantern;
import svenhjol.charm.crafting.feature.Lantern;

public class CharmModule extends CompatModule {
   public CharmModule() {
      super("charm");
   }

   public void enable() {
      super.enable();
      if (Lantern.ironLantern != null) {
         this.addIfAbsent(this.lanterns, Lantern.ironLantern.getDefaultState().withProperty(BlockLantern.HANGING, Boolean.valueOf(true)));
      }

      if (Lantern.goldLantern != null) {
         this.addIfAbsent(this.lanterns, Lantern.goldLantern.getDefaultState().withProperty(BlockLantern.HANGING, Boolean.valueOf(true)));
      }

   }

   public boolean shouldBeEnabled() {
      return super.shouldBeEnabled() && Configuration.modCompat.charmLanternsEnabled;
   }
}
