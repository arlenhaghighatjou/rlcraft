package com.tmtravlr.potioncore.potion;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.effect.EntityLightningBolt;

public class PotionLightning extends PotionCorePotion {
   public static final String NAME = "lightning";
   public static final PotionLightning INSTANCE = new PotionLightning();

   public PotionLightning() {
      super("lightning", true, 16776960);
   }

   public boolean isInstant() {
      return true;
   }

   public void performEffect(EntityLivingBase entity, int amplifier) {
      entity.world.addWeatherEffect(new EntityLightningBolt(entity.world, entity.posX, entity.posY, entity.posZ, false));
   }
}
