package com.elenai.elenaidodge.event;

import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.Phase;
import net.minecraftforge.fml.common.gameevent.TickEvent.PlayerTickEvent;

public class PotionTickEventListener {
   public PotionTickEventListener() {
   }

   @SubscribeEvent
   public void onPotionTickEvent(PlayerTickEvent event) {
      if (event.phase == Phase.END) {
         ;
      }

   }
}
