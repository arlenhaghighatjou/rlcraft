package com.lycanitesmobs.core.entity.goals.targeting;

import com.lycanitesmobs.core.entity.ExtendedEntity;
import com.lycanitesmobs.core.entity.TameableCreatureEntity;
import net.minecraft.entity.EntityLivingBase;

public class CopyOwnerAttackTargetGoal extends TargetingGoal {
   private TameableCreatureEntity host;
   private int lastAttackTime;

   public CopyOwnerAttackTargetGoal(TameableCreatureEntity setHost) {
      super(setHost);
      this.host = setHost;
      this.checkSight = false;
   }

   public boolean shouldExecute() {
      if (!this.host.isTamed()) {
         return false;
      } else if (this.host.isSitting()) {
         return false;
      } else if (!this.host.isAssisting() && !this.host.isAggressive()) {
         return false;
      } else if (this.host.getOwner() == null) {
         return false;
      } else if (!(this.host.getOwner() instanceof EntityLivingBase)) {
         return false;
      } else if (this.host.isAggressive() && this.isValidTarget(this.target)) {
         this.setTarget(this.target);
         return true;
      } else {
         EntityLivingBase owner = (EntityLivingBase)this.host.getOwner();
         int lastAttackedTime = owner.getLastAttackedEntityTime();
         this.target = owner.getLastAttackedEntity();
         if (this.target == null) {
            ExtendedEntity extendedOwner = ExtendedEntity.getForEntity(owner);
            if (extendedOwner != null) {
               this.target = extendedOwner.lastAttackedEntity;
               lastAttackedTime = extendedOwner.lastAttackedTime;
            }
         }

         if (this.target == null) {
            return false;
         } else {
            return this.lastAttackTime != lastAttackedTime;
         }
      }
   }

   public void startExecuting() {
      if (this.isEntityTargetable(this.target, false)) {
         this.lastAttackTime = ((EntityLivingBase)this.host.getOwner()).getLastAttackedEntityTime();
         super.startExecuting();
      }

   }

   public boolean shouldContinueExecuting() {
      return !this.host.isSitting() && this.isValidTarget(this.getTarget()) ? super.shouldContinueExecuting() : false;
   }

   protected boolean isValidTarget(EntityLivingBase target) {
      if (target == null) {
         return false;
      } else if (!target.isEntityAlive()) {
         return false;
      } else if (target == this.host) {
         return false;
      } else if (!this.host.canAttackClass(target.getClass())) {
         return false;
      } else {
         return this.host.canAttackEntity(target);
      }
   }

   protected EntityLivingBase getTarget() {
      return this.host.getAttackTarget();
   }

   protected void setTarget(EntityLivingBase newTarget) {
      this.host.setAttackTarget(newTarget);
   }
}
