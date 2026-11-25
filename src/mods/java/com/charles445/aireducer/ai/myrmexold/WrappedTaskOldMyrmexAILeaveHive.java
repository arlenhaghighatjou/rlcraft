package com.charles445.aireducer.ai.myrmexold;

import com.charles445.aireducer.config.ModConfig;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.ai.EntityAIBase;

public class WrappedTaskOldMyrmexAILeaveHive extends WrappedTaskOldMyrmex {
   public WrappedTaskOldMyrmexAILeaveHive(EntityLiving entity, EntityAIBase task) {
      super(entity, task);
   }

   public void resetTask() {
      if (!ModConfig.iceandfireold.myrmexModifyLeaveHive) {
         super.resetTask();
      }
   }

   public boolean shouldExecute() {
      if (!ModConfig.iceandfireold.myrmexModifyLeaveHive) {
         return super.shouldExecute();
      } else if (this.reflector.c_EntityMyrmexQueen.isInstance(this.entity)) {
         return false;
      } else if (this.entity.isChild()) {
         return false;
      } else if (!this.reflector.canMove(this.entity)) {
         return false;
      } else if (!this.reflector.shouldLeaveHive(this.entity)) {
         return false;
      } else if (this.reflector.shouldEnterHive(this.entity)) {
         return false;
      } else {
         boolean shouldExecute = super.shouldExecute();
         return shouldExecute;
      }
   }

   public boolean shouldContinueExecuting() {
      if (!ModConfig.iceandfireold.myrmexModifyLeaveHive) {
         return super.shouldContinueExecuting();
      } else if (!this.reflector.shouldLeaveHive(this.entity)) {
         return false;
      } else if (this.entity.getNavigator().noPath()) {
         return false;
      } else {
         double distanceCheck = this.entity.getDistanceSq(this.reflector.getMyrmexAILeaveHive_nextEntrance(this.task));
         if (distanceCheck <= 3.0D) {
            return false;
         } else {
            return !this.reflector.shouldEnterHive(this.entity);
         }
      }
   }
}
