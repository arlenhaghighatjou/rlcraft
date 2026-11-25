package com.github.fnar.minecraft.item.mapper;

import com.github.fnar.minecraft.item.DesignPattern;
import net.minecraft.tileentity.BannerPattern;

public class DesignMapper1_12 {
   public DesignMapper1_12() {
   }

   public BannerPattern map(DesignPattern designPattern) {
      try {
         return BannerPattern.valueOf(designPattern.toString());
      } catch (IllegalArgumentException var3) {
         return BannerPattern.BASE;
      }
   }
}
