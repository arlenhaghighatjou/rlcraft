package com.charles445.aireducer.routine;

import com.charles445.aireducer.config.JsonConfig;
import com.charles445.aireducer.config.ModConfig;
import com.charles445.aireducer.reflect.ReflectorMinecraft;
import com.charles445.aireducer.util.ErrorUtil;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import net.minecraft.entity.EntityLiving;

public class TaskDelayRoutine extends Routine {
   public Map<String, Integer> taskDelayMap = new ConcurrentHashMap();

   public TaskDelayRoutine() {
      this.taskDelayMap.putAll(JsonConfig.taskDelay);
   }

   public boolean canRun() {
      return ModConfig.any.taskDelayTweaks && ReflectorMinecraft.reflector != null;
   }

   protected void run(EntityLiving entity, String domain, String path) {
      String resource = domain + ':' + path;
      Integer override = (Integer)this.taskDelayMap.get(resource);
      if (override != null && override.intValue() > 3) {
         ErrorUtil.debugDebug("Applying " + resource + " delay: " + override);
         this.applyTickRate(entity.tasks, override.intValue());
         this.applyTickRate(entity.targetTasks, override.intValue());
      }

   }
}
