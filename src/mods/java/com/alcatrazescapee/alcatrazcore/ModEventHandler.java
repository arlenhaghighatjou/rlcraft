package com.alcatrazescapee.alcatrazcore;

import net.minecraftforge.common.config.ConfigManager;
import net.minecraftforge.common.config.Config.Type;
import net.minecraftforge.fml.client.event.ConfigChangedEvent.OnConfigChangedEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@EventBusSubscriber
public final class ModEventHandler {
   public ModEventHandler() {
   }

   @SubscribeEvent
   public static void onConfigChangedEvent(OnConfigChangedEvent event) {
      if (event.getModID().equals("alcatrazcore")) {
         ConfigManager.sync("alcatrazcore", Type.INSTANCE);
      }

   }
}
