package com.Shultrea.Rin.Enchantment_Base_Sector;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnumEnchantmentType;
import net.minecraft.enchantment.Enchantment.Rarity;
import net.minecraft.inventory.EntityEquipmentSlot;

public class EnchantmentRareWeaponBase extends Enchantment {
   public EnchantmentRareWeaponBase() {
      super(Rarity.RARE, EnumEnchantmentType.WEAPON, new EntityEquipmentSlot[]{EntityEquipmentSlot.MAINHAND});
   }

   public int getMaxLevel() {
      return 5;
   }

   public int getMinEnchantability(int par1) {
      return 16 + 14 * (par1 - 1);
   }

   public int getMaxEnchantability(int par1) {
      return super.getMinEnchantability(par1) + 40;
   }
}
