package com.dhanantry.scapeandrunparasites.entity.ai;

import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityParasiteBase;
import com.dhanantry.scapeandrunparasites.init.SRPPotions;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.IRangedAttackMob;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.util.math.MathHelper;

public class EntityAIAttackRangedStatus extends EntityAIBase {
   private final EntityParasiteBase entityHost;
   private final IRangedAttackMob rangedAttackEntityHost;
   private EntityLivingBase attackTarget;
   private int rangedAttackTime;
   private final double entityMoveSpeed;
   private int seeTime;
   private final int attackIntervalMin;
   private final int maxRangedAttackTime;
   private final float attackRadius;
   private final float maxAttackDistance;

   public EntityAIAttackRangedStatus(IRangedAttackMob attacker, double movespeed, int maxAttackTime, float maxAttackDistanceIn) {
      this(attacker, movespeed, maxAttackTime, maxAttackTime, maxAttackDistanceIn);
   }

   public EntityAIAttackRangedStatus(IRangedAttackMob attacker, double movespeed, int IntervalMin, int maxAttackTime, float maxAttackDistanceIn) {
      this.rangedAttackTime = -1;
      if (!(attacker instanceof EntityLivingBase)) {
         throw new IllegalArgumentException("ArrowAttackGoal requires Mob implements RangedAttackMob");
      } else {
         this.rangedAttackEntityHost = attacker;
         this.entityHost = (EntityParasiteBase)attacker;
         this.entityMoveSpeed = movespeed;
         this.attackIntervalMin = IntervalMin;
         this.maxRangedAttackTime = maxAttackTime;
         this.attackRadius = maxAttackDistanceIn;
         this.maxAttackDistance = maxAttackDistanceIn * maxAttackDistanceIn;
         this.setMutexBits(3);
      }
   }

   public boolean shouldExecute() {
      EntityLivingBase entitylivingbase = this.entityHost.getAttackTarget();
      if (entitylivingbase != null && !this.entityHost.shouldWorkTask()) {
         this.attackTarget = entitylivingbase;
         return true;
      } else {
         return false;
      }
   }

   public boolean shouldContinueExecuting() {
      return (this.shouldExecute() || !this.entityHost.getNavigator().noPath()) && !this.entityHost.shouldWorkTask();
   }

   public void resetTask() {
      this.attackTarget = null;
      this.seeTime = 0;
      this.rangedAttackTime = -1;
   }

   public void updateTask() {
      this.entityHost.setParasiteStatus(2);
      if (this.attackTarget == null) {
         this.resetTask();
      } else if (!this.attackTarget.isEntityAlive()) {
         this.resetTask();
      } else {
         double d0 = this.entityHost.getDistanceSq(this.attackTarget.posX, this.attackTarget.getEntityBoundingBox().minY, this.attackTarget.posZ);
         boolean flag = this.entityHost.getEntitySenses().canSee(this.attackTarget);
         if (flag) {
            ++this.seeTime;
         } else {
            this.seeTime = 0;
         }

         if (d0 <= (double)this.maxAttackDistance && this.seeTime >= 20) {
            this.entityHost.getNavigator().clearPath();
         } else {
            this.entityHost.getNavigator().tryMoveToEntityLiving(this.attackTarget, this.entityMoveSpeed);
         }

         if (this.entityHost.isPotionActive(SRPPotions.RAGE_E)) {
            --this.rangedAttackTime;
         }

         if (--this.rangedAttackTime == 0) {
            if (!flag) {
               return;
            }

            float f = MathHelper.sqrt(d0) / this.attackRadius;
            float lvt_5_1_ = MathHelper.clamp(f, 0.1F, 1.0F);
            this.rangedAttackEntityHost.attackEntityWithRangedAttack(this.attackTarget, lvt_5_1_);
            this.entityHost.resetIdleTime();
            this.rangedAttackTime = MathHelper.floor(f * (float)(this.maxRangedAttackTime - this.attackIntervalMin) + (float)this.attackIntervalMin);
         } else if (this.rangedAttackTime < 0) {
            float f2 = MathHelper.sqrt(d0) / this.attackRadius;
            this.rangedAttackTime = MathHelper.floor(f2 * (float)(this.maxRangedAttackTime - this.attackIntervalMin) + (float)this.attackIntervalMin);
         }
      }

   }
}
