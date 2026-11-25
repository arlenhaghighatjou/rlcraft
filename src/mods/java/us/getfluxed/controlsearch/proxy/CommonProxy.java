package us.getfluxed.controlsearch.proxy;

import net.minecraftforge.common.MinecraftForge;
import us.getfluxed.controlsearch.events.CommonEventHandler;

public class CommonProxy {
   public CommonProxy() {
   }

   public void registerEvents() {
      MinecraftForge.EVENT_BUS.register(new CommonEventHandler());
   }
}
