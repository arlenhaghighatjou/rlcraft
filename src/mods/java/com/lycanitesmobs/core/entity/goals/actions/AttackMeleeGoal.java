package com.lycanitesmobs.core.entity.goals.actions;

import com.lycanitesmobs.LycanitesMobs;
import com.lycanitesmobs.core.entity.BaseCreatureEntity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.pathfinding.Path;
import net.minecraft.pathfinding.PathPoint;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;

public class AttackMeleeGoal extends EntityAIBase {
   private BaseCreatureEntity host;
   private EntityLivingBase attackTarget;
   private Path pathToTarget;
   private double speed = 1.0D;
   private Class targetClass;
   private boolean longMemory = true;
   private int attackTime;
   private double attackRange = 0.5D;
   private float maxChaseDistance = 1024.0F;
   private double damageScale = 1.0D;
   public boolean enabled = true;
   protected int phase = -1;
   private int failedPathFindingPenalty;
   private int failedPathFindingPenaltyMax = 0;
   private int failedPathFindingPenaltyPlayerMax = 0;
   private int repathTime;

   public AttackMeleeGoal(BaseCreatureEntity setHost) {
      this.host = setHost;
      this.setMutexBits(3);
   }

   public AttackMeleeGoal setSpeed(double setSpeed) {
      this.speed = setSpeed;
      return this;
   }

   public AttackMeleeGoal setDamageScale(double scale) {
      this.damageScale = scale;
      return this;
   }

   public AttackMeleeGoal setTargetClass(Class setTargetClass) {
      this.targetClass = setTargetClass;
      return this;
   }

   public AttackMeleeGoal setLongMemory(boolean setMemory) {
      this.longMemory = setMemory;
      return this;
   }

   public AttackMeleeGoal setRange(double range) {
      this.attackRange = range;
      return this;
   }

   public AttackMeleeGoal setMaxChaseDistanceSq(float distance) {
      this.maxChaseDistance = distance * distance;
      return this;
   }

   public AttackMeleeGoal setMaxChaseDistance(float distance) {
      this.maxChaseDistance = distance;
      return this;
   }

   public AttackMeleeGoal setMissRate(int rate) {
      this.failedPathFindingPenaltyMax = rate;
      return this;
   }

   public AttackMeleeGoal setEnabled(boolean setEnabled) {
      this.enabled = setEnabled;
      return this;
   }

   public AttackMeleeGoal setPhase(int phase) {
      this.phase = phase;
      return this;
   }

   public boolean shouldExecute() {
      if (!this.enabled) {
         return false;
      } else if (this.phase >= 0 && this.phase != this.host.getBattlePhase()) {
         return false;
      } else if (this.host.hasPickupEntity() && !this.host.canAttackWithPickup()) {
         return false;
      } else {
         this.attackTarget = this.host.getAttackTarget();
         if (this.attackTarget == null) {
            return false;
         } else if (!this.attackTarget.isEntityAlive()) {
            return false;
         } else if (this.host.getDistanceSq(this.attackTarget.posX, this.attackTarget.getEntityBoundingBox().minY, this.attackTarget.posZ) > (double)this.maxChaseDistance) {
            return false;
         } else if (this.targetClass != null && !this.targetClass.isAssignableFrom(this.attackTarget.getClass())) {
            return false;
         } else if (--this.repathTime <= 0) {
            if (!this.host.useDirectNavigator()) {
               if (this.host.isCurrentlyFlying()) {
                  this.pathToTarget = this.host.getNavigator().getPathToXYZ(this.attackTarget.posX, this.attackTarget.getEntityBoundingBox().minY + this.host.getFlightOffset(), this.attackTarget.posZ);
               } else {
                  this.pathToTarget = this.host.getNavigator().getPathToEntityLiving(this.attackTarget);
               }

               this.repathTime = 4 + this.host.getRNG().nextInt(7);
               return this.pathToTarget != null;
            } else {
               return this.host.directNavigator.setTargetPosition(new BlockPos((double)((int)this.attackTarget.posX), (double)((int)this.attackTarget.posY) + this.host.getFlightOffset(), (double)((int)this.attackTarget.posZ)), this.speed);
            }
         } else {
            return true;
         }
      }
   }

