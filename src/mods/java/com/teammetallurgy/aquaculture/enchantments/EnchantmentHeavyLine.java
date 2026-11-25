package com.teammetallurgy.aquaculture.enchantments;

import net.minecraft.enchantment.Enchantment.Rarity;

public class EnchantmentHeavyLine extends EnchantmentFishingPole {
   public EnchantmentHeavyLine(Rarity rarity) {
      super(rarity);
      this.setName("aquacultureHeavyLine");
   }

   public int getMaxLevel() {
      return 5;
   }

   public int getMinEnchantability(int enchantmentLevel) {
      return 5 + 20 * (enchantmentLevel - 1);
   }

   public int getMaxEnchantability(int enchantmentLevel) {
      return super.getMinEnchantability(enchantmentLevel) + 50;
   }
}
