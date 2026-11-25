package com.teammetallurgy.aquaculture.enchantments;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.Enchantment.Rarity;

public class EnchantmentMagnetic extends EnchantmentFishingPole {
   public EnchantmentMagnetic(Rarity rarity) {
      super(rarity);
      this.setName("aquacultureMagnetic");
   }

   public int getMaxLevel() {
      return 5;
   }

   public boolean canApplyTogether(Enchantment enchantment) {
      return !(enchantment instanceof EnchantmentMagnetic) && !(enchantment instanceof EnchantmentAppealing);
   }

   public int getMinEnchantability(int enchantmentLevel) {
      return 12 + (enchantmentLevel - 1) * 20;
   }

   public int getMaxEnchantability(int enchantmentLevel) {
      return this.getMinEnchantability(enchantmentLevel) + 25;
   }
}
