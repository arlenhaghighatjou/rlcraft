package com.charles445.aireducer.ai.myrmex;

import com.charles445.aireducer.config.ModConfig;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.ai.EntityAIBase;

public class WrappedTaskMyrmexAIForage extends WrappedTaskMyrmexRunDelay {
   public WrappedTaskMyrmexAIForage(EntityLiving entity, EntityAIBase task) {
      super(entity, task);
   }

   public boolean canModify() {
      return ModConfig.iceandfire.myrmexModifyForage;
   }

   public int getRunDelay() {
      return ModConfig.iceandfire.myrmexRunDelayForage;
   }

   public double getUpdateChance() {
      return ModConfig.iceandfire.myrmexUpdateChanceForage;
   }
}
