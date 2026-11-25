package com.Shultrea.Rin.Utility_Sector;

import com.Shultrea.Rin.Enchantments_Sector.Smc_030;
import com.Shultrea.Rin.Main_Sector.ModConfig;
import net.minecraft.util.DamageSource;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class AdditionalProtectionEnchantmentsEffects {
   public AdditionalProtectionEnchantmentsEffects() {
   }

   @SubscribeEvent(
      priority = EventPriority.LOW,
      receiveCanceled = true
   )
   public void protectionOnly(LivingHurtEvent fEvent) {
      if (ModConfig.miscellaneous.extraProtectionEffects) {
         if (ModConfig.enabled.AdvancedProtection) {
            if (!fEvent.getSource().damageType.equals("null")) {
               int modifier = EnchantmentsUtility.CalcModgetTotalLevel(4.75F, Smc_030.AdvancedProtection, fEvent.getEntityLiving());
               float damage = EnchantmentsUtility.getDamageAfterMagicAbsorb(fEvent.getAmount(), (float)modifier);
               fEvent.setAmount(damage);
            }
         }
      }
   }

   @SubscribeEvent(
      priority = EventPriority.LOW,
      receiveCanceled = true
   )
   public void projprotection(LivingHurtEvent fEvent) {
      if (ModConfig.miscellaneous.extraProtectionEffects) {
         if (ModConfig.enabled.AdvancedProjectileProtection) {
            if (fEvent.getSource().isProjectile()) {
               int modifier = EnchantmentsUtility.CalcModgetTotalLevel(7.5F, Smc_030.AdvancedProjectileProtection, fEvent.getEntityLiving());
               float damage = EnchantmentsUtility.getDamageAfterMagicAbsorb(fEvent.getAmount(), (float)modifier);
               fEvent.setAmount(damage);
            }
         }
      }
   }

   @SubscribeEvent(
      priority = EventPriority.LOW,
      receiveCanceled = true
   )
   public void fireprotection(LivingDamageEvent fEvent) {
      if (ModConfig.miscellaneous.extraProtectionEffects) {
         if (ModConfig.enabled.AdvancedFireProtection) {
            if (fEvent.getSource().isFireDamage()) {
               int modifier = EnchantmentsUtility.CalcModgetTotalLevel(7.5F, Smc_030.AdvancedFireProtection, fEvent.getEntityLiving());
               float damage = EnchantmentsUtility.getDamageAfterMagicAbsorb(fEvent.getAmount(), (float)modifier);
               fEvent.setAmount(damage);
            }
         }
      }
   }

   @SubscribeEvent(
      priority = EventPriority.LOW,
      receiveCanceled = true
   )
   public void blastprotection(LivingHurtEvent fEvent) {
      if (ModConfig.miscellaneous.extraProtectionEffects) {
         if (ModConfig.enabled.AdvancedBlastProtection) {
            if (fEvent.getSource().isExplosion()) {
               int modifier = EnchantmentsUtility.CalcModgetTotalLevel(7.5F, Smc_030.AdvancedBlastProtection, fEvent.getEntityLiving());
               float damage = EnchantmentsUtility.getDamageAfterMagicAbsorb(fEvent.getAmount(), (float)modifier);
               fEvent.setAmount(damage);
            }
         }
      }
   }

   @SubscribeEvent(
      priority = EventPriority.LOW,
      receiveCanceled = true
   )
   public void featherfall(LivingHurtEvent fEvent) {
      if (ModConfig.miscellaneous.extraProtectionEffects) {
         if (ModConfig.enabled.AdvancedFeatherFalling) {
            if (fEvent.getSource() == DamageSource.FALL) {
               int modifier = EnchantmentsUtility.CalcModgetTotalLevel(9.0F, Smc_030.AdvancedFeatherFalling, fEvent.getEntityLiving());
               float damage = EnchantmentsUtility.getDamageAfterMagicAbsorb(fEvent.getAmount(), (float)modifier);
               fEvent.setAmount(damage);
            }
         }
      }
   }
}
