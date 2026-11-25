package com.charles445.aireducer.routine;

import com.charles445.aireducer.reflect.ReflectorMinecraft;
import com.charles445.aireducer.util.ErrorUtil;
import net.minecraft.entity.EntityLiving;

public class PrintRoutine extends Routine {
   public PrintRoutine() {
   }

   public boolean canRun() {
      return ReflectorMinecraft.reflector != null;
   }

   protected void run(EntityLiving entity, String domain, String path) {
      ErrorUtil.debugDebug("PrintRoutine: " + domain + ":" + path);
      ErrorUtil.debugDebug("Tasks");
      this.debugPrintTasks(entity.tasks);
      ErrorUtil.debugDebug("Target Tasks");
      this.debugPrintTasks(entity.targetTasks);
   }
}
