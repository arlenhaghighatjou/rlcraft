package com.dhanantry.scapeandrunparasites.entity.ai;

import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityParasiteBase;
import com.dhanantry.scapeandrunparasites.init.SRPPotions;
import com.dhanantry.scapeandrunparasites.util.config.SRPConfigSystems;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.pathfinding.Path;
import net.minecraft.pathfinding.PathPoint;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class EntityAIAttackMeleeStatus extends EntityAIBase {
   World world;
   protected EntityParasiteBase attacker;
   protected int attackTick;
   double speedTowardsTarget;
   boolean longMemory;
   Path path;
   private int delayCounter;
   private double targetX;
   private double targetY;
   private double targetZ;
   private double blockDistance;
   protected final int attackInterval;
   private int failedPathFindingPenalty;
   private boolean canPenalize;
   private int attackCooldown;

   public EntityAIAttackMeleeStatus(EntityParasiteBase creature, double speedIn, boolean useLongMemory, double runningD) {
      this.attackInterval = 20;
      this.failedPathFindingPenalty = 0;
      this.canPenalize = false;
      this.attacker = creature;
      this.world = creature.world;
      this.speedTowardsTarget = speedIn;
      this.longMemory = useLongMemory;
      this.blockDistance = runningD * runningD;
      this.setMutexBits(3);
      this.attackCooldown = 20;
   }

   public EntityAIAttackMeleeStatus(EntityParasiteBase creature, double speedIn, boolean useLongMemory, double runningD, int attCool) {
      this(creature, speedIn, useLongMemory, runningD);
      this.attackCooldown = attCool;
   }

   public boolean shouldExecute() {
      EntityLivingBase entitylivingbase = this.attacker.getAttackTarget();
      if (entitylivingbase == null) {
         return false;
      } else if (entitylivingbase.isEntityAlive() && this.attacker.shouldWorkTask()) {
         if (this.canPenalize) {
            if (--this.delayCounter <= 0) {
               this.path = this.attacker.getNavigator().getPathToEntityLiving(entitylivingbase);
               this.delayCounter = 4 + this.attacker.getRNG().nextInt(7);
               return this.path != null;
            } else {
               return true;
            }
         } else {
            this.path = this.attacker.getNavigator().getPathToEntityLiving(entitylivingbase);
            if (this.path != null) {
               return true;
            } else {
               return this.getAttackReachSqr(entitylivingbase) >= this.attacker.getDistanceSq(entitylivingbase.posX, entitylivingbase.getEntityBoundingBox().minY, entitylivingbase.posZ);
            }
         }
      } else {
         return false;
      }
   }

   public boolean shouldContinueExecuting() {
      EntityLivingBase entitylivingbase = this.attacker.getAttackTarget();
      if (entitylivingbase == null) {
         return false;
      } else if (entitylivingbase.isEntityAlive() && this.attacker.shouldWorkTask()) {
         if (!this.longMemory) {
            return !this.attacker.getNavigator().noPath();
         } else if (!this.attacker.isWithinHomeDistanceFromPosition(new BlockPos(entitylivingbase))) {
            return false;
         } else {
            return !(entitylivingbase instanceof EntityPlayer) || !((EntityPlayer)entitylivingbase).isSpectator() && !((EntityPlayer)entitylivingbase).isCreative();
         }
      } else {
         return false;
      }
   }

   public void startExecuting() {
      EntityLivingBase entitylivingbase = this.attacker.getAttackTarget();
      if (this.attacker.getParasiteStatus() <= 2 && this.attacker.getLLeap() < 1) {
         if (this.attacker.getDistanceSq(entitylivingbase) <= this.blockDistance && this.attacker.getAttackCooldownAni() != 0) {
            this.attacker.getNavigator().setPath(this.path, 1.0D);
            this.attacker.setParasiteStatus(1);
         } else {
            this.attacker.getNavigator().setPath(this.path, this.speedTowardsTarget);
            this.attacker.setParasiteStatus(2);
            this.attacker.setAttackCooldownAni(0);
         }
      }

      this.delayCounter = 0;
   }

   public void resetTask() {
      EntityLivingBase entitylivingbase = this.attacker.getAttackTarget();
      if (entitylivingbase instanceof EntityPlayer && (((EntityPlayer)entitylivingbase).isSpectator() || ((EntityPlayer)entitylivingbase).isCreative())) {
         this.attacker.setAttackTarget((EntityLivingBase)null);
      }

      this.attacker.getNavigator().clearPath();
   }

   public void updateTask() {
      EntityLivingBase entitylivingbase = this.attacker.getAttackTarget();
      if (entitylivingbase != null) {
         this.attacker.getLookHelper().setLookPositionWithEntity(entitylivingbase, 30.0F, 30.0F);
         double d0 = this.attacker.getDistanceSq(entitylivingbase.posX, entitylivingbase.getEntityBoundingBox().minY, entitylivingbase.posZ);
         --this.delayCounter;
         if ((this.longMemory || this.attacker.getEntitySenses().canSee(entitylivingbase)) && this.delayCounter <= 0 && (this.targetX == 0.0D && this.targetY == 0.0D && this.targetZ == 0.0D || entitylivingbase.getDistanceSq(this.targetX, this.targetY, this.targetZ) >= 1.0D || this.attacker.getRNG().nextFloat() < 0.05F)) {
            this.targetX = entitylivingbase.posX;
            this.targetY = entitylivingbase.getEntityBoundingBox().minY;
            this.targetZ = entitylivingbase.posZ;
            this.delayCounter = 4 + this.attacker.getRNG().nextInt(7);
            if (this.canPenalize) {
               this.delayCounter += this.failedPathFindingPenalty;
               if (this.attacker.getNavigator().getPath() != null) {
                  PathPoint finalPathPoint = this.attacker.getNavigator().getPath().getFinalPathPoint();
                  if (finalPathPoint != null && entitylivingbase.getDistanceSq((double)finalPathPoint.x, (double)finalPathPoint.y, (double)finalPathPoint.z) < 1.0D) {
                     this.failedPathFindingPenalty = 0;
                  } else {
                     this.failedPathFindingPenalty += 10;
                  }
               } else {
                  this.failedPathFindingPenalty += 10;
               }
            }

            if (d0 > 1024.0D) {
               this.delayCounter += 10;
            } else if (d0 > 256.0D) {
               this.delayCounter += 5;
            }

            if (this.attacker.getParasiteStatus() <= 2 && this.attacker.getLLeap() < 1) {
               if (this.attacker.getDistanceSq(entitylivingbase) <= this.blockDistance && this.attacker.getAttackCooldownAni() != 0) {
                  if (!this.attacker.getNavigator().tryMoveToEntityLiving(entitylivingbase, 1.0D)) {
                     this.delayCounter += 15;
                  }

                  this.attacker.setParasiteStatus(1);
               } else {
                  if (!this.attacker.getNavigator().tryMoveToEntityLiving(entitylivingbase, this.speedTowardsTarget)) {
                     this.delayCounter += 15;
                  }

                  this.attacker.setParasiteStatus(2);
                  this.attacker.setAttackCooldownAni(0);
               }
            }
         }

         this.attackTick = Math.max(this.attackTick - 1, 0);
         this.checkAndPerformAttack(entitylivingbase, d0);
      }
   }

   protected void checkAndPerformAttack(EntityLivingBase target, double distance) {
      double d0 = this.getAttackReachSqr(target);
      if (!this.isTargetParasite(target)) {
         if (distance <= d0 && this.attackTick <= 0 && this.attacker.canEntityBeSeen(target)) {
            this.attackTick = this.attackCooldown;
            this.attacker.attackEntityAsMob(target);
         }

      }
   }

   protected double getAttackReachSqr(EntityLivingBase attackTarget) {
      return (double)(this.attacker.width * 2.0F * this.attacker.width * 2.0F + attackTarget.width);
   }

   protected boolean isTargetParasite(EntityLivingBase target) {
      if (SRPConfigSystems.useOneMind && target instanceof EntityParasiteBase) {
         if (((EntityParasiteBase)target).getAttackTarget() instanceof EntityParasiteBase) {
            this.attacker.setAttackTarget((EntityLivingBase)null);
            return true;
         } else {
            if (SRPPotions.applySense(this.attacker, 200, this.attacker.getDistanceSq(target), SRPConfigSystems.oneMinRangeCap)) {
               this.attacker.setAttackTarget(((EntityParasiteBase)target).getAttackTarget());
            } else {
               this.attacker.setAttackTarget((EntityLivingBase)null);
            }

            return true;
         }
      } else {
         return false;
      }
   }
}
