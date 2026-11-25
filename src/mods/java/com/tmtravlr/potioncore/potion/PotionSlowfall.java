package com.tmtravlr.potioncore.potion;

import net.minecraft.entity.EntityLivingBase;

public class PotionSlowfall extends PotionCorePotion {
   public static final String NAME = "slow_fall";
   public static final PotionSlowfall INSTANCE = new PotionSlowfall();
   public static float maxSpeed = 0.5F;

   public PotionSlowfall() {
      super("slow_fall", false, 16777164);
   }

   public void performEffect(EntityLivingBase entity, int amplifier) {
      double maxMotion = (double)(-maxSpeed) / (double)(amplifier + 1);
      if (entity.motionY < maxMotion) {
         entity.motionY = maxMotion;
      }

   }
}
