package com.tmtravlr.potioncore.potion;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.math.MathHelper;

public class PotionSpin extends PotionCorePotion {
   public static final String NAME = "spin";
   public static final String TAG_PITCH = "potion core - spin pitch";
   public static final String TAG_YAW = "potion core - spin yaw";
   public static final PotionSpin INSTANCE = new PotionSpin();
   public static float rotationSpeed = 2.0F;

   public PotionSpin() {
      super("spin", true, 10079232);
   }

   public void performEffect(EntityLivingBase entity, int amplifier) {
      float maxRotation = 8.0F;
      float maxRand = rotationSpeed * (float)(amplifier + 1);
      float pitch = entity.getEntityData().getFloat("potion core - spin pitch");
      float yaw = entity.getEntityData().getFloat("potion core - spin yaw");
      float randPitch = entity.getRNG().nextFloat() * maxRand * 2.0F - maxRand;
      float randYaw = entity.getRNG().nextFloat() * maxRand * 2.0F - maxRand;
      if (MathHelper.abs(pitch + randPitch) > maxRotation) {
         pitch = pitch + -randPitch;
      } else {
         pitch = pitch + randPitch;
      }

      if (MathHelper.abs(yaw + randYaw) > maxRotation) {
         yaw = yaw + -randYaw;
      } else {
         yaw = yaw + randYaw;
      }

      entity.rotationPitch += pitch;
      entity.rotationYaw += yaw;
      entity.getEntityData().setFloat("potion core - spin pitch", pitch);
      entity.getEntityData().setFloat("potion core - spin yaw", yaw);
   }
}
