package com.lycanitesmobs.core.entity.goals.actions;

import com.lycanitesmobs.core.entity.BaseCreatureEntity;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.util.math.BlockPos;

public class StayByHomeGoal extends EntityAIBase {
   protected BaseCreatureEntity host;
   protected boolean enabled = true;
   protected double speed = 1.0D;
   protected double farSpeed = 1.5D;

   public StayByHomeGoal(BaseCreatureEntity setHost) {
      this.host = setHost;
      this.setMutexBits(1);
   }

   public StayByHomeGoal setEnabled(boolean flag) {
      this.enabled = flag;
      return this;
   }

   public StayByHomeGoal setSpeed(double setSpeed) {
      this.speed = setSpeed;
      return this;
   }

   public StayByHomeGoal setFarSpeed(double setSpeed) {
      this.farSpeed = setSpeed;
      return this;
   }

   public boolean shouldExecute() {
      if (!this.enabled) {
         return false;
      } else if (this.host.hasHome() && this.host.getDistanceFromHome() > 1.0D) {
         return this.host.canBreatheUnderwater() || !this.host.isInWater();
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
      BlockPos homePos = this.host.getHomePosition();
      double speed = this.speed;
      if (this.host.getDistanceFromHome() > (double)this.host.getHomeDistanceMax()) {
         speed = this.farSpeed;
      }

      if (!this.host.useDirectNavigator()) {
         this.host.getNavigator().tryMoveToXYZ((double)homePos.getX(), (double)homePos.getY(), (double)homePos.getZ(), this.speed);
      } else {
         this.host.directNavigator.setTargetPosition(new BlockPos(homePos.getX(), homePos.getY(), homePos.getZ()), speed);
      }

   }

   public void resetTask() {
      if (!this.host.useDirectNavigator()) {
         this.host.getNavigator().clearPath();
      } else {
         this.host.directNavigator.clearTargetPosition(this.speed);
      }

   }
}
