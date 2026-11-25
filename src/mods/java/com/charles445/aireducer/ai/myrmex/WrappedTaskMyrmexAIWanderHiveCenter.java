package com.charles445.aireducer.ai.myrmex;

import com.charles445.aireducer.config.ModConfig;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.ai.EntityAIBase;

public class WrappedTaskMyrmexAIWanderHiveCenter extends WrappedTaskMyrmexRunDelay {
   public WrappedTaskMyrmexAIWanderHiveCenter(EntityLiving entity, EntityAIBase task) {
      super(entity, task);
   }

   public boolean canModify() {
      return ModConfig.iceandfire.myrmexModifyWanderHiveCenter;
   }

   public int getRunDelay() {
      return ModConfig.iceandfire.myrmexRunDelayWanderHiveCenter;
   }

   public double getUpdateChance() {
      return ModConfig.iceandfire.myrmexUpdateChanceWanderHiveCenter;
   }
}
