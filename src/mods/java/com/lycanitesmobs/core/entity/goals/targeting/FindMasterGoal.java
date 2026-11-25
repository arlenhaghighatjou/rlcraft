package com.lycanitesmobs.core.entity.goals.targeting;

import com.lycanitesmobs.core.entity.AgeableCreatureEntity;
import com.lycanitesmobs.core.entity.BaseCreatureEntity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.entity.passive.EntityAnimal;

public class FindMasterGoal extends TargetingGoal {
   private Class targetClass = EntityLiving.class;
   private boolean tameTargeting = false;
   private int targetChance = 0;

   public FindMasterGoal(BaseCreatureEntity setHost) {
      super(setHost);
   }

   public FindMasterGoal setTameTargetting(boolean setTargetting) {
      this.tameTargeting = setTargetting;
      return this;
   }

   public FindMasterGoal setChance(int setChance) {
      this.targetChance = setChance;
      return this;
   }

   public FindMasterGoal setTargetClass(Class setTargetClass) {
      this.targetClass = setTargetClass;
      return this;
   }

   public FindMasterGoal setSightCheck(boolean setSightCheck) {
      this.checkSight = setSightCheck;
      return this;
   }

   public FindMasterGoal setOnlyNearby(boolean setNearby) {
      this.nearbyOnly = setNearby;
      return this;
   }

   public FindMasterGoal setCantSeeTimeMax(int setCantSeeTimeMax) {
      this.cantSeeTimeMax = setCantSeeTimeMax;
      return this;
   }

   public FindMasterGoal setRange(double setDist) {
      this.targetingRange = setDist;
      return this;
   }

   protected EntityLivingBase getTarget() {
      return this.host.getMasterTarget();
   }

   protected void setTarget(EntityLivingBase newTarget) {
      this.host.setMasterTarget(newTarget);
   }

   protected boolean isValidTarget(EntityLivingBase target) {
      if (this.targetClass != null && !this.targetClass.isAssignableFrom(target.getClass())) {
         return false;
      } else if (target instanceof EntityAnimal && ((EntityAnimal)target).getGrowingAge() < 0) {
         return false;
      } else if (target instanceof AgeableCreatureEntity && ((AgeableCreatureEntity)target).getGrowingAge() < 0) {
         return false;
      } else {
         return this.tameTargeting || !this.host.isTamed();
      }
   }

   protected double getTargetDistance() {
      if (this.targetingRange > 0.0D) {
         return this.targetingRange;
      } else {
         IAttributeInstance attributeinstance = this.host.getEntityAttribute(SharedMonsterAttributes.FOLLOW_RANGE);
         return attributeinstance.getAttributeValue();
      }
   }

   public boolean shouldExecute() {
      if (this.host.updateTick % 20L != 0L) {
         return false;
      } else if (this.targetChance > 0 && this.host.getRNG().nextInt(this.targetChance) != 0) {
         return false;
      } else {
         this.target = null;
         double distance = this.getTargetDistance();
         double heightDistance = 4.0D;
         if (this.host.useDirectNavigator()) {
            heightDistance = distance;
         }

         this.target = this.getNewTarget(distance, heightDistance, distance);
         return this.target != null;
      }
   }
}
