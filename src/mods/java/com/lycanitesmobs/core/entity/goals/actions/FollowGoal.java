package com.lycanitesmobs.core.entity.goals.actions;

import com.lycanitesmobs.core.entity.BaseCreatureEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.BlockPos;

public abstract class FollowGoal extends EntityAIBase {
   BaseCreatureEntity host;
   double speed = 1.0D;
   Class targetClass;
   private int updateRate;
   double strayDistance = 4.0D;
   double lostDistance = 32.0D;
   double behindDistance = 0.0D;

   public FollowGoal(BaseCreatureEntity setHost) {
      this.host = setHost;
      this.targetClass = this.host.getClass();
      this.setMutexBits(1);
   }

   public abstract Entity getTarget();

   public abstract void setTarget(Entity var1);

   public FollowGoal setSpeed(double setSpeed) {
      this.speed = setSpeed;
      return this;
   }

   public FollowGoal setTargetClass(Class setTargetClass) {
      this.targetClass = setTargetClass;
      return this;
   }

   public FollowGoal setStrayDistance(double setDist) {
      this.strayDistance = setDist;
      return this;
   }

   public FollowGoal setLostDistance(double setDist) {
      this.lostDistance = setDist;
      return this;
   }

   public FollowGoal setFollowBehind(double setDist) {
      this.behindDistance = setDist;
      return this;
   }

   public boolean shouldExecute() {
      Entity target = this.getTarget();
      if (target != null && target != this.host) {
         if (!target.isEntityAlive()) {
            return false;
         } else {
            double distance = (double)this.host.getDistance(target);
            if (distance > this.lostDistance && this.lostDistance != 0.0D) {
               return false;
            } else {
               return distance > this.strayDistance || this.strayDistance == 0.0D;
            }
         }
      } else {
         return false;
      }
   }

   public boolean shouldContinueExecuting() {
      Entity target = this.getTarget();
      if (target == null) {
         return false;
      } else if (!target.isEntityAlive()) {
         return false;
      } else {
         double distance = (double)this.host.getDistance(target);
         if (distance > this.lostDistance && this.lostDistance != 0.0D) {
            this.setTarget((Entity)null);
            return false;
         } else if (distance <= this.strayDistance / 2.0D && this.strayDistance != 0.0D) {
            return false;
         } else {
            this.onTargetDistance(distance, target);
            return this.getTarget() != null;
         }
      }
   }

   public void startExecuting() {
      this.updateRate = 0;
   }

   public void updateTask() {
      if (this.updateRate-- <= 0) {
         Entity target = this.getTarget();
         if (target instanceof EntityPlayer) {
            this.updateRate = 10;
         } else {
            this.updateRate = 60;
         }

         if (!this.host.useDirectNavigator()) {
            if (this.behindDistance != 0.0D && target instanceof BaseCreatureEntity) {
               BlockPos pos = ((BaseCreatureEntity)target).getFacingPosition(-this.behindDistance);
               this.host.getNavigator().tryMoveToXYZ((double)pos.getX(), (double)pos.getY(), (double)pos.getZ(), this.speed);
            } else {
               this.host.getNavigator().tryMoveToEntityLiving(target, this.speed);
            }
         } else if (this.behindDistance != 0.0D && target instanceof BaseCreatureEntity) {
            BlockPos pos = ((BaseCreatureEntity)target).getFacingPosition(-this.behindDistance);
            this.host.directNavigator.setTargetPosition(pos, this.speed);
         } else {
            this.host.directNavigator.setTargetPosition(new BlockPos((int)target.posX, (int)target.posY, (int)target.posZ), this.speed);
         }
      }

   }

   public void resetTask() {
      this.host.clearMovement();
   }

   public void onTargetDistance(double distance, Entity followTarget) {
   }
}
