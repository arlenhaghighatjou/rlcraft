package com.charles445.simpledifficulty.item;

import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemArmor.ArmorMaterial;

public class ItemArmorTemperature extends ItemArmor {
   public ItemArmorTemperature(ArmorMaterial material, EntityEquipmentSlot equipmentSlot) {
      super(material, 0, equipmentSlot);
   }

   public boolean hasOverlay(ItemStack stack) {
      return false;
   }
}
