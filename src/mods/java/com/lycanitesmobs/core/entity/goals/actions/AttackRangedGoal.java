package com.lycanitesmobs.core.entity.goals.actions;

import com.lycanitesmobs.core.entity.BaseCreatureEntity;
import com.lycanitesmobs.core.entity.RideableCreatureEntity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.BlockPos;

public class AttackRangedGoal extends EntityAIBase {
   private final BaseCreatureEntity host;
   private EntityLivingBase attackTarget;
   private int attackTime;
   private int attackStamina = 0;
   private int attackStaminaMax = 0;
   public boolean attackOnCooldown = false;
   private int staminaRecoverRate = 1;
   private int staminaDrainRate = 1;
   private double speed = 1.0D;
   private int chaseTime;
   private int chaseTimeMax = -1;
   private float range = 6.0F;
   private float attackDistance = 5.0F;
   private float minChaseDistance = 3.0F;
   private float flyingHeight = 2.0F;
   private boolean longMemory = true;
   private boolean checkSight = true;
   private boolean mountedAttacking = true;
   public boolean enabled = true;
   private int repathTimeMax = 20;
   private int repathTime;

   public AttackRangedGoal(BaseCreatureEntity setHost) {
      this.host = setHost;
      this.setMutexBits(3);
   }

   public AttackRangedGoal setSpeed(double setSpeed) {
      this.speed = setSpeed;
      return this;
   }

   public AttackRangedGoal setLongMemory(boolean setLongMemory) {
      this.longMemory = setLongMemory;
      return this;
   }

   public AttackRangedGoal setCheckSight(boolean setCheckSight) {
      this.checkSight = setCheckSight;
      return this;
   }

   public AttackRangedGoal setStaminaTime(int setInt) {
      this.attackStaminaMax = setInt;
      this.attackStamina = this.attackStaminaMax;
      return this;
   }

   public AttackRangedGoal setStaminaRecoverRate(int rate) {
      this.staminaRecoverRate = rate;
      return this;
   }

   public AttackRangedGoal setStaminaDrainRate(int rate) {
      this.staminaDrainRate = rate;
      return this;
   }

   public AttackRangedGoal setRange(float setRange) {
      this.range = setRange;
      this.attackDistance = setRange;
      return this;
   }

   public AttackRangedGoal setMinChaseDistance(float setMinDist) {
      this.minChaseDistance = setMinDist;
      return this;
   }

   public AttackRangedGoal setChaseTime(int setChaseTime) {
      this.chaseTimeMax = setChaseTime;
      return this;
   }

   public AttackRangedGoal setFlyingHeight(float setFlyingHeight) {
      this.flyingHeight = setFlyingHeight;
      return this;
   }

   public AttackRangedGoal setMountedAttacking(boolean bool) {
      this.mountedAttacking = bool;
      return this;
   }

   public AttackRangedGoal setEnabled(boolean setEnabled) {
      this.enabled = setEnabled;
      return this;
   }

   public boolean shouldExecute() {
      if (this.attackStaminaMax > 0 && this.attackOnCooldown) {
         this.attackStamina += this.staminaRecoverRate;
         if (this.attackStamina >= this.attackStaminaMax) {
            this.attackOnCooldown = false;
         }
      }

      if (!this.enabled) {
         return false;
      } else if (this.host.hasPickupEntity() && !this.host.canAttackWithPickup()) {
         return false;
      } else {
         if (!this.mountedAttacking && this.host instanceof RideableCreatureEntity) {
            RideableCreatureEntity rideableHost = (RideableCreatureEntity)this.host;
            if (rideableHost.getControllingPassenger() instanceof EntityPlayer) {
               return false;
            }
         }

         EntityLivingBase possibleAttackTarget = this.host.getAttackTarget();
         if (possibleAttackTarget == null) {
            return false;
         } else if (!possibleAttackTarget.isEntityAlive()) {
            return false;
         } else {
            this.attackTarget = possibleAttackTarget;
            return true;
         }
      }
   }

