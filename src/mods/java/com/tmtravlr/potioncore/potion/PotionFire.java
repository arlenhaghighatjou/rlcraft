package com.tmtravlr.potioncore.potion;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.math.MathHelper;

public class PotionFire extends PotionCorePotion {
   public static final String NAME = "fire";
   public static final PotionFire INSTANCE = new PotionFire();
   public static float fireDuration = 10.0F;

   public PotionFire() {
      super("fire", true, 16733440);
   }

   public boolean isInstant() {
      return true;
   }

   public void affectEntity(Entity thrownPotion, Entity thrower, EntityLivingBase entity, int amplifier, double potency) {
      int duration = MathHelper.ceil((double)(amplifier + 1) * (double)fireDuration * potency);
      entity.setFire(duration);
   }

   public void performEffect(EntityLivingBase entity, int amplifier) {
      int duration = MathHelper.ceil((float)(amplifier + 1) * fireDuration);
      entity.setFire(duration);
   }
}
