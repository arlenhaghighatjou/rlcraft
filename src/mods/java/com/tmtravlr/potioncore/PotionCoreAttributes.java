package com.tmtravlr.potioncore;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.attributes.IAttribute;
import net.minecraft.entity.ai.attributes.RangedAttribute;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.MobEffects;

public class PotionCoreAttributes {
   public static final IAttribute PROJECTILE_DAMAGE = new RangedAttribute((IAttribute)null, "potioncore.projectileDamage", 1.0D, 0.0D, 2.147483647E9D);
   public static final IAttribute MAGIC_DAMAGE = new RangedAttribute((IAttribute)null, "potioncore.magicDamage", 1.0D, 0.0D, 2.147483647E9D);
   public static final IAttribute JUMP_HEIGHT = (new RangedAttribute((IAttribute)null, "potioncore.jumpHeight", 1.0D, 0.0D, 2.147483647E9D)).setShouldWatch(true);
   public static final IAttribute DIG_SPEED = (new RangedAttribute((IAttribute)null, "potioncore.digSpeed", 1.0D, 8.1E-4D, 2.147483647E9D)).setShouldWatch(true);
   public static final IAttribute STEP_HEIGHT = (new RangedAttribute((IAttribute)null, "potioncore.stepHeight", 0.6D, 0.0D, 256.0D)).setShouldWatch(true);
   public static final IAttribute DAMAGE_RESISTANCE = (new RangedAttribute((IAttribute)null, "potioncore.damageResistance", 1.0D, -2.147483648E9D, 2.0D)).setShouldWatch(true);
   public static final IAttribute MAGIC_SHIELDING = (new RangedAttribute((IAttribute)null, "potioncore.magicShielding", 0.0D, 0.0D, 20.0D)).setShouldWatch(true);

   public PotionCoreAttributes() {
   }

   public static double getPlayerReachDistance(EntityPlayer player) {
      return player != null ? player.getEntityAttribute(EntityPlayer.REACH_DISTANCE).getAttributeValue() + (player.capabilities.isCreativeMode ? 0.5D : 0.0D) : 0.0D;
   }

   public static double getAdjustedJumpHeightAttribute(EntityLivingBase entity) {
      double jump = entity.getEntityAttribute(JUMP_HEIGHT).getAttributeValue();
      if (entity.isPotionActive(MobEffects.JUMP_BOOST)) {
         jump -= (double)(entity.getActivePotionEffect(MobEffects.JUMP_BOOST).getAmplifier() + 1);
      }

      return jump;
   }

   public static double getAdjustedDigSpeed(EntityLivingBase entity, double speed) {
      if (entity.isPotionActive(MobEffects.HASTE)) {
         speed /= 1.0D + (double)(entity.getActivePotionEffect(MobEffects.HASTE).getAmplifier() + 1) * 0.2D;
      }

      return speed;
   }

   public static double getAdjustedDamageResistanceAttribute(EntityLivingBase entity) {
      double resistance = entity.getEntityAttribute(DAMAGE_RESISTANCE).getAttributeValue() - 1.0D;
      if (entity.isPotionActive(MobEffects.RESISTANCE)) {
         resistance -= (double)(entity.getActivePotionEffect(MobEffects.RESISTANCE).getAmplifier() + 1) * 0.2D;
      }

      if (resistance > 1.0D) {
         resistance = 1.0D;
      }

      return resistance;
   }
}
