package com.bloodnbonesgaming.loadingscreens.proxy;

import com.bloodnbonesgaming.loadingscreens.client.ClientEventHandler;
import net.minecraftforge.common.MinecraftForge;

public class ClientProxy extends CommonProxy {
   public ClientProxy() {
   }

   public void registerEventHandlers() {
      super.registerEventHandlers();
      (new ClientEventHandler()).register(MinecraftForge.EVENT_BUS);
   }
}
