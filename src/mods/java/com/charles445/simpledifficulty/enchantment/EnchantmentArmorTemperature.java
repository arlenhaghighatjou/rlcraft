package com.charles445.simpledifficulty.enchantment;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnumEnchantmentType;
import net.minecraft.enchantment.Enchantment.Rarity;
import net.minecraft.inventory.EntityEquipmentSlot;

public class EnchantmentArmorTemperature extends Enchantment {
   public EnchantmentArmorTemperature() {
      super(Rarity.COMMON, EnumEnchantmentType.ARMOR, new EntityEquipmentSlot[]{EntityEquipmentSlot.CHEST, EntityEquipmentSlot.FEET, EntityEquipmentSlot.HEAD, EntityEquipmentSlot.LEGS});
   }

   protected boolean canApplyTogether(Enchantment ench) {
      return !(ench instanceof EnchantmentArmorTemperature);
   }
}
