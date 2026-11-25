package com.charles445.aireducer.routine;

import com.charles445.aireducer.ai.myrmexold.PathNavigateOldMyrmexAlternate;
import com.charles445.aireducer.ai.myrmexold.WrappedProximityTaskOldMyrmex;
import com.charles445.aireducer.ai.myrmexold.WrappedTaskOldMyrmexAIEscortEntity;
import com.charles445.aireducer.ai.myrmexold.WrappedTaskOldMyrmexAIFindHidingSpot;
import com.charles445.aireducer.ai.myrmexold.WrappedTaskOldMyrmexAIForage;
import com.charles445.aireducer.ai.myrmexold.WrappedTaskOldMyrmexAILeaveHive;
import com.charles445.aireducer.ai.myrmexold.WrappedTaskOldMyrmexAIMoveThroughHive;
import com.charles445.aireducer.ai.myrmexold.WrappedTaskOldMyrmexAIReEnterHive;
import com.charles445.aireducer.config.ModConfig;
import com.charles445.aireducer.reflect.ReflectorIAFOld;
import com.charles445.aireducer.reflect.ReflectorMinecraft;
import com.charles445.aireducer.util.ErrorUtil;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.SharedMonsterAttributes;

public class IAFOldMyrmexRoutine extends Routine {
   public IAFOldMyrmexRoutine() {
   }

   public boolean canRun() {
      return ModConfig.iceandfireold.myrmex && ReflectorIAFOld.reflector != null && ReflectorMinecraft.reflector != null;
   }

   protected void run(EntityLiving entity, String domain, String path) {
      if (ModConfig.iceandfireold.myrmexAlternateNavigation && !path.equals("myrmex_royal") && !path.equals("myrmex_queen")) {
         ErrorUtil.debugDebug("Swapping out myrmex navigator");
         this.setNavigator(entity, new PathNavigateOldMyrmexAlternate(entity, entity.getEntityWorld()));
      }

      entity.getEntityAttribute(SharedMonsterAttributes.FOLLOW_RANGE).setBaseValue(ModConfig.iceandfireold.myrmexFollowRange);
      ErrorUtil.debugDebug("Current Tasks Count: " + entity.tasks.taskEntries.size());
      this.wrapTask(entity, entity.tasks, ReflectorIAFOld.reflector.c_iceandfire_MyrmexAIEscortEntity, WrappedTaskOldMyrmexAIEscortEntity.class);
      this.wrapTask(entity, entity.tasks, ReflectorIAFOld.reflector.c_iceandfire_MyrmexAIForage, WrappedTaskOldMyrmexAIForage.class);
      this.wrapTask(entity, entity.tasks, ReflectorIAFOld.reflector.c_iceandfire_MyrmexAIFindHidingSpot, WrappedTaskOldMyrmexAIFindHidingSpot.class);
      this.wrapTask(entity, entity.tasks, ReflectorIAFOld.reflector.c_iceandfire_MyrmexAILeaveHive, WrappedTaskOldMyrmexAILeaveHive.class);
      this.wrapTask(entity, entity.tasks, ReflectorIAFOld.reflector.c_iceandfire_MyrmexAIMoveThroughHive, WrappedTaskOldMyrmexAIMoveThroughHive.class);
      this.wrapTask(entity, entity.tasks, ReflectorIAFOld.reflector.c_iceandfire_MyrmexAIReEnterHive, WrappedTaskOldMyrmexAIReEnterHive.class);
      this.wrapTask(entity, entity.tasks, (Class)null, WrappedProximityTaskOldMyrmex.class);
      this.wrapTask(entity, entity.targetTasks, (Class)null, WrappedProximityTaskOldMyrmex.class);
      ErrorUtil.debugDebug("Final Tasks Count: " + entity.tasks.taskEntries.size());
   }
}
