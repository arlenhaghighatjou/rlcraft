package com.charles445.simpledifficulty.handler;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.event.entity.EntityMountEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class MiscHandler {
   public MiscHandler() {
   }

   @SubscribeEvent
   public void onDismount(EntityMountEvent event) {
      if (event.isDismounting() && event.getEntityMounting() instanceof EntityPlayer) {
         ((EntityPlayer)event.getEntityMounting()).setJumping(false);
      }

   }
}
