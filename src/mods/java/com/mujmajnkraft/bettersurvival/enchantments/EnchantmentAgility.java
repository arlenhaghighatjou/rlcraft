package com.mujmajnkraft.bettersurvival.enchantments;

import com.mujmajnkraft.bettersurvival.config.ForgeConfigHandler;
import net.lax1dude.eaglercraft.v1_8.EaglercraftUUID;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnumEnchantmentType;
import net.minecraft.enchantment.Enchantment.Rarity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.inventory.EntityEquipmentSlot;

public class EnchantmentAgility extends Enchantment {
   public static final EaglercraftUUID speedModifier = EaglercraftUUID.fromString("e6107045-134f-4c54-a645-75c3ae5c7a27");

   public EnchantmentAgility() {
      super(Rarity.RARE, EnumEnchantmentType.ARMOR_LEGS, new EntityEquipmentSlot[]{EntityEquipmentSlot.LEGS});
      this.setRegistryName("agility");
      this.setName("mujmajnkraftsbettersurvival.agility");
   }

   public static void applySpeedModifier(EntityLivingBase entity, int level) {
      if (level > 0) {
         double d = 0.01D * (double)level;
         AttributeModifier modifier = new AttributeModifier(speedModifier, "agility", d, 0);
         IAttributeInstance speed = entity.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED);
         if (!speed.hasModifier(modifier)) {
            speed.applyModifier(modifier);
         } else if (speed.getModifier(speedModifier).getAmount() != d) {
            speed.removeModifier(speedModifier);
            speed.applyModifier(modifier);
         }
      }

   }

   public int getMaxLevel() {
      return ForgeConfigHandler.enchantments.agilityLevel;
   }

   public int getMinEnchantability(int enchantmentLevel) {
      return 10 + 20 * (enchantmentLevel - 1);
   }

   public int getMaxEnchantability(int enchantmentLevel) {
      return super.getMinEnchantability(enchantmentLevel) + 50;
   }

   public boolean isTreasureEnchantment() {
      return ForgeConfigHandler.enchantments.agilityTreasure;
   }

   public boolean isAllowedOnBooks() {
      return ForgeConfigHandler.enchantments.agilityLevel != 0;
   }
}
