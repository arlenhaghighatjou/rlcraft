package com.elenai.elenaidodge.integration;

import c4.conarm.common.armor.utils.ArmorHelper;
import c4.conarm.common.items.armor.Boots;
import c4.conarm.common.items.armor.Chestplate;
import c4.conarm.common.items.armor.Helmet;
import c4.conarm.common.items.armor.Leggings;
import c4.conarm.lib.armor.ArmorCore;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;

public class ConstructsArmory {
   public ConstructsArmory() {
   }

   public static double getWeight(EntityPlayer player, EntityEquipmentSlot slot) {
      switch(slot) {
      case HEAD:
         if (player.getItemStackFromSlot(EntityEquipmentSlot.HEAD).getItem() instanceof Helmet) {
            ItemStack item = player.getItemStackFromSlot(EntityEquipmentSlot.HEAD);
            return (double)(ArmorHelper.getDefense(item) / 8.0F) * 1.8D.doubleValue();
         }
         break;
      case CHEST:
         if (player.getItemStackFromSlot(EntityEquipmentSlot.CHEST).getItem() instanceof Chestplate) {
            ItemStack item = player.getItemStackFromSlot(EntityEquipmentSlot.CHEST);
            return (double)(ArmorHelper.getDefense(item) / 8.0F) * 1.8D.doubleValue();
         }
         break;
      case LEGS:
         if (player.getItemStackFromSlot(EntityEquipmentSlot.LEGS).getItem() instanceof Leggings) {
            ItemStack item = player.getItemStackFromSlot(EntityEquipmentSlot.LEGS);
            return (double)(ArmorHelper.getDefense(item) / 8.0F) * 1.8D.doubleValue();
         }
         break;
      case FEET:
         if (player.getItemStackFromSlot(EntityEquipmentSlot.FEET).getItem() instanceof Boots) {
            ItemStack item = player.getItemStackFromSlot(EntityEquipmentSlot.FEET);
            return (double)(ArmorHelper.getDefense(item) / 8.0F) * 1.8D.doubleValue();
         }
      }

      return 0.0D;
   }

   public static double getWeight(ItemStack item) {
      return item.getItem() instanceof ArmorCore ? (double)(ArmorHelper.getDefense(item) / 8.0F) * 1.8D.doubleValue() : 0.0D;
   }
}