   public boolean shouldContinueExecuting() {
      if (!this.enabled) {
         return false;
      } else if (this.phase >= 0 && this.phase != this.host.getBattlePhase()) {
         return false;
      } else {
         this.attackTarget = this.host.getAttackTarget();
         if (this.attackTarget == null) {
            return false;
         } else if (this.targetClass != null && !this.targetClass.isAssignableFrom(this.attackTarget.getClass())) {
            return false;
         } else if (this.host.isEntityAlive() && this.attackTarget.isEntityAlive()) {
            if (this.host.getDistanceSq(this.attackTarget.posX, this.attackTarget.getEntityBoundingBox().minY, this.attackTarget.posZ) > (double)this.maxChaseDistance) {
               return false;
            } else {
               if (!this.longMemory) {
                  if (!this.host.useDirectNavigator() && this.host.getNavigator().noPath()) {
                     return false;
                  }

                  if (this.host.useDirectNavigator() && (this.host.directNavigator.atTargetPosition() || !this.host.directNavigator.isTargetPositionValid())) {
                     return false;
                  }
               }

               return this.host.positionNearHome(MathHelper.floor(this.attackTarget.posX), MathHelper.floor(this.attackTarget.posY), MathHelper.floor(this.attackTarget.posZ));
            }
         } else {
            return false;
         }
      }
   }

   public void startExecuting() {
      if (!this.host.useDirectNavigator()) {
         this.host.getNavigator().setPath(this.pathToTarget, this.speed);
      } else if (this.attackTarget != null) {
         this.host.directNavigator.setTargetPosition(new BlockPos((int)this.attackTarget.posX, (int)(this.attackTarget.getEntityBoundingBox().minY + this.host.getFlightOffset()), (int)this.attackTarget.posZ), this.speed);
      }

      this.repathTime = 0;
   }

   public void resetTask() {
      this.host.getNavigator().clearPath();
      this.host.directNavigator.clearTargetPosition(1.0D);
      this.attackTarget = null;
   }

   public void updateTask() {
      this.host.getLookHelper().setLookPositionWithEntity(this.attackTarget, 30.0F, 30.0F);
      if (this.longMemory || this.host.getEntitySenses().canSee(this.attackTarget)) {
         if (!this.host.useDirectNavigator() && --this.repathTime <= 0) {
            this.repathTime = this.failedPathFindingPenalty + 4 + this.host.getRNG().nextInt(7);
            if (this.host.isCurrentlyFlying()) {
               this.host.getNavigator().tryMoveToXYZ(this.attackTarget.posX, this.attackTarget.getEntityBoundingBox().minY + this.host.getFlightOffset(), this.attackTarget.posZ, this.speed);
            } else {
               this.host.getNavigator().tryMoveToEntityLiving(this.attackTarget, this.speed);
            }

            if (this.host.getNavigator().getPath() != null) {
               PathPoint finalPathPoint = this.host.getNavigator().getPath().getFinalPathPoint();
               if (finalPathPoint != null && this.attackTarget.getDistanceSq((double)finalPathPoint.x, (double)finalPathPoint.y, (double)finalPathPoint.z) < 1.0D) {
                  this.failedPathFindingPenalty = 0;
               } else {
                  this.failedPathFindingPenalty += this.getFailedPathFindingPenalty();
               }
            } else {
               this.failedPathFindingPenalty += this.getFailedPathFindingPenalty();
            }
         } else if (this.host.useDirectNavigator()) {
            this.host.directNavigator.setTargetPosition(new BlockPos((int)this.attackTarget.posX, (int)(this.attackTarget.getEntityBoundingBox().minY + this.host.getFlightOffset()), (int)this.attackTarget.posZ), this.speed);
         }
      }

      LycanitesMobs.logDebug("Attack", "Attack range: " + this.host.getMeleeAttackRange(this.attackTarget, this.attackRange) + "/" + this.host.getDistanceSq(this.attackTarget.posX, this.attackTarget.getEntityBoundingBox().minY, this.attackTarget.posZ));
      if (this.host.getDistanceSq(this.attackTarget.posX, this.attackTarget.getEntityBoundingBox().minY, this.attackTarget.posZ) <= this.host.getMeleeAttackRange(this.attackTarget, this.attackRange)) {
         if (--this.attackTime <= 0) {
            this.attackTime = this.host.getMeleeCooldown();
            if (!this.host.getHeldItemMainhand().isEmpty()) {
               this.host.swingArm(EnumHand.MAIN_HAND);
            }

            this.host.attackMelee(this.attackTarget, this.damageScale);
         }

         double d0 = this.host.posX - this.attackTarget.posX;
         double d1 = this.host.posZ - this.attackTarget.posZ;
         float f = (float)(Math.atan2(d1, d0) * 180.0D / 3.141592653589793D) + 90.0F;
         f = MathHelper.wrapDegrees(f - this.host.rotationYaw);
         if (f < -30.0F) {
            f = -30.0F;
         }

         if (f > 30.0F) {
            f = 30.0F;
         }

         this.host.rotationYaw += f;
      }

   }

   public int getFailedPathFindingPenalty() {
      return !(this.attackTarget instanceof EntityPlayer) && !this.host.isTamed() ? this.failedPathFindingPenaltyMax : this.failedPathFindingPenaltyPlayerMax;
   }
}
