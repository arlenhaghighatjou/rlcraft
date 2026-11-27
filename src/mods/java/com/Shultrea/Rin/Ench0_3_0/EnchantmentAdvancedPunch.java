package com.Shultrea.Rin.Ench0_3_0;

import net.minecraftforge.fml.common.eventhandler.EventBus;
import com.Shultrea.Rin.Enchantment_Base_Sector.EnchantmentBase;
import com.Shultrea.Rin.Enchantments_Sector.Smc_030;
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

public class EnchantmentAdvancedPunch extends EnchantmentBase {
   public EnchantmentAdvancedPunch() {
      super(Rarity.VERY_RARE, EnumEnchantmentType.BOW, new EntityEquipmentSlot[]{EntityEquipmentSlot.MAINHAND, EntityEquipmentSlot.OFFHAND});
      this.setName("AdvancedPunch");
      this.setRegistryName("AdvancedPunch");
   }

   public boolean isConfigEnabled() {
      return ModConfig.enabled.AdvancedPunch;
   }

   public int getMaxLevel() {
      return ModConfig.level.AdvancedPunch;
   }

   public int getMinEnchantability(int par1) {
      return 10 + (par1 - 1) * 8;
   }

   public int getMaxEnchantability(int par1) {
      return this.getMinEnchantability(par1) + 30;
   }

   public boolean canApplyTogether(Enchantment fTest) {
      return fTest == Enchantments.PUNCH ? false : super.canApplyTogether(fTest);
   }

   public void register(EventBus bus) {
      bus.register(EntityJoinWorldEvent.class, EventPriority.HIGHEST, true, this::onEvent);
   }

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

         int p = EnchantmentHelper.getEnchantmentLevel(Smc_030.AdvancedPunch, bow);
         if (p > 0) {
            arrow.setKnockbackStrength(p * 2 + 1);
         }
      }

   }
}
