package com.charles445.aireducer.compat.iceandfireold;

import com.charles445.aireducer.ai.AIAvoidReduced;
import com.google.common.base.Predicate;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.passive.EntityTameable;

public class VillagerAIFearUntamedReduced<T extends Entity> extends AIAvoidReduced {
   public VillagerAIFearUntamedReduced(EntityCreature entityIn, Class<T> classToAvoidIn, float avoidDistanceIn, double farSpeedIn, double nearSpeedIn) {
      super(entityIn, classToAvoidIn, avoidDistanceIn, farSpeedIn, nearSpeedIn);
   }

   public VillagerAIFearUntamedReduced(EntityCreature entityIn, Class<T> classToAvoidIn, Predicate<? super T> avoidTargetSelectorIn, float avoidDistanceIn, double farSpeedIn, double nearSpeedIn) {
      super(entityIn, classToAvoidIn, avoidTargetSelectorIn, avoidDistanceIn, farSpeedIn, nearSpeedIn);
   }

   public boolean getRadiusConfig() {
      return true;
   }

   public boolean shouldExecute() {
      return super.shouldExecute() && (this.closestLivingEntity == null || !(this.closestLivingEntity instanceof EntityTameable) || !((EntityTameable)this.closestLivingEntity).isTamed());
   }

   public boolean mustSee() {
      return false;
   }
}
