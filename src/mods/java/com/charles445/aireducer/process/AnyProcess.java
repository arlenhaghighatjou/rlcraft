package com.charles445.aireducer.process;

import com.charles445.aireducer.reflect.ReflectorMinecraft;
import com.charles445.aireducer.routine.IAFOldFearUntamedRoutine;
import com.charles445.aireducer.routine.Routine;
import com.charles445.aireducer.routine.TaskDelayRoutine;
import net.minecraft.entity.EntityLiving;

public class AnyProcess extends ModProcess {
   private Routine iafFearUntamedRoutine = new IAFOldFearUntamedRoutine();
   private Routine taskDelayRoutine = new TaskDelayRoutine();

   public AnyProcess() {
   }

   public boolean canUse() {
      return ReflectorMinecraft.reflector != null;
   }

   public void handle(EntityLiving entity, String domain, String path) {
      this.taskDelayRoutine.runRoutine(entity, domain, path);
      this.iafFearUntamedRoutine.runRoutine(entity, domain, path);
   }
}
