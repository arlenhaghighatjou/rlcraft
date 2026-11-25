package com.lycanitesmobs.core.entity.goals.targeting;

import com.lycanitesmobs.api.Targeting;
import com.lycanitesmobs.core.entity.BaseCreatureEntity;
import com.lycanitesmobs.core.entity.TameableCreatureEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.IEntityOwnable;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.player.EntityPlayer;

public class DefendOwnerGoal extends TargetingGoal {
   private TameableCreatureEntity tamedHost;

   public DefendOwnerGoal(TameableCreatureEntity setHost) {
      super(setHost);
      this.tamedHost = setHost;
      this.setMutexBits(1);
   }

   public DefendOwnerGoal setSightCheck(boolean setSightCheck) {
      this.checkSight = setSightCheck;
      return this;
   }

   public DefendOwnerGoal setOnlyNearby(boolean setNearby) {
      this.nearbyOnly = setNearby;
      return this;
   }

   public DefendOwnerGoal setCantSeeTimeMax(int setCantSeeTimeMax) {
      this.cantSeeTimeMax = setCantSeeTimeMax;
      return this;
   }

   protected EntityLivingBase getTarget() {
      return this.host.getAttackTarget();
   }

   protected void setTarget(EntityLivingBase newTarget) {
      this.host.setAttackTarget(newTarget);
   }

   protected Entity getOwner() {
      return this.tamedHost.getPlayerOwner();
   }

   protected boolean isValidTarget(EntityLivingBase target) {
      if (target == null) {
         return false;
      } else if (this.tamedHost.isTamed() && this.getOwner() != null) {
         if (this.tamedHost.isPassive()) {
            return false;
         } else if (!this.host.isAggressive()) {
            return false;
         } else if (this.host.isOnSameTeam(target)) {
            return false;
         } else {
            if (target instanceof EntityLiving) {
               EntityLiving EntityLiving = (EntityLiving)target;
               if (!EntityLiving.canAttackClass(EntityPlayer.class)) {
                  return false;
               }
            }

            if (!Targeting.isValidTarget(this.host, target)) {
               return false;
            } else if (target instanceof IMob && !(target instanceof IEntityOwnable) && !(target instanceof BaseCreatureEntity)) {
               return true;
            } else if (target instanceof BaseCreatureEntity && ((BaseCreatureEntity)target).isHostileTo(this.getOwner())) {
               return true;
            } else if (target instanceof EntityLiving && ((EntityLiving)target).getAttackTarget() == this.getOwner()) {
               return true;
            } else {
               return target.getRevengeTarget() == this.getOwner();
            }
         }
      } else {
         return false;
      }
   }

   public boolean shouldExecute() {
      this.target = null;
      if (!this.tamedHost.isTamed()) {
         return false;
      } else if (this.tamedHost.isPassive()) {
         return false;
      } else if (!this.host.isAggressive()) {
         return false;
      } else {
         double distance = this.getTargetDistance() - (double)this.host.width;
         double heightDistance = 4.0D - (double)this.host.height;
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

   public boolean shouldContinueExecuting() {
      return !this.isValidTarget(this.getTarget()) ? false : super.shouldContinueExecuting();
   }
}
