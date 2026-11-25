package com.charles445.aireducer.routine;

import com.charles445.aireducer.ai.myrmex.WrappedProximityTaskMyrmex;
import com.charles445.aireducer.ai.myrmex.WrappedTaskMyrmexAIEscortEntity;
import com.charles445.aireducer.ai.myrmex.WrappedTaskMyrmexAIFindHidingSpot;
import com.charles445.aireducer.ai.myrmex.WrappedTaskMyrmexAIForage;
import com.charles445.aireducer.ai.myrmex.WrappedTaskMyrmexAILeaveHive;
import com.charles445.aireducer.ai.myrmex.WrappedTaskMyrmexAIMoveThroughHive;
import com.charles445.aireducer.ai.myrmex.WrappedTaskMyrmexAIWanderHiveCenter;
import com.charles445.aireducer.config.ModConfig;
import com.charles445.aireducer.reflect.ReflectorIAF;
import com.charles445.aireducer.reflect.ReflectorMinecraft;
import com.charles445.aireducer.util.ErrorUtil;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.SharedMonsterAttributes;

public class IAFMyrmexRoutine extends Routine {
   public IAFMyrmexRoutine() {
   }

   public boolean canRun() {
      return ModConfig.iceandfire.myrmex && ReflectorIAF.reflector != null && ReflectorMinecraft.reflector != null;
   }

   protected void run(EntityLiving entity, String domain, String path) {
      ErrorUtil.debugDebug("Applying myrmex follow range: " + ModConfig.iceandfireold.myrmexFollowRange);
      entity.getEntityAttribute(SharedMonsterAttributes.FOLLOW_RANGE).setBaseValue(ModConfig.iceandfireold.myrmexFollowRange);
      ErrorUtil.debugDebug("Current Tasks Count: " + entity.tasks.taskEntries.size());
      this.wrapTask(entity, entity.tasks, ReflectorIAF.reflector.c_iceandfire_MyrmexAIEscortEntity, WrappedTaskMyrmexAIEscortEntity.class);
      this.wrapTask(entity, entity.tasks, ReflectorIAF.reflector.c_iceandfire_MyrmexAIFindHidingSpot, WrappedTaskMyrmexAIFindHidingSpot.class);
      this.wrapTask(entity, entity.tasks, ReflectorIAF.reflector.c_iceandfire_MyrmexAIForage, WrappedTaskMyrmexAIForage.class);
      this.wrapTask(entity, entity.tasks, ReflectorIAF.reflector.c_iceandfire_MyrmexAILeaveHive, WrappedTaskMyrmexAILeaveHive.class);
      this.wrapTask(entity, entity.tasks, ReflectorIAF.reflector.c_iceandfire_MyrmexAIMoveThroughHive, WrappedTaskMyrmexAIMoveThroughHive.class);
      this.wrapTask(entity, entity.tasks, ReflectorIAF.reflector.c_iceandfire_MyrmexAIWanderHiveCenter, WrappedTaskMyrmexAIWanderHiveCenter.class);
      this.wrapTask(entity, entity.tasks, (Class)null, WrappedProximityTaskMyrmex.class);
      this.wrapTask(entity, entity.targetTasks, (Class)null, WrappedProximityTaskMyrmex.class);
      ErrorUtil.debugDebug("Final Tasks Count: " + entity.tasks.taskEntries.size());
   }
}
