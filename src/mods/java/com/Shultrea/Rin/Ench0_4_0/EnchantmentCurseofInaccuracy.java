package com.Shultrea.Rin.Ench0_4_0;

import net.minecraftforge.fml.common.eventhandler.EventBus;
import com.Shultrea.Rin.Ench0_4_5.EnchantmentTrueStrike;
import com.Shultrea.Rin.Enchantment_Base_Sector.EnchantmentBase;
import com.Shultrea.Rin.Enum.EnumList;
import com.Shultrea.Rin.Interfaces.IEnchantmentCurse;
import com.Shultrea.Rin.Main_Sector.ModConfig;
import com.Shultrea.Rin.Utility_Sector.EnchantmentsUtility;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantment.Rarity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;

public class EnchantmentCurseofInaccuracy extends EnchantmentBase implements IEnchantmentCurse {
   public EnchantmentCurseofInaccuracy() {
      super(Rarity.VERY_RARE, EnumList.COMBAT_WEAPON, new EntityEquipmentSlot[]{EntityEquipmentSlot.MAINHAND, EntityEquipmentSlot.OFFHAND});
      this.setName("CurseOfInaccuracy");
      this.setRegistryName("CurseOfInaccuracy");
   }

   public boolean isConfigEnabled() {
      return ModConfig.enabled.CurseOfInaccuracy;
   }

   public int getMaxLevel() {
      return ModConfig.level.CurseOfInaccuracy;
   }

   public int getMinEnchantability(int ench) {
      return 15 + (ench - 1) * 15;
   }

   public int getMaxEnchantability(int ench) {
      return this.getMinEnchantability(ench) + 30;
   }

   public boolean isCurse() {
      return true;
   }

   public boolean canApplyTogether(Enchantment fTest) {
      return fTest instanceof EnchantmentTrueStrike ? false : super.canApplyTogether(fTest);
   }

   public boolean isTreasureEnchantment() {
      return true;
   }

   public void register(EventBus bus) {
      bus.register(LivingAttackEvent.class, EventPriority.HIGHEST, false, this::onAttack);
      bus.register(EntityJoinWorldEvent.class, EventPriority.LOWEST, false, this::onRangeAttack);
   }

   public void onAttack(LivingAttackEvent e) {
      if (EnchantmentsUtility.checkEventCondition(e, this)) {
         EntityLivingBase eb = (EntityLivingBase)e.getSource().getTrueSource();
         int level = EnchantmentHelper.getEnchantmentLevel(this, eb.getHeldItemMainhand());
         if (level > 0) {
            if (eb.getRNG().nextInt(10) < level * 2) {
               e.setCanceled(true);
            }

         }
      }
   }

   public void onRangeAttack(EntityJoinWorldEvent e) {
      if (e.getEntity() instanceof EntityArrow) {
         EntityArrow arrow = (EntityArrow)e.getEntity();
         if (arrow.shootingEntity == null) {
            return;
         }

         if (!(arrow.shootingEntity instanceof EntityLivingBase)) {
            return;
         }

         EntityLivingBase shooter = (EntityLivingBase)arrow.shootingEntity;
         float level = (float)EnchantmentHelper.getEnchantmentLevel(this, shooter.getHeldItemMainhand());
         if (level <= 0.0F) {
            level = (float)EnchantmentHelper.getEnchantmentLevel(this, shooter.getHeldItemOffhand());
         }

         if (level <= 0.0F) {
            return;
         }

         if ((float)shooter.getRNG().nextInt(10) < level * 2.0F) {
            float velocity = (float)((Math.abs(arrow.motionX) + Math.abs(arrow.motionY) + Math.abs(arrow.motionZ)) / 3.0D);
            arrow.shoot(shooter, shooter.rotationPitch, shooter.rotationYaw, 0.0F, velocity, level * 10.0F);
         }
      }

   }
}
