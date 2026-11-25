package com.lycanitesmobs.core.entity.goals.actions;

import com.lycanitesmobs.core.entity.TameableCreatureEntity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.util.math.BlockPos;

public class StayGoal extends EntityAIBase {
   private TameableCreatureEntity host;
   private boolean enabled = true;
   private double speed = 1.0D;
   private double farSpeed = 1.5D;

   public StayGoal(TameableCreatureEntity setHost) {
      this.host = setHost;
      this.setMutexBits(1);
   }

   public StayGoal setEnabled(boolean flag) {
      this.enabled = flag;
      return this;
   }

   public StayGoal setSpeed(double setSpeed) {
      this.speed = setSpeed;
      return this;
   }

   public StayGoal setFarSpeed(double setSpeed) {
      this.farSpeed = setSpeed;
      return this;
   }

   public boolean shouldExecute() {
      if (!this.enabled) {
         return false;
      } else if (!this.host.isTamed()) {
         return false;
      } else {
         if (this.host.isInWater()) {
            if (!this.host.canBreatheUnderwater()) {
               return false;
            }
         } else if (!this.host.onGround && !this.host.isFlying()) {
            return false;
         }

         if (!(this.host.getOwner() instanceof EntityLivingBase)) {
            return false;
         } else {
            EntityLivingBase owner = (EntityLivingBase)this.host.getOwner();
            return owner != null && (double)this.host.getDistance(owner) < 144.0D && owner.getRevengeTarget() != null && !this.host.isPassive() ? false : this.host.isSitting();
         }
      }
   }

   public void startExecuting() {
      this.host.clearMovement();
      if (this.host.hasHome() && this.host.getDistanceFromHome() > 1.0D) {
         BlockPos homePos = this.host.getHomePosition();
         double speed = this.speed;
         if (this.host.getDistanceFromHome() > (double)this.host.getHomeDistanceMax()) {
            speed = this.farSpeed;
         }

         if (!this.host.useDirectNavigator()) {
            this.host.getNavigator().tryMoveToXYZ((double)homePos.getX(), (double)homePos.getY(), (double)homePos.getZ(), this.speed);
         } else {
            this.host.directNavigator.setTargetPosition(new BlockPos(homePos), speed);
         }
      }

   }
}