   public boolean shouldContinueExecuting() {
      if (!this.longMemory) {
         if (!this.host.useDirectNavigator() && !this.host.getNavigator().noPath()) {
            return this.shouldExecute();
         }

         if (this.host.useDirectNavigator() && this.host.directNavigator.targetPosition == null) {
            return this.shouldExecute();
         }
      }

      if (!this.enabled) {
         return false;
      } else {
         EntityLivingBase possibleAttackTarget = this.host.getAttackTarget();
         if (possibleAttackTarget == null) {
            return false;
         } else if (!possibleAttackTarget.isEntityAlive()) {
            return false;
         } else {
            this.attackTarget = possibleAttackTarget;
            return true;
         }
      }
   }

   public void resetTask() {
      this.attackTarget = null;
      this.chaseTime = 0;
      this.attackTime = -1;
   }

   public void updateTask() {
      boolean fixated = this.host.hasFixateTarget() && this.host.getFixateTarget() == this.attackTarget;
      double distance = (double)(this.host.getDistance(this.attackTarget) - this.attackTarget.width / 2.0F);
      boolean hasSight = fixated || this.host.getEntitySenses().canSee(this.attackTarget);
      float flyingHeightOffset = this.flyingHeight;
      if (hasSight && this.chaseTimeMax >= 0 && !fixated) {
         ++this.chaseTime;
      } else {
         this.chaseTime = 0;
      }

      if (!hasSight) {
         flyingHeightOffset = 0.0F;
      }

      if (distance <= (double)this.minChaseDistance || this.chaseTimeMax >= 0 && distance <= (double)this.attackDistance && this.chaseTime >= this.chaseTimeMax) {
         if (!this.host.useDirectNavigator()) {
            this.host.getNavigator().clearPath();
         } else {
            this.host.directNavigator.clearTargetPosition(1.0D);
         }
      } else if (--this.repathTime <= 0) {
         this.repathTime = this.repathTimeMax;
         BlockPos targetPosition = this.attackTarget.getPosition();
         if (this.host.isFlying()) {
            targetPosition = targetPosition.add(0.0D, (double)flyingHeightOffset, 0.0D);
         }

         if (!this.host.useDirectNavigator()) {
            this.host.getNavigator().tryMoveToXYZ((double)targetPosition.getX(), (double)targetPosition.getY(), (double)targetPosition.getZ(), this.speed);
         } else {
            this.host.directNavigator.setTargetPosition(targetPosition, this.speed);
         }
      }

      this.host.getLookHelper().setLookPositionWithEntity(this.attackTarget, 30.0F, 30.0F);
      if (this.attackStaminaMax > 0) {
         if (!this.attackOnCooldown) {
            this.attackStamina -= this.staminaDrainRate;
            if (this.attackStamina <= 0) {
               this.attackOnCooldown = true;
            }
         } else {
            this.attackStamina += this.staminaRecoverRate;
            if (this.attackStamina >= this.attackStaminaMax) {
               this.attackOnCooldown = false;
            }
         }
      } else if (this.attackOnCooldown) {
         this.attackOnCooldown = false;
      }

      if (!this.attackOnCooldown) {
         if (--this.attackTime == 0) {
            if (distance > (double)this.attackDistance || this.checkSight && !hasSight) {
               return;
            }

            float rangeFactor = (float)distance / this.range;
            float outerRangeFactor = rangeFactor;
            if (rangeFactor < 0.1F) {
               outerRangeFactor = 0.1F;
            }

            if (outerRangeFactor > 1.0F) {
               outerRangeFactor = 1.0F;
            }

            this.host.attackRanged(this.attackTarget, outerRangeFactor);
            this.attackTime = this.host.getRangedCooldown();
         } else if (this.attackTime < 0) {
            this.attackTime = this.host.getRangedCooldown();
         }
      }

   }
}
