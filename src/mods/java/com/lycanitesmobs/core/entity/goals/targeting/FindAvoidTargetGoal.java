package com.lycanitesmobs.core.entity.goals.targeting;

import com.lycanitesmobs.core.entity.BaseCreatureEntity;
import com.lycanitesmobs.core.entity.TameableCreatureEntity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;

public class FindAvoidTargetGoal extends TargetingGoal {
   private Class targetClass = EntityLiving.class;
   protected int targetChance = 0;
   protected boolean tameTargeting = false;

   public FindAvoidTargetGoal(BaseCreatureEntity setHost) {
      super(setHost);
   }

   public FindAvoidTargetGoal setChance(int setChance) {
      this.targetChance = setChance;
      return this;
   }

   public FindAvoidTargetGoal setTargetClass(Class setTargetClass) {
      this.targetClass = setTargetClass;
      return this;
   }

   public FindAvoidTargetGoal setSightCheck(boolean setSightCheck) {
      this.checkSight = setSightCheck;
      return this;
   }

   public FindAvoidTargetGoal setOnlyNearby(boolean setNearby) {
      this.nearbyOnly = setNearby;
      return this;
   }

   public FindAvoidTargetGoal setCantSeeTimeMax(int setCantSeeTimeMax) {
      this.cantSeeTimeMax = setCantSeeTimeMax;
      return this;
   }

   public FindAvoidTargetGoal setTameTargetting(boolean setTargetting) {
      this.tameTargeting = setTargetting;
      return this;
   }

   public FindAvoidTargetGoal setHelpCall(boolean setHelp) {
      this.callForHelp = setHelp;
      return this;
   }

   protected EntityLivingBase getTarget() {
      return this.host.getAvoidTarget();
   }

   protected void setTarget(EntityLivingBase newTarget) {
      this.host.setAvoidTarget(newTarget);
   }

   protected boolean isValidTarget(EntityLivingBase target) {
      if (this.targetClass != null && !this.targetClass.isAssignableFrom(target.getClass())) {
         return false;
      } else if (this.targetClass != this.host.getClass() && target.getClass() == this.host.getClass()) {
         return false;
      } else {
         return !(target instanceof TameableCreatureEntity) || !((TameableCreatureEntity)target).isTamed();
      }
   }

   public boolean shouldExecute() {
      if (!this.tameTargeting && this.host.isTamed()) {
         return false;
      } else if (this.host.updateTick % 20L != 0L) {
         return false;
      } else if (this.targetChance > 0 && this.host.getRNG().nextInt(this.targetChance) != 0) {
         return false;
      } else {
         EntityLivingBase avoidTarget = this.getTarget();
         if (avoidTarget != null && !this.isValidTarget(avoidTarget)) {
            return false;
         } else {
            this.target = null;
            double distance = this.getTargetDistance();
            double heightDistance = 4.0D;
            if (this.host.useDirectNavigator()) {
               heightDistance = distance;
            }

            this.target = this.getNewTarget(distance, heightDistance, distance);
            if (this.callForHelp) {
               this.callNearbyForHelp();
            }

            return this.target != null;
         }
      }
   }
}
