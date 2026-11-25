package com.charles445.aireducer.routine;

import com.charles445.aireducer.ai.WrappedProximityTask;
import com.charles445.aireducer.config.ModConfig;
import com.charles445.aireducer.reflect.ReflectorIAF;
import com.charles445.aireducer.reflect.ReflectorMinecraft;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.ai.EntityAIBase;

public class IAFDeathWormRoutine extends Routine {
   public IAFDeathWormRoutine() {
   }

   public boolean canRun() {
      return ModConfig.iceandfire.deathworm && ReflectorIAF.reflector != null && ReflectorMinecraft.reflector != null;
   }

   protected void run(EntityLiving entity, String domain, String path) {
      this.wrapTask(entity, entity.tasks, (Class)null, IAFDeathWormRoutine.WrappedProximityTaskDeathWorm.class);
      this.wrapTask(entity, entity.targetTasks, (Class)null, IAFDeathWormRoutine.WrappedProximityTaskDeathWorm.class);
   }

   public static class WrappedProximityTaskDeathWorm extends WrappedProximityTask {
      public WrappedProximityTaskDeathWorm(EntityLiving entity, EntityAIBase task) {
         super(entity, task);
      }

      public double getProximity() {
         return ModConfig.iceandfire.deathwormProximityRequirement;
      }

      public boolean canModify() {
         return ModConfig.iceandfire.deathwormProximityRequired;
      }
   }
}
