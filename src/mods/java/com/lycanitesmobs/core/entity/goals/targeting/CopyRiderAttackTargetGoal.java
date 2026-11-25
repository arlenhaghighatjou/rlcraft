package com.lycanitesmobs.core.entity.goals.targeting;

import com.lycanitesmobs.core.entity.TameableCreatureEntity;
import net.minecraft.entity.EntityLivingBase;

public class CopyRiderAttackTargetGoal extends TargetingGoal {
   private TameableCreatureEntity host;
   private int lastAttackTime;

   public CopyRiderAttackTargetGoal(TameableCreatureEntity setHost) {
      super(setHost);
      this.host = setHost;
      this.checkSight = false;
   }

   public boolean shouldExecute() {
      if (!this.host.hasRiderTarget()) {
         return false;
      } else if (this.host.isSitting()) {
         return false;
      } else if (this.host.getRider() == null) {
         return false;
      } else {
         this.target = this.host.getRider().getLastAttackedEntity();
         if (this.target == null) {
            return false;
         } else {
            return this.lastAttackTime != this.host.getRider().getLastAttackedEntityTime();
         }
      }
   }

   public void startExecuting() {
      if (this.isTargetValid(this.target)) {
         this.lastAttackTime = this.host.getRider().getLastAttackedEntityTime();
         super.startExecuting();
      }

   }

   public boolean shouldContinueExecuting() {
      if (!this.host.hasRiderTarget()) {
         return false;
      } else {
         return !this.host.isSitting() && this.isValidTarget(this.getTarget()) ? super.shouldContinueExecuting() : false;
      }
   }

   private boolean isTargetValid(EntityLivingBase target) {
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
