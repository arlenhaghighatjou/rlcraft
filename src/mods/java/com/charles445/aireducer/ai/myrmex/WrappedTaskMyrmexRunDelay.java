package com.charles445.aireducer.ai.myrmex;

import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.ai.EntityAIBase;

public abstract class WrappedTaskMyrmexRunDelay extends WrappedTaskMyrmex {
   int lastRun = -10000;

   public WrappedTaskMyrmexRunDelay(EntityLiving entity, EntityAIBase task) {
      super(entity, task);
   }

   public abstract boolean canModify();

   public abstract int getRunDelay();

   public abstract double getUpdateChance();

   public boolean shouldExecute() {
      if (!this.canModify()) {
         return super.shouldExecute();
      } else {
         return this.lastRun < this.entity.ticksExisted - this.getRunDelay() ? super.shouldExecute() : false;
      }
   }

   public void startExecuting() {
      if (!this.canModify()) {
         super.startExecuting();
      } else {
         this.lastRun = this.entity.ticksExisted;
         super.startExecuting();
      }
   }

   public void updateTask() {
      if (!this.canModify()) {
         super.updateTask();
      } else {
         double updateChance = this.getUpdateChance();
         if (updateChance > 0.0D) {
            if (updateChance >= 1.0D) {
               super.updateTask();
            } else if (this.entity.getRNG().nextDouble() < updateChance) {
               super.updateTask();
            }
         }
      }
   }
}
