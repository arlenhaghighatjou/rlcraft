package com.charles445.aireducer.ai.myrmex;

import com.charles445.aireducer.config.ModConfig;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.ai.EntityAIBase;

public class WrappedTaskMyrmexAIMoveThroughHive extends WrappedTaskMyrmexRunDelay {
   public WrappedTaskMyrmexAIMoveThroughHive(EntityLiving entity, EntityAIBase task) {
      super(entity, task);
   }

   public boolean canModify() {
      return ModConfig.iceandfire.myrmexModifyMoveThroughHive;
   }

   public int getRunDelay() {
      return ModConfig.iceandfire.myrmexRunDelayMoveThroughHive;
   }

   public double getUpdateChance() {
      return ModConfig.iceandfire.myrmexUpdateChanceMoveThroughHive;
   }
}
