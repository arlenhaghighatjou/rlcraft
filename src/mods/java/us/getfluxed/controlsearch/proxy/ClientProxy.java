package us.getfluxed.controlsearch.proxy;

import net.minecraftforge.common.MinecraftForge;
import us.getfluxed.controlsearch.events.ClientEventHandler;

public class ClientProxy extends CommonProxy {
   public ClientProxy() {
   }

   public void registerEvents() {
      super.registerEvents();
      MinecraftForge.EVENT_BUS.register(new ClientEventHandler());
   }
}
