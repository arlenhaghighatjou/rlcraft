package com.Shultrea.Rin.Ench0_3_0;

import com.Shultrea.Rin.Enchantment_Base_Sector.EnchantmentBase;
import com.Shultrea.Rin.Enchantments_Sector.Smc_030;
import com.Shultrea.Rin.Interfaces.IEnchantmentCurse;
import com.Shultrea.Rin.Main_Sector.ModConfig;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.EnumEnchantmentType;
import net.minecraft.enchantment.Enchantment.Rarity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.init.Enchantments;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class EnchantmentPowerless extends EnchantmentBase implements IEnchantmentCurse {
   public EnchantmentPowerless() {
      super(Rarity.RARE, EnumEnchantmentType.BOW, new EntityEquipmentSlot[]{EntityEquipmentSlot.MAINHAND, EntityEquipmentSlot.OFFHAND});
      this.setName("Powerless");
      this.setRegistryName("Powerless");
   }

   public boolean isConfigEnabled() {
      return ModConfig.enabled.PowerlessEnable;
   }

   public int getMaxLevel() {
      return ModConfig.level.Powerless;
   }

   public int getMinEnchantability(int par1) {
      return 12 + (par1 - 1) * 12;
   }

   public int getMaxEnchantability(int par1) {
      return this.getMinEnchantability(par1) + 30;
   }

   public boolean canApplyTogether(Enchantment fTest) {
      return fTest != Enchantments.POWER && fTest != Smc_030.AdvancedPower ? super.canApplyTogether(fTest) : false;
   }

   public boolean isCurse() {
      return true;
   }

   public boolean isTreasureEnchantment() {
      return true;
   }

   @SubscribeEvent(
      priority = EventPriority.HIGHEST,
      receiveCanceled = true
   )
   public void onEvent(EntityJoinWorldEvent event) {
      if (event.getEntity() instanceof EntityArrow) {
         EntityArrow arrow = (EntityArrow)event.getEntity();
         EntityLivingBase shooter = (EntityLivingBase)arrow.shootingEntity;
         if (shooter == null) {
            return;
         }

         ItemStack bow = shooter.getActiveItemStack();
         if (bow == null || bow == ItemStack.EMPTY) {
            bow = shooter.getHeldItemOffhand();
            if (bow == null || bow == ItemStack.EMPTY) {
               return;
            }
         }

         int p = EnchantmentHelper.getEnchantmentLevel(Smc_030.Powerless, bow);
         if (p > 0) {
            double ArrowDamage = arrow.getDamage();
            arrow.setDamage(ArrowDamage - (0.5D + (double)p * 0.5D));
            if (p < 3) {
               if (Math.random() < (double)((float)p * 0.4F)) {
                  arrow.setIsCritical(false);
               }
            } else if (p >= 3) {
               arrow.setIsCritical(false);
            }
         }
      }

   }
}
