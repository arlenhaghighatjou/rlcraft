package com.lycanitesmobs.core.entity.goals.actions;

import com.lycanitesmobs.core.entity.BaseCreatureEntity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.util.math.BlockPos;

public class ChaseGoal extends EntityAIBase {
   private BaseCreatureEntity host;
   private EntityLivingBase target;
   private double speed = 1.0D;
   private float maxTargetDistance = 8.0F;
   private float minTargetDistance = 0.0F;
   private BlockPos movePos;

   public ChaseGoal(BaseCreatureEntity setHost) {
      this.host = setHost;
      this.setMutexBits(1);
   }

   public ChaseGoal setSpeed(double setSpeed) {
      this.speed = setSpeed;
      return this;
   }

   public ChaseGoal setMaxDistance(float setDist) {
      this.maxTargetDistance = setDist;
      return this;
   }

   public ChaseGoal setMinDistance(float setDist) {
      this.minTargetDistance = setDist;
      return this;
   }

   public boolean shouldExecute() {
      this.target = this.host.getAttackTarget();
      if (this.target != null && this.target.isEntityAlive()) {
         float distance = this.host.getDistance(this.target);
         if (distance > this.maxTargetDistance) {
            return false;
         } else if (distance < this.minTargetDistance) {
            return false;
         } else {
            this.movePos = this.target.getPosition();
            return true;
         }
      } else {
         return false;
      }
   }

   public boolean shouldContinueExecuting() {
      if (!this.shouldExecute()) {
         return false;
      } else {
         return this.host.useDirectNavigator() || !this.host.getNavigator().noPath();
      }
   }

   public void startExecuting() {
      if (!this.host.useDirectNavigator()) {
         this.host.getNavigator().tryMoveToXYZ((double)this.movePos.getX(), (double)this.movePos.getY(), (double)this.movePos.getZ(), this.speed);
      } else {
         this.host.directNavigator.setTargetPosition(this.movePos, this.speed);
      }

   }

   public void resetTask() {
      this.target = null;
      if (!this.host.useDirectNavigator()) {
         this.host.getNavigator().clearPath();
      } else {
         this.host.directNavigator.clearTargetPosition(this.speed);
      }

   }
}
