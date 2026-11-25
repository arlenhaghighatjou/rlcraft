package com.teammetallurgy.aquaculture.enchantments;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.Enchantment.Rarity;

public class EnchantmentLongCast extends EnchantmentFishingPole {
   public EnchantmentLongCast(Rarity rarity) {
      super(rarity);
      this.setName("aquacultureLongCast");
   }

   public int getMaxLevel() {
      return 5;
   }

   public boolean canApplyTogether(Enchantment enchantment) {
      return !(enchantment instanceof EnchantmentShortCast) && !(enchantment instanceof EnchantmentLongCast);
   }

   public int getMinEnchantability(int enchantmentLevel) {
      return 1 + 10 * (enchantmentLevel - 1);
   }

   public int getMaxEnchantability(int enchantmentLevel) {
      return super.getMinEnchantability(enchantmentLevel) + 50;
   }
}
