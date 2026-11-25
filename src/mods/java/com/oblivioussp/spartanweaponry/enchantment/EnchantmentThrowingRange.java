package com.oblivioussp.spartanweaponry.enchantment;

import net.minecraft.enchantment.Enchantment.Rarity;
import net.minecraft.inventory.EntityEquipmentSlot;

public class EnchantmentThrowingRange extends EnchantmentSW {
   public EnchantmentThrowingRange(Rarity rarityIn) {
      super("propulsion", rarityIn, EnchantmentSW.TYPE_THROWING_WEAPON, new EntityEquipmentSlot[]{EntityEquipmentSlot.MAINHAND, EntityEquipmentSlot.OFFHAND});
   }

   public int getMinEnchantability(int enchantmentLevel) {
      return 10 * enchantmentLevel;
   }

   public int getMaxEnchantability(int enchantmentLevel) {
      return super.getMinEnchantability(enchantmentLevel) + 30;
   }

   public int getMaxLevel() {
      return 3;
   }
}
