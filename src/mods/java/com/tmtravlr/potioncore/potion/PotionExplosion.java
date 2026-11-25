package com.tmtravlr.potioncore.potion;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityTNTPrimed;

public class PotionExplosion extends PotionCorePotion {
   public static final String NAME = "explode";
   public static final PotionExplosion INSTANCE = new PotionExplosion();
   public static float explosionSize = 2.0F;

   public PotionExplosion() {
      super("explode", true, 3355443);
   }

   public boolean isInstant() {
      return true;
   }

   public void affectEntity(Entity thrownPotion, Entity thrower, EntityLivingBase entity, int amplifier, double potency) {
      float strength = (float)(amplifier + 1) * explosionSize * (float)potency;
      EntityLivingBase source = null;
      if (thrower instanceof EntityLivingBase) {
         source = (EntityLivingBase)thrower;
      }

      if (!entity.world.isRemote) {
         EntityTNTPrimed tnt = new EntityTNTPrimed(entity.world, entity.posX, entity.posY, entity.posZ, source);
         entity.world.createExplosion(tnt, entity.posX, entity.posY, entity.posZ, strength, true);
      }

   }

   public void performEffect(EntityLivingBase entity, int amplifier) {
      float strength = (float)(amplifier + 1) * explosionSize;
      if (!entity.world.isRemote) {
         EntityTNTPrimed tnt = new EntityTNTPrimed(entity.world, entity.posX, entity.posY, entity.posZ, entity);
         entity.world.createExplosion(tnt, entity.posX, entity.posY, entity.posZ, strength, true);
      }

   }
}
