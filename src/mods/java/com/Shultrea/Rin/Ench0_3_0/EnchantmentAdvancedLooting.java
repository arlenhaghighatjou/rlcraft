package com.Shultrea.Rin.Ench0_3_0;

import com.Shultrea.Rin.Enchantment_Base_Sector.EnchantmentBase;
import com.Shultrea.Rin.Enchantments_Sector.Smc_030;
import com.Shultrea.Rin.Main_Sector.ModConfig;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.EnumEnchantmentType;
import net.minecraft.enchantment.Enchantment.Rarity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.Enchantments;
import net.minecraft.inventory.EntityEquipmentSlot;

public class EnchantmentAdvancedLooting extends EnchantmentBase {
   public EnchantmentAdvancedLooting() {
      super(Rarity.VERY_RARE, EnumEnchantmentType.WEAPON, new EntityEquipmentSlot[]{EntityEquipmentSlot.MAINHAND});
      this.setName("AdvancedLooting");
      this.setRegistryName("AdvancedLooting");
   }

   public boolean isConfigEnabled() {
      return ModConfig.enabled.AdvancedLootingEnable;
   }

   public int getMaxLevel() {
      return ModConfig.level.AdvancedLooting;
   }

   public int getMinEnchantability(int par1) {
      return 30 + 15 * (par1 - 1);
   }

   public int getMaxEnchantability(int par1) {
      return super.getMinEnchantability(par1) + 60;
   }

   public boolean canApplyTogether(Enchantment fTest) {
      return fTest == Enchantments.LOOTING ? false : super.canApplyTogether(fTest);
   }

   public static int getValue(int original, EntityLivingBase entity) {
      if (!Smc_030.AdvancedLooting.isEnabled()) {
         return 0;
      } else {
         int levelLooting = EnchantmentHelper.getMaxEnchantmentLevel(Smc_030.AdvancedLooting, entity);
         if (levelLooting <= 0) {
            return 0;
         } else {
            int toReturn = original + 2 + (levelLooting - 1) * 2;
            if (Math.random() < 0.25D) {
               toReturn = toReturn + 2 + levelLooting * 2;
            }

            return toReturn;
         }
      }
   }
}
