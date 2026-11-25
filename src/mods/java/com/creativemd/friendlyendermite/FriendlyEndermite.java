package com.creativemd.friendlyendermite;

import java.lang.reflect.Field;
import net.minecraft.entity.ai.EntityAINearestAttackableTarget;
import net.minecraft.entity.ai.EntityAITasks.EntityAITaskEntry;
import net.minecraft.entity.monster.EntityEnderman;
import net.minecraft.entity.monster.EntityEndermite;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.ReflectionHelper;

@EventBusSubscriber
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
   private static Field targetClassField = ReflectionHelper.findField(EntityAINearestAttackableTarget.class, new String[]{"targetClass", "targetClass"});

   public FriendlyEndermite() {
   }

   @SubscribeEvent
   public static void entitySpawned(EntityJoinWorldEvent event) {
      if (event.getEntity() instanceof EntityEnderman) {
         EntityEnderman man = (EntityEnderman)event.getEntity();

         try {
            for(EntityAITaskEntry task : man.targetTasks.taskEntries) {
               if (task.action instanceof EntityAINearestAttackableTarget && targetClassField.get(task.action) == EntityEndermite.class) {
                  man.targetTasks.removeTask(task.action);
                  return;
               }
            }
         } catch (IllegalAccessException | IllegalArgumentException var4) {
            var4.printStackTrace();
         }
      }

   }
}
