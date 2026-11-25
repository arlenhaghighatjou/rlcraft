package com.fantasticsource.mctools;

import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.ai.EntityAIBase;

public class NPEAttackTargetTaskHolder extends EntityAIBase {
   private EntityLiving entity;
   private EntityAIBase badAI;

   public NPEAttackTargetTaskHolder(EntityLiving entity, EntityAIBase badAI) {
      this.entity = entity;
      this.badAI = badAI;
      this.setMutexBits(badAI.getMutexBits());
   }

   public boolean shouldExecute() {
      return this.entity.getAttackTarget() != null && this.badAI.shouldExecute();
   }

   public void startExecuting() {
      if (this.entity.getAttackTarget() != null) {
         this.badAI.startExecuting();
      }

   }

   public boolean shouldContinueExecuting() {
      return this.entity.getAttackTarget() != null && this.badAI.shouldContinueExecuting();
   }

   public void updateTask() {
      if (this.entity.getAttackTarget() != null) {
         this.badAI.updateTask();
      }

   }

   public boolean isInterruptible() {
      return this.badAI.isInterruptible();
   }

   public int getMutexBits() {
      return this.badAI.getMutexBits();
   }

   public void resetTask() {
      this.badAI.resetTask();
   }

   public EntityAIBase getBadAI() {
      return this.badAI;
   }
}
