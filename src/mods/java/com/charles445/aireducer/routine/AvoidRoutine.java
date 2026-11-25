package com.charles445.aireducer.routine;

import com.charles445.aireducer.ai.AIAvoidReduced;
import com.charles445.aireducer.config.ModConfig;
import com.charles445.aireducer.reflect.ReflectorMinecraft;
import com.google.common.base.Function;
import com.google.common.base.Predicate;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.ai.EntityAIAvoidEntity;
import net.minecraft.entity.ai.EntityAIBase;

public class AvoidRoutine extends Routine {
   public AvoidRoutine() {
   }

   public boolean canRun() {
      return ModConfig.vanilla.avoidTaskReplacement && ReflectorMinecraft.reflector != null;
   }

   protected EntityAIBase constructAvoid(EntityCreature entity, Class<?> classToAvoid, Predicate<?> avoidTargetSelector, float avoidDistance, double farSpeed, double nearSpeed) {
      return new AIAvoidReduced(entity, classToAvoid, avoidTargetSelector, avoidDistance, farSpeed, nearSpeed) {
         public boolean getRadiusConfig() {
            return ModConfig.vanilla.avoidTaskReplacement;
         }
      };
   }

   protected void run(EntityLiving entity, String domain, String path) {
      if (entity instanceof EntityCreature) {
         this.findAndReplaceAvoids((EntityCreature)entity);
      }

   }

   private void findAndReplaceAvoids(EntityCreature entityIn) {
      this.tryAndReplaceAllTasks(entityIn, entityIn.tasks, EntityAIAvoidEntity.class, (oldTaskEntry) -> {
         try {
            Class<?> classToAvoid = (Class)ReflectorMinecraft.reflector.f_EntityAIAvoidEntity_classToAvoid.get(oldTaskEntry.action);
            Predicate<?> avoidTargetSelector = (Predicate)ReflectorMinecraft.reflector.f_EntityAIAvoidEntity_avoidTargetSelector.get(oldTaskEntry.action);
            float avoidDistance = ((Float)ReflectorMinecraft.reflector.f_EntityAIAvoidEntity_avoidDistance.get(oldTaskEntry.action)).floatValue();
            double farSpeed = ((Double)ReflectorMinecraft.reflector.f_EntityAIAvoidEntity_farSpeed.get(oldTaskEntry.action)).doubleValue();
            double nearSpeed = ((Double)ReflectorMinecraft.reflector.f_EntityAIAvoidEntity_nearSpeed.get(oldTaskEntry.action)).doubleValue();
            EntityCreature entity = (EntityCreature)ReflectorMinecraft.reflector.f_EntityAIAvoidEntity_entity.get(oldTaskEntry.action);
            return this.constructAvoid(entity, classToAvoid, avoidTargetSelector, avoidDistance, farSpeed, nearSpeed);
         } catch (Exception var10) {
            return null;
         }
      });
   }
}
