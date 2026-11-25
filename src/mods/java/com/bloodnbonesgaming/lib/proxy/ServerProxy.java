package com.bloodnbonesgaming.lib.proxy;

import net.minecraftforge.fml.common.event.FMLServerStartingEvent;

public class ServerProxy extends CommonProxy {
   public ServerProxy() {
   }

   public void registerCommands(FMLServerStartingEvent event) {
   }

   public boolean client() {
      return false;
   }

   public void registerEventHandlers() {
   }
}
