package com.charles445.aireducer.routine;

import com.charles445.aireducer.compat.iceandfireold.VillagerAIFearUntamedReduced;
import com.charles445.aireducer.config.ModConfig;
import com.charles445.aireducer.reflect.ReflectorIAFOld;
import com.charles445.aireducer.reflect.ReflectorMinecraft;
import com.charles445.aireducer.util.ErrorUtil;
import com.google.common.base.Function;
import com.google.common.base.Predicate;
import javax.annotation.Nullable;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.EntityAIBase;

public class IAFOldFearUntamedRoutine extends Routine {
   public IAFOldFearUntamedRoutine() {
   }

   public boolean canRun() {
      return ModConfig.iceandfireold.replaceVillageAIFearUntamed && ReflectorIAFOld.reflector != null && ReflectorMinecraft.reflector != null;
   }

   public void run(EntityLiving entity, String domain, String path) {
      try {
         if (((Boolean)ReflectorIAFOld.reflector.m_iceandfire_isLivestock.invoke((Object)null, entity)).booleanValue()) {
            this.tryReplaceIceAndFireLivestockFearTask(entity);
         } else if (((Boolean)ReflectorIAFOld.reflector.m_iceandfire_isVillager.invoke((Object)null, entity)).booleanValue()) {
            this.tryReplaceIceAndFireVillagerFearTask(entity);
         }
      } catch (Exception var5) {
         ErrorUtil.debugError("Failed to check isLivestock!", var5);
      }

   }

   private void tryReplaceIceAndFireVillagerFearTask(EntityLiving entity) {
      if (entity instanceof EntityCreature) {
         this.tryAndReplaceAllTasks(entity, entity.tasks, ReflectorIAFOld.reflector.c_iceandfire_VillagerAIFearUntamed, (task) -> {
            return this.makeIceAndFireVillagerFearTask((EntityCreature)entity);
         });
      }

   }

   private EntityAIBase makeIceAndFireVillagerFearTask(EntityCreature entity) {
      return new VillagerAIFearUntamedReduced(entity, EntityLivingBase.class, new Predicate<EntityLivingBase>() {
         public boolean apply(@Nullable EntityLivingBase targentity) {
            return targentity != null && ReflectorIAFOld.reflector.c_iceandfire_IVillagerFear.isInstance(targentity);
         }
      }, 12.0F, 0.8D, 0.8D);
   }

   private void tryReplaceIceAndFireLivestockFearTask(EntityLiving entity) {
      if (entity instanceof EntityCreature) {
         this.tryAndReplaceAllTasks(entity, entity.tasks, ReflectorIAFOld.reflector.c_iceandfire_VillagerAIFearUntamed, (task) -> {
            return this.makeIceAndFireLivestockFearTask((EntityCreature)entity);
         });
      }

   }

   private EntityAIBase makeIceAndFireLivestockFearTask(final EntityCreature entity) {
      return new VillagerAIFearUntamedReduced(entity, EntityLivingBase.class, new Predicate<EntityLivingBase>() {
         public boolean apply(@Nullable EntityLivingBase targentity) {
            try {
               return targentity != null && ReflectorIAFOld.reflector.c_iceandfire_IAnimalFear.isInstance(targentity) && ((Boolean)ReflectorIAFOld.reflector.m_iceandfire_shouldAnimalsFear.invoke(targentity, entity)).booleanValue();
            } catch (Exception var3) {
               ErrorUtil.debugError("Failed to makeIceAndFireLivestockFearTask!", var3);
               return false;
            }
         }
      }, 12.0F, 1.2D, 1.5D);
   }
}
