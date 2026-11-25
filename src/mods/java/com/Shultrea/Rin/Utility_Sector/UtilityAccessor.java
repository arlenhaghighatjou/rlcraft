package com.Shultrea.Rin.Utility_Sector;

import java.lang.reflect.Method;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.DamageSource;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;

public class UtilityAccessor {
   private static Method damageEntity;
   private static Method applyPotionDamageCalculations;
   private static Method applyArmorCalculations;
   private static Method negateDamage;
   private static Method canApplyTogether;

   public UtilityAccessor() {
   }

   public static void damageEntity(EntityLivingBase target, DamageSource source, float amount) {
      if (damageEntity == null) {
         damageEntity = ObfuscationReflectionHelper.findMethod(EntityLivingBase.class, "damageEntity", Void.TYPE, new Class[]{DamageSource.class, Float.TYPE});
      }

      try {
         damageEntity.invoke(target, source, amount);
      } catch (Exception var4) {
         var4.printStackTrace();
      }

   }

   public static float applyArmorCalculations(EntityLivingBase target, DamageSource source, float amount) {
      if (applyArmorCalculations == null) {
         applyArmorCalculations = ObfuscationReflectionHelper.findMethod(EntityLivingBase.class, "applyArmorCalculations", Float.TYPE, new Class[]{DamageSource.class, Float.TYPE});
      }

      try {
         applyArmorCalculations.invoke(target, source, amount);
      } catch (Exception var4) {
         var4.printStackTrace();
      }

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
      if (applyPotionDamageCalculations == null) {
         applyPotionDamageCalculations = ObfuscationReflectionHelper.findMethod(EntityLivingBase.class, "applyPotionDamageCalculations", Float.TYPE, new Class[]{DamageSource.class, Float.TYPE});
      }

      try {
         applyPotionDamageCalculations.invoke(target, source, amount);
      } catch (Exception var4) {
         var4.printStackTrace();
      }

      return amount;
   }

   public static boolean canApplyTogether(Enchantment en1, Enchantment en2) {
      boolean flag = true;
      if (canApplyTogether == null) {
         canApplyTogether = ObfuscationReflectionHelper.findMethod(Enchantment.class, "canApplyTogether", Boolean.TYPE, new Class[]{Enchantment.class});
      }

      try {
         canApplyTogether.invoke(en1, en2, flag);
         return flag;
      } catch (Exception var4) {
         var4.printStackTrace();
         return flag;
      }
   }
}
