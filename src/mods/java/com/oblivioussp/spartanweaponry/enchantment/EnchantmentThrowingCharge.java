package com.oblivioussp.spartanweaponry.enchantment;

import net.minecraft.enchantment.Enchantment.Rarity;
import net.minecraft.inventory.EntityEquipmentSlot;

public class EnchantmentThrowingCharge extends EnchantmentSW {
   public EnchantmentThrowingCharge(Rarity rarityIn) {
      super("supercharge", rarityIn, EnchantmentSW.TYPE_THROWING_WEAPON, new EntityEquipmentSlot[]{EntityEquipmentSlot.MAINHAND, EntityEquipmentSlot.OFFHAND});
   }

   public int getMinEnchantability(int enchantmentLevel) {
      return enchantmentLevel * 15;
   }

   public int getMaxEnchantability(int enchantmentLevel) {
      return this.getMinEnchantability(enchantmentLevel) + 10;
   }

   public int getMaxLevel() {
      return 2;
   }
}
