package com.elenai.elenaidodge.event;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.client.event.EntityViewRenderEvent.CameraSetup;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class CameraEventListener {
   public CameraEventListener() {
   }

   @SubscribeEvent
   public void camera(CameraSetup event) {
      if (event.getEntity() instanceof EntityPlayer) {
         ;
      }

   }
}
