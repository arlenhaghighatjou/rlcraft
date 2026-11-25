package com.creativemd.friendlyendermite;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.event.FMLConstructionEvent;
import net.minecraftforge.fml.common.event.FMLEvent;
import net.minecraftforge.fml.common.eventhandler.EventBus;
import net.minecraft.entity.ai.EntityAINearestAttackableTarget;
import net.minecraft.entity.ai.EntityAITasks.EntityAITaskEntry;
import net.minecraft.entity.monster.EntityEnderman;
import net.minecraft.entity.monster.EntityEndermite;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.fml.common.Mod;

@Mod(
   modid = "friendlyendermite",
   name = "Friendly Endermite",
   version = "1.0",
   acceptableRemoteVersions = "*",
   acceptedMinecraftVersions = "[1.12.2]"
)
public class FriendlyEndermite {
   public static final String modid = "friendlyendermite";
   public static final String version = "1.0";

   public FriendlyEndermite() {
   }

   public void handleModStateEvent(FMLEvent event) {
      if (event instanceof FMLConstructionEvent) {
         register(MinecraftForge.EVENT_BUS);
      }

   }

   public static void register(EventBus bus) {
      bus.register(EntityJoinWorldEvent.class, FriendlyEndermite::entitySpawned);
   }

   public static void entitySpawned(EntityJoinWorldEvent event) {
      if (event.getEntity() instanceof EntityEnderman) {
         EntityEnderman man = (EntityEnderman)event.getEntity();

         for (EntityAITaskEntry task : man.targetTasks.taskEntries) {
            if (task.action instanceof EntityAINearestAttackableTarget
                  && ((EntityAINearestAttackableTarget<?>) task.action).getTargetClass() == EntityEndermite.class) {
               man.targetTasks.removeTask(task.action);
               return;
            }
         }
      }

   }
}
