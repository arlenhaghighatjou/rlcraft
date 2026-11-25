package com.charles445.aireducer.ai.myrmex;

import com.charles445.aireducer.config.ModConfig;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.ai.EntityAIBase;

public class WrappedTaskMyrmexAIEscortEntity extends WrappedTaskMyrmexRunDelay {
   public WrappedTaskMyrmexAIEscortEntity(EntityLiving entity, EntityAIBase task) {
      super(entity, task);
   }

   public boolean canModify() {
      return ModConfig.iceandfire.myrmexModifyEscortEntity;
   }

   public int getRunDelay() {
      return ModConfig.iceandfire.myrmexRunDelayEscortEntity;
   }

   public double getUpdateChance() {
      return ModConfig.iceandfire.myrmexUpdateChanceEscortEntity;
   }
}
