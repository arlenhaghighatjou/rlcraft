package com.lycanitesmobs.core.entity.goals.targeting;

import com.lycanitesmobs.core.entity.BaseCreatureEntity;
import com.lycanitesmobs.core.entity.TameableCreatureEntity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.passive.EntityTameable;

public class DefendEntitiesGoal extends TargetingGoal {
   protected Class<? extends EntityLiving> defendClass;

   public DefendEntitiesGoal(BaseCreatureEntity setHost, Class<? extends EntityLiving> defendClass) {
      super(setHost);
      this.defendClass = defendClass;
   }

   public DefendEntitiesGoal setSightCheck(boolean setSightCheck) {
      this.checkSight = setSightCheck;
      return this;
   }

   public DefendEntitiesGoal setOnlyNearby(boolean setNearby) {
      this.nearbyOnly = setNearby;
      return this;
   }

   public DefendEntitiesGoal setCantSeeTimeMax(int setCantSeeTimeMax) {
      this.cantSeeTimeMax = setCantSeeTimeMax;
      return this;
   }

   protected EntityLivingBase getTarget() {
      return this.host.getAttackTarget();
   }

   protected void setTarget(EntityLivingBase newTarget) {
      this.host.setAttackTarget(newTarget);
   }

   protected boolean isValidTarget(EntityLivingBase target) {
      if (this.host.getOwner() != null) {
         return false;
      } else {
         EntityLivingBase targetTarget = target.getRevengeTarget();
         if (target instanceof EntityLiving) {
            targetTarget = ((EntityLiving)target).getAttackTarget();
         }

         if (targetTarget == null) {
            return false;
         } else {
            if (this.host.getOwner() != null) {
               if (target instanceof EntityTameable && this.host.getOwner() == ((EntityTameable)target).getOwner()) {
                  return false;
               }

               if (target instanceof TameableCreatureEntity && this.host.getOwner() == ((TameableCreatureEntity)target).getOwner()) {
                  return false;
               }

               if (target == this.host.getOwner()) {
                  return false;
               }
            }

            return this.defendClass.isAssignableFrom(targetTarget.getClass());
         }
      }
   }

   public boolean shouldExecute() {
      this.target = null;
      if (this.host.getOwner() != null) {
         return false;
      } else {
         double distance = this.getTargetDistance() - (double)this.host.width;
         double heightDistance = 4.0D - (double)this.host.height;
         if (this.host.useDirectNavigator()) {
            heightDistance = this.getTargetDistance() - (double)this.host.height;
         }

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
