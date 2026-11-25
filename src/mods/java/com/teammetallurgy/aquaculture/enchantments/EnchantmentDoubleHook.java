package com.teammetallurgy.aquaculture.enchantments;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.Enchantment.Rarity;

public class EnchantmentDoubleHook extends EnchantmentFishingPole {
   public EnchantmentDoubleHook(Rarity rarity) {
      super(rarity);
      this.setName("aquacultureDoubleHook");
   }

   public int getMaxLevel() {
      return 5;
   }

   public boolean canApplyTogether(Enchantment enchantment) {
      return !(enchantment instanceof EnchantmentDoubleHook) && !(enchantment instanceof EnchantmentBardedHook);
   }

   public int getMinEnchantability(int enchantmentLevel) {
      return 15;
   }

   public int getMaxEnchantability(int enchantmentLevel) {
      return super.getMinEnchantability(enchantmentLevel) + 50;
   }
}
