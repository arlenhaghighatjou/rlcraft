package com.teammetallurgy.aquaculture.enchantments;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.Enchantment.Rarity;

public class EnchantmentAppealing extends EnchantmentFishingPole {
   public EnchantmentAppealing(Rarity rarity) {
      super(rarity);
      this.setName("aquacultureAppealing");
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
