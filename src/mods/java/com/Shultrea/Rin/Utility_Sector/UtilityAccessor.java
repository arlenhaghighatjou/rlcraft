package com.Shultrea.Rin.Utility_Sector;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.DamageSource;
import net.minecraftforge.common.ForgeHooks;

public class UtilityAccessor {
   public UtilityAccessor() {
   }

   public static void damageEntity(EntityLivingBase target, DamageSource source, float amount) {
      target.callDamageEntity(source, amount);
   }

   public static float applyArmorCalculations(EntityLivingBase target, DamageSource source, float amount) {
      target.callApplyArmorCalculations(source, amount);
      return amount;
   }

   public static void damageTarget(EntityLivingBase target, DamageSource damageSrc, float damageAmount) {
      if (!target.isEntityInvulnerable(damageSrc)) {
         if (damageAmount <= 0.0F) {
            return;
         }

         damageAmount = applyArmorCalculations(target, damageSrc, damageAmount);
         damageAmount = applyPotionDamageCalculations(target, damageSrc, damageAmount);
         float f = damageAmount;
         damageAmount = Math.max(damageAmount - target.getAbsorptionAmount(), 0.0F);
         target.setAbsorptionAmount(target.getAbsorptionAmount() - (f - damageAmount));
         if (damageAmount > 0.0F) {
            float f1 = target.getHealth();
            target.getCombatTracker().trackDamage(damageSrc, f1, damageAmount);
            target.setHealth(f1 - damageAmount);
            target.setAbsorptionAmount(target.getAbsorptionAmount() - damageAmount);
         }
      }

   }

   public static void damageTargetEvent(EntityLivingBase target, DamageSource damageSrc, float damageAmount) {
      if (!target.isEntityInvulnerable(damageSrc)) {
         if (damageAmount <= 0.0F) {
            return;
         }

         damageAmount = applyArmorCalculations(target, damageSrc, damageAmount);
         damageAmount = applyPotionDamageCalculations(target, damageSrc, damageAmount);
         float f = damageAmount;
         damageAmount = Math.max(damageAmount - target.getAbsorptionAmount(), 0.0F);
         target.setAbsorptionAmount(target.getAbsorptionAmount() - (f - damageAmount));
         damageAmount = ForgeHooks.onLivingDamage(target, damageSrc, damageAmount);
         if (damageAmount > 0.0F) {
            float f1 = target.getHealth();
            target.getCombatTracker().trackDamage(damageSrc, f1, damageAmount);
            target.setHealth(f1 - damageAmount);
            target.setAbsorptionAmount(target.getAbsorptionAmount() - damageAmount);
         }
      }

   }

   public static float applyPotionDamageCalculations(EntityLivingBase target, DamageSource source, float amount) {
      target.callApplyPotionDamageCalculations(source, amount);
      return amount;
   }

   public static boolean canApplyTogether(Enchantment en1, Enchantment en2) {
      return true;
   }
}
