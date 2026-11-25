package com.oblivioussp.spartanweaponry.enchantment;

import net.minecraft.enchantment.Enchantment.Rarity;
import net.minecraft.inventory.EntityEquipmentSlot;

public class EnchantmentCrossbowSpreadshot extends EnchantmentSW {
   public EnchantmentCrossbowSpreadshot(Rarity rarityIn) {
      super("spreadshot", rarityIn, TYPE_CROSSBOW, new EntityEquipmentSlot[]{EntityEquipmentSlot.MAINHAND, EntityEquipmentSlot.OFFHAND});
   }

   public int getMinEnchantability(int enchantmentLevel) {
      return 20;
   }

   public int getMaxEnchantability(int enchantmentLevel) {
      return 50;
   }

   public int getMaxLevel() {
      return 1;
   }
}
