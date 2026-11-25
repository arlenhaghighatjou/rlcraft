package com.yungnickyoung.minecraft.yungslaw.event;

import com.yungnickyoung.minecraft.yungslaw.YungsLaw;
import net.minecraftforge.event.world.WorldEvent.Unload;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class EventWorld {
   public EventWorld() {
   }

   @SubscribeEvent
   public void onWorldUnload(Unload event) {
      YungsLaw.configMap.remove(Integer.valueOf(event.getWorld().provider.getDimension()));
   }
}
