package com.oblivioussp.spartanshields.item;

import com.google.common.collect.Multimap;
import net.lax1dude.eaglercraft.v1_8.EaglercraftUUID;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Item.ToolMaterial;
import net.minecraftforge.oredict.OreDictionary;

public class ItemShieldObsidian extends ItemShieldBasic {
   private static final String ORE_DICT_REPAIR = "obsidian";

   public ItemShieldObsidian(String unlocName, int maxDamage, ToolMaterial toolMaterial) {
      super(unlocName, maxDamage, toolMaterial);
   }

   public Multimap<String, AttributeModifier> getAttributeModifiers(EntityEquipmentSlot slot, ItemStack stack) {
      Multimap<String, AttributeModifier> modifiers = super.getAttributeModifiers(slot, stack);
      if (slot == EntityEquipmentSlot.MAINHAND || slot == EntityEquipmentSlot.OFFHAND) {
         modifiers.put(SharedMonsterAttributes.MOVEMENT_SPEED.getName(), new AttributeModifier(EaglercraftUUID.fromString("c0d49eb1-c7be-4509-a6c8-20c7ee186aa3"), "spartanshields:shieldMovementSpeed", -0.3D, 2));
         modifiers.put(SharedMonsterAttributes.KNOCKBACK_RESISTANCE.getName(), new AttributeModifier(EaglercraftUUID.fromString("045b59bd-7746-4be1-8ae2-25b67fdc6b46"), "spartanshields:shieldMovementKnockback", 0.5D, 0));
      }

      return modifiers;
   }

   public boolean getIsRepairable(ItemStack toRepair, ItemStack repair) {
      return OreDictionary.containsMatch(false, OreDictionary.getOres("obsidian"), new ItemStack[]{repair});
   }
}
