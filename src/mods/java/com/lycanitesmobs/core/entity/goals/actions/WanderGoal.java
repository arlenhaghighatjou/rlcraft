package com.lycanitesmobs.core.entity.goals.actions;

import com.lycanitesmobs.core.entity.BaseCreatureEntity;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

public class WanderGoal extends EntityAIBase {
   private BaseCreatureEntity host;
   private double speed = 1.0D;
   private double xPosition;
   private double yPosition;
   private double zPosition;

   public WanderGoal(BaseCreatureEntity setHost) {
      this.host = setHost;
      this.setMutexBits(1);
   }

   public WanderGoal setSpeed(double setSpeed) {
      this.speed = setSpeed;
      return this;
   }

   public boolean shouldExecute() {
      if (this.host.hasAttackTarget()) {
         return false;
      } else if (this.host.getAge() >= 100) {
         return false;
      } else if (!this.host.rollWanderChance()) {
         return false;
      } else {
         Vec3d newTarget = RandomPositionGenerator.findRandomTarget(this.host, 10, 7, this.host.getFlyingHeight());
         if (newTarget == null) {
            return false;
         } else {
            BlockPos wanderPosition = this.host.getWanderPosition(new BlockPos((int)newTarget.x, (int)newTarget.y, (int)newTarget.z));
            this.xPosition = (double)wanderPosition.getX();
            this.yPosition = (double)wanderPosition.getY();
            this.zPosition = (double)wanderPosition.getZ();
            return true;
         }
      }
   }

   public boolean shouldContinueExecuting() {
      if (!this.host.useDirectNavigator()) {
         if (this.host.getNavigator().noPath()) {
            return false;
         } else if (this.host.getDistanceSq(this.xPosition, this.yPosition, this.zPosition) < 4.0D) {
            this.host.getNavigator().clearPath();
            return false;
         } else {
            return true;
         }
      } else {
         return !this.host.directNavigator.atTargetPosition() && this.host.directNavigator.isTargetPositionValid();
      }
   }

   public void startExecuting() {
      if (!this.host.useDirectNavigator()) {
         this.host.getNavigator().tryMoveToXYZ(this.xPosition, this.yPosition, this.zPosition, this.speed);
      } else {
         this.host.directNavigator.setTargetPosition(new BlockPos((int)this.xPosition, (int)this.yPosition, (int)this.zPosition), this.speed);
      }

   }
}
