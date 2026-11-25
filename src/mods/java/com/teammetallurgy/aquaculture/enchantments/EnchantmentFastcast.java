package com.teammetallurgy.aquaculture.enchantments;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.Enchantment.Rarity;

public class EnchantmentFastcast extends EnchantmentFishingPole {
   public EnchantmentFastcast(Rarity rarity) {
      super(rarity);
      this.setName("aquacultureFastCast");
   }

   public int getMaxLevel() {
      return 5;
   }

   public boolean canApplyTogether(Enchantment enchantment) {
      return !(enchantment instanceof EnchantmentShortCast) && !(enchantment instanceof EnchantmentLongCast) && !(enchantment instanceof EnchantmentFastcast);
   }
}
