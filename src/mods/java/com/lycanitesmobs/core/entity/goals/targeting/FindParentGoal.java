package com.lycanitesmobs.core.entity.goals.targeting;

import com.lycanitesmobs.core.entity.AgeableCreatureEntity;
import com.lycanitesmobs.core.entity.TameableCreatureEntity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.IAttributeInstance;

public class FindParentGoal extends TargetingGoal {
   private AgeableCreatureEntity host;
   private Class targetClass;
   private boolean tameTargeting = true;
   private int targetChance = 0;
   private double targetDistance = -1.0D;

   public FindParentGoal(AgeableCreatureEntity setHost) {
      super(setHost);
      this.host = setHost;
      this.targetClass = this.host.getClass();
   }

   public FindParentGoal setChance(int setChance) {
      this.targetChance = setChance;
      return this;
   }

   public FindParentGoal setTargetClass(Class setTargetClass) {
      this.targetClass = setTargetClass;
      return this;
   }

   public FindParentGoal setSightCheck(boolean setSightCheck) {
      this.checkSight = setSightCheck;
      return this;
   }

   public FindParentGoal setOnlyNearby(boolean setNearby) {
      this.nearbyOnly = setNearby;
      return this;
   }

   public FindParentGoal setCantSeeTimeMax(int setCantSeeTimeMax) {
      this.cantSeeTimeMax = setCantSeeTimeMax;
      return this;
   }

   public FindParentGoal setDistance(double setDist) {
      this.targetDistance = setDist;
      return this;
   }

   public FindParentGoal setTameTargetting(boolean setTargetting) {
      this.tameTargeting = setTargetting;
      return this;
   }

   protected EntityLivingBase getTarget() {
      return this.host.getParentTarget();
   }

   protected void setTarget(EntityLivingBase newTarget) {
      this.host.setParentTarget(newTarget);
   }

   protected boolean isValidTarget(EntityLivingBase target) {
      if (this.targetClass != null && !this.targetClass.isAssignableFrom(target.getClass())) {
         return false;
      } else if (target.isChild()) {
         return false;
      } else {
         return this.tameTargeting || !(this.host instanceof TameableCreatureEntity) || !this.host.isTamed();
      }
   }

   protected double getTargetDistance() {
      if (this.targetDistance > -1.0D) {
         return this.targetDistance;
      } else {
         IAttributeInstance attributeinstance = this.host.getEntityAttribute(SharedMonsterAttributes.FOLLOW_RANGE);
         return attributeinstance.getAttributeValue();
      }
   }

   public boolean shouldExecute() {
      if (this.host.updateTick % 20L == 0L && this.host.shouldFindParent()) {
         this.target = null;
         if (!this.host.shouldFollowParent()) {
            this.host.setParentTarget((EntityLivingBase)null);
            return false;
         } else if (this.host.hasParent()) {
            return false;
         } else if (this.host.updateTick % 20L != 0L && this.targetChance > 0 && this.host.getRNG().nextInt(this.targetChance) != 0) {
            return false;
         } else {
            double distance = this.getTargetDistance();
            double heightDistance = 4.0D;
            if (this.host.useDirectNavigator()) {
               heightDistance = distance;
            }

            this.target = this.getNewTarget(distance, heightDistance, distance);
            return this.target != null;
         }
      } else {
         return false;
      }
   }

   public boolean shouldContinueExecuting() {
      if (this.host.getGrowingAge() >= 0) {
         this.host.setParentTarget((EntityLivingBase)null);
         return false;
      } else {
         return super.shouldContinueExecuting();
      }
   }
}
