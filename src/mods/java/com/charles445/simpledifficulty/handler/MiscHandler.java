package com.charles445.simpledifficulty.handler;

import net.minecraftforge.fml.common.eventhandler.EventBus;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.event.entity.EntityMountEvent;

public class MiscHandler {
   public MiscHandler() {
   }

   public void register(EventBus bus) {
      bus.register(EntityMountEvent.class, this::onDismount);
   }

   public void onDismount(EntityMountEvent event) {
      if (event.isDismounting() && event.getEntityMounting() instanceof EntityPlayer) {
         ((EntityPlayer)event.getEntityMounting()).setJumping(false);
      }

   }
}
