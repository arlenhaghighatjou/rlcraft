package com.mujmajnkraft.bettersurvival.enchantments;

import com.mujmajnkraft.bettersurvival.config.ForgeConfigHandler;
import com.mujmajnkraft.bettersurvival.init.ModEnchantments;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.EnumEnchantmentType;
import net.minecraft.enchantment.Enchantment.Rarity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Enchantments;
import net.minecraft.inventory.EntityEquipmentSlot;

public class EnchantmentEducation extends Enchantment {
   public EnchantmentEducation() {
      super(Rarity.RARE, EnumEnchantmentType.WEAPON, new EntityEquipmentSlot[]{EntityEquipmentSlot.MAINHAND});
      this.setRegistryName("education");
      this.setName("mujmajnkraftsbettersurvival.education");
   }

   public static float getExpMultiplyer(EntityPlayer killer, EntityLivingBase killed) {
      if (killed.isNonBoss()) {
         float lvl = (float)EnchantmentHelper.getMaxEnchantmentLevel(ModEnchantments.education, killer);
         return lvl / 2.0F + 1.0F;
      } else {
         return 1.0F;
      }
   }

   public int getMinEnchantability(int enchantmentLevel) {
      return 15 + (enchantmentLevel - 1) * 9;
   }

   public int getMaxEnchantability(int enchantmentLevel) {
      return super.getMinEnchantability(enchantmentLevel) + 50;
   }

   public int getMaxLevel() {
      return ForgeConfigHandler.enchantments.educationLevel;
   }

   public boolean canApplyTogether(Enchantment ench) {
      return super.canApplyTogether(ench) && ench != Enchantments.LOOTING;
   }

   public boolean isTreasureEnchantment() {
      return ForgeConfigHandler.enchantments.educationTreasure;
   }

   public boolean isAllowedOnBooks() {
      return ForgeConfigHandler.enchantments.educationLevel != 0;
   }
}
