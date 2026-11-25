package com.charles445.aireducer.ai.myrmexold;

import com.charles445.aireducer.config.ModConfig;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.ai.EntityAIBase;

public class WrappedTaskOldMyrmexAIReEnterHive extends WrappedTaskOldMyrmex {
   public WrappedTaskOldMyrmexAIReEnterHive(EntityLiving entity, EntityAIBase task) {
      super(entity, task);
   }

   public double getUpdateChance() {
      return ModConfig.iceandfireold.myrmexUpdateChanceReEnterHive;
   }

   public void updateTask() {
      if (this.entity.getRNG().nextDouble() < this.getUpdateChance()) {
         this.task.updateTask();
      }

   }
}
