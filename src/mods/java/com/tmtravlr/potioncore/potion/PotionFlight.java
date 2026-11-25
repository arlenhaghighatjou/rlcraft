package com.tmtravlr.potioncore.potion;

import net.minecraft.entity.EntityLivingBase;

public class PotionFlight extends PotionCorePotion {
   public static final String NAME = "flight";
   public static final String TAG_NAME = "potion core - flight";
   public static final PotionFlight INSTANCE = new PotionFlight();

   public PotionFlight() {
      super("flight", false, 5609983);
   }

   public void performEffect(EntityLivingBase entity, int amplifier) {
      entity.fallDistance = 0.0F;
   }
}
