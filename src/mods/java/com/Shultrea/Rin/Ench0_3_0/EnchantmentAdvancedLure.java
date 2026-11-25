package com.Shultrea.Rin.Ench0_3_0;

import com.Shultrea.Rin.Enchantment_Base_Sector.EnchantmentBase;
import com.Shultrea.Rin.Enchantments_Sector.Smc_030;
import com.Shultrea.Rin.Main_Sector.ModConfig;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.EnumEnchantmentType;
import net.minecraft.enchantment.Enchantment.Rarity;
import net.minecraft.init.Enchantments;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;

public class EnchantmentAdvancedLure extends EnchantmentBase {
   public EnchantmentAdvancedLure() {
      super(Rarity.VERY_RARE, EnumEnchantmentType.FISHING_ROD, new EntityEquipmentSlot[]{EntityEquipmentSlot.MAINHAND, EntityEquipmentSlot.OFFHAND});
      this.setName("AdvancedLure");
      this.setRegistryName("AdvancedLure");
   }

   public boolean isConfigEnabled() {
      return ModConfig.enabled.AdvancedLure;
   }

   public int getMaxLevel() {
      return ModConfig.level.AdvancedLure;
   }

   public int getMinEnchantability(int par1) {
      return 15 + 15 * par1;
   }

   public int getMaxEnchantability(int par1) {
      return super.getMinEnchantability(par1) + 30;
   }

   public boolean canApplyTogether(Enchantment fTest) {
      return fTest == Enchantments.LURE ? false : super.canApplyTogether(fTest);
   }

   public static int getValue(ItemStack stack) {
      if (!Smc_030.AdvancedLure.isEnabled()) {
         return 0;
      } else {
         int level = EnchantmentHelper.getEnchantmentLevel(Smc_030.AdvancedLure, stack);
         if (level <= 0) {
            return 0;
         } else {
            int toReturn = level + 1;
            if (Math.random() < 0.15000000596046448D) {
               toReturn = level + 2;
            }

            return toReturn;
         }
      }
   }
}
