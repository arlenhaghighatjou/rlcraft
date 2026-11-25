package com.lycanitesmobs.core.entity.goals.actions;

import com.lycanitesmobs.core.entity.RideableCreatureEntity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.EntityAIBase;

public class PlayerControlGoal extends EntityAIBase {
   private RideableCreatureEntity host;
   private double speed = 1.0D;
   private double sprintSpeed = 1.5D;
   private double flightSpeed = 1.0D;
   public boolean enabled = true;

   public PlayerControlGoal(RideableCreatureEntity setHost) {
      this.host = setHost;
      this.setMutexBits(1);
   }

   public PlayerControlGoal setSpeed(double set) {
      this.speed = set;
      return this;
   }

   public PlayerControlGoal setSprintSpeed(double set) {
      this.sprintSpeed = set;
      return this;
   }

   public PlayerControlGoal setFlightSpeed(double set) {
      this.flightSpeed = set;
      return this;
   }

   public PlayerControlGoal setEnabled(boolean setEnabled) {
      this.enabled = setEnabled;
      return this;
   }

   public boolean shouldExecute() {
      if (!this.enabled) {
         return false;
      } else if (!this.host.isTamed()) {
         return false;
      } else if (!this.host.hasRiderTarget()) {
         return false;
      } else {
         return this.host.getControllingPassenger() instanceof EntityLivingBase;
      }
   }

   public boolean shouldContinueExecuting() {
      return this.shouldExecute();
   }

   public void startExecuting() {
   }

   public void resetTask() {
   }

   public void updateTask() {
   }
}
