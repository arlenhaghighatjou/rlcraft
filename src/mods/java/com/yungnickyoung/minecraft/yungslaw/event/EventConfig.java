package com.yungnickyoung.minecraft.yungslaw.event;

import com.yungnickyoung.minecraft.yungslaw.YungsLaw;
import com.yungnickyoung.minecraft.yungslaw.integration.Integrations;
import net.minecraftforge.common.config.ConfigManager;
import net.minecraftforge.common.config.Config.Type;
import net.minecraftforge.fml.client.event.ConfigChangedEvent.OnConfigChangedEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class EventConfig {
   public EventConfig() {
   }

   @SubscribeEvent
   public void onConfigReload(OnConfigChangedEvent event) {
      if (event.getModID().equals("yungslaw")) {
         YungsLaw.LOGGER.info("Baking config changes...");
         ConfigManager.sync("yungslaw", Type.INSTANCE);
         Integrations.update();
         YungsLaw.configMap.clear();
      }

   }
}
