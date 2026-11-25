package com.charles445.aireducer.routine;

import com.charles445.aireducer.ai.AIAvoidReducedRabbit;
import com.charles445.aireducer.config.ModConfig;
import com.charles445.aireducer.reflect.ReflectorMinecraft;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.passive.EntityRabbit;
import net.minecraft.entity.passive.EntityWolf;
import net.minecraft.entity.player.EntityPlayer;

public class RabbitRoutine extends Routine {
   public RabbitRoutine() {
   }

   public boolean canRun() {
      return ModConfig.vanilla.rabbit && ReflectorMinecraft.reflector != null;
   }

   protected void run(EntityLiving entity, String domain, String path) {
      if (entity instanceof EntityRabbit) {
         EntityRabbit entityRabbit = (EntityRabbit)entity;
         if (this.removeAllTasksOfClass(entityRabbit, ReflectorMinecraft.reflector.c_rabbit_AIAvoidEntity)) {
            entityRabbit.tasks.addTask(4, new AIAvoidReducedRabbit(entityRabbit, EntityPlayer.class, 8.0F, 2.2D, 2.2D));
            entityRabbit.tasks.addTask(4, new AIAvoidReducedRabbit(entityRabbit, EntityWolf.class, 10.0F, 2.2D, 2.2D));
            entityRabbit.tasks.addTask(4, new AIAvoidReducedRabbit(entityRabbit, EntityMob.class, 4.0F, 2.2D, 2.2D));
         }
      }

   }
}
