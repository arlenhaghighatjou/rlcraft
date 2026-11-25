package com.charles445.aireducer.routine;

import com.charles445.aireducer.ai.WrappedProximityTask;
import com.charles445.aireducer.config.ModConfig;
import com.charles445.aireducer.reflect.ReflectorIAFOld;
import com.charles445.aireducer.reflect.ReflectorMinecraft;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.ai.EntityAIBase;

public class IAFOldDeathWormRoutine extends Routine {
   public IAFOldDeathWormRoutine() {
   }

   public boolean canRun() {
      return ModConfig.iceandfireold.deathworm && ReflectorIAFOld.reflector != null && ReflectorMinecraft.reflector != null;
   }

   protected void run(EntityLiving entity, String domain, String path) {
      this.wrapTask(entity, entity.tasks, (Class)null, IAFOldDeathWormRoutine.WrappedProximityTaskOldDeathWorm.class);
      this.wrapTask(entity, entity.targetTasks, (Class)null, IAFOldDeathWormRoutine.WrappedProximityTaskOldDeathWorm.class);
   }

   public static class WrappedProximityTaskOldDeathWorm extends WrappedProximityTask {
      public WrappedProximityTaskOldDeathWorm(EntityLiving entity, EntityAIBase task) {
         super(entity, task);
      }

      public double getProximity() {
         return ModConfig.iceandfireold.deathwormProximityRequirement;
      }

      public boolean canModify() {
         return ModConfig.iceandfireold.deathwormProximityRequired;
      }
   }
}
