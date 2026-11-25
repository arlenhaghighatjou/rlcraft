package com.charles445.aireducer.ai;

import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.ai.EntityAIBase;

public abstract class WrappedTask extends EntityAIBase {
   public EntityAIBase task;
   public EntityLiving entity;

   public WrappedTask(EntityLiving entity, EntityAIBase task) {
      this.task = task;
      this.entity = entity;
   }

   public boolean shouldExecute() {
      return this.task.shouldExecute();
   }

   public boolean shouldContinueExecuting() {
      return this.task.shouldContinueExecuting();
   }

   public boolean isInterruptible() {
      return this.task.isInterruptible();
   }

   public void startExecuting() {
      this.task.startExecuting();
   }

   public void resetTask() {
      this.task.resetTask();
   }

   public void updateTask() {
      this.task.updateTask();
   }

   public void setMutexBits(int mutexBitsIn) {
      this.task.setMutexBits(mutexBitsIn);
      super.setMutexBits(mutexBitsIn);
   }

   public int getMutexBits() {
      return this.task.getMutexBits();
   }
}
