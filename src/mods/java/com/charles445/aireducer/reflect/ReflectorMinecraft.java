package com.charles445.aireducer.reflect;

import com.charles445.aireducer.util.ReflectUtil;
import java.lang.reflect.Field;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.ai.EntityAIAvoidEntity;
import net.minecraft.entity.ai.EntityAITasks;

public class ReflectorMinecraft {
   public static ReflectorMinecraft reflector;
   public final Class c_rabbit_AIAvoidEntity = Class.forName("net.minecraft.entity.passive.EntityRabbit$AIAvoidEntity");
   public final Field f_EntityLiving_navigator = ReflectUtil.findFieldAny(EntityLiving.class, "navigator", "navigator");
   public final Field f_EntityAITasks_tickRate = ReflectUtil.findFieldAny(EntityAITasks.class, "tickRate", "tickRate");
   public final Field f_EntityAIAvoidEntity_entity = ReflectUtil.findFieldAny(EntityAIAvoidEntity.class, "entity", "entity");
   public final Field f_EntityAIAvoidEntity_classToAvoid = ReflectUtil.findFieldAny(EntityAIAvoidEntity.class, "classToAvoid", "classToAvoid");
   public final Field f_EntityAIAvoidEntity_avoidTargetSelector = ReflectUtil.findFieldAny(EntityAIAvoidEntity.class, "avoidTargetSelector", "avoidTargetSelector");
   public final Field f_EntityAIAvoidEntity_avoidDistance = ReflectUtil.findFieldAny(EntityAIAvoidEntity.class, "avoidDistance", "avoidDistance");
   public final Field f_EntityAIAvoidEntity_farSpeed = ReflectUtil.findFieldAny(EntityAIAvoidEntity.class, "farSpeed", "farSpeed");
   public final Field f_EntityAIAvoidEntity_nearSpeed = ReflectUtil.findFieldAny(EntityAIAvoidEntity.class, "nearSpeed", "nearSpeed");

   public ReflectorMinecraft() throws Exception {
      reflector = this;
   }
}
