package com.github.fnar.roguelike.loot.special.tools;

import com.github.fnar.minecraft.item.RldItem;
import com.github.fnar.minecraft.item.ToolType;
import greymerk.roguelike.treasure.loot.Quality;
import net.lax1dude.eaglercraft.v1_8.EaglercraftRandom;

public class SpecialAxe extends SpecialTool {
   public SpecialAxe(EaglercraftRandom random, Quality quality) {
      this.withQuality(quality);
      if (quality == Quality.IRON) {
         this.withName("Woodland Hatchet");
      } else {
         this.withName(quality.getDescriptor() + " Axe");
      }

      this.withRldItem(this.getItem());
      this.withToolEnchantments(random);
      this.withCommonEnchantments(random);
   }

   private RldItem getItem() {
      return ToolType.AXE.asItem().withQuality(this.getQuality());
   }
}
