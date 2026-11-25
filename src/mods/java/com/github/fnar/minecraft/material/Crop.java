package com.github.fnar.minecraft.material;

import com.github.fnar.minecraft.block.decorative.CropBlock;
import com.github.fnar.minecraft.item.Seed;
import net.lax1dude.eaglercraft.v1_8.EaglercraftRandom;

public enum Crop {
   BEETROOTS,
   CARROTS,
   COCOA,
   MELON,
   NETHER_WART,
   POTATOES,
   PUMPKIN,
   WHEAT;

   private Crop() {
   }

   public Seed asSeed() {
      return new Seed(this);
   }

   public CropBlock getBrush() {
      return CropBlock.crop().setCrop(this);
   }

   public static Crop chooseOneAtRandom(EaglercraftRandom random) {
      Crop[] crops = values();
      return crops[random.nextInt(crops.length)];
   }
}
