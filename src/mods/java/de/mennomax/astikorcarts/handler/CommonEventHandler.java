package de.mennomax.astikorcarts.handler;

import net.minecraftforge.fml.common.eventhandler.EventBus;
import de.mennomax.astikorcarts.capabilities.PullProvider;
import de.mennomax.astikorcarts.entity.ai.EntityAIPullCart;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;

public class CommonEventHandler {
   public CommonEventHandler() {
   }

   public static void register(EventBus bus) {
      bus.registerGeneric(AttachCapabilitiesEvent.class, Entity.class, CommonEventHandler::onAttachCapability);
      bus.register(EntityJoinWorldEvent.class, CommonEventHandler::onEntityJoinWorld);
   }

   public static void onAttachCapability(AttachCapabilitiesEvent<Entity> event) {
      if (((Entity)event.getObject()).world != null && !((Entity)event.getObject()).world.isRemote) {
         event.addCapability(new ResourceLocation("astikorcarts"), new PullProvider());
      }

   }

   public static void onEntityJoinWorld(EntityJoinWorldEvent event) {
      if (event.getEntity() instanceof EntityLiving) {
         ((EntityLiving)event.getEntity()).tasks.addTask(2, new EntityAIPullCart((EntityLiving)event.getEntity()));
      }

   }
}
