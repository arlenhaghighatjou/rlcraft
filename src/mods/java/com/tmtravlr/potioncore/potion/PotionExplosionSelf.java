package com.tmtravlr.potioncore.potion;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;

public class PotionExplosionSelf extends PotionCorePotion {
   public static final String NAME = "burst";
   public static final PotionExplosionSelf INSTANCE = new PotionExplosionSelf();
   public static float explosionSize = 2.0F;

   public PotionExplosionSelf() {
      super("burst", false, 6710886);
   }

   public boolean isInstant() {
      return true;
   }

   public void affectEntity(Entity thrownPotion, Entity thrower, EntityLivingBase entity, int amplifier, double potency) {
      float strength = (float)(amplifier + 1) * explosionSize * (float)potency;
      if (!entity.world.isRemote) {
         entity.world.createExplosion(entity, entity.posX, entity.posY, entity.posZ, strength, false);
      }

   }

   public void performEffect(EntityLivingBase entity, int amplifier) {
      float strength = (float)(amplifier + 1) * explosionSize;
      if (!entity.world.isRemote) {
         entity.world.createExplosion(entity, entity.posX, entity.posY, entity.posZ, strength, false);
      }

   }
}
