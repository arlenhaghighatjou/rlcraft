package com.yungnickyoung.minecraft.bettercaves.event;

import net.minecraftforge.fml.common.eventhandler.EventBus;
import net.minecraftforge.common.config.ConfigManager;
import net.minecraftforge.common.config.Config.Type;
import net.minecraftforge.fml.client.event.ConfigChangedEvent.OnConfigChangedEvent;

public class EventConfigReload {
   public EventConfigReload() {
   }

   public void register(EventBus bus) {
      bus.register(OnConfigChangedEvent.class, this::onConfigReload);
   }

   public void onConfigReload(OnConfigChangedEvent event) {
      if ("bettercaves".equals(event.getModID())) {
         ConfigManager.sync("bettercaves", Type.INSTANCE);
      }

   }
}
