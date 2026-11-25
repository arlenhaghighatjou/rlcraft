package com.yungnickyoung.minecraft.bettermineshafts.event;

import com.yungnickyoung.minecraft.bettermineshafts.integration.Integrations;
import net.minecraftforge.common.config.ConfigManager;
import net.minecraftforge.common.config.Config.Type;
import net.minecraftforge.fml.client.event.ConfigChangedEvent.OnConfigChangedEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class EventConfigReload {
   public EventConfigReload() {
   }

   @SubscribeEvent
   public void onConfigReload(OnConfigChangedEvent event) {
      if ("bettermineshafts".equals(event.getModID())) {
         ConfigManager.sync("bettermineshafts", Type.INSTANCE);
         Integrations.update();
      }

   }
}
