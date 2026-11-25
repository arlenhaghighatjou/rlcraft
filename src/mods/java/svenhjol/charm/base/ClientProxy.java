package svenhjol.charm.base;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import svenhjol.meson.handler.ModelHandler;

public class ClientProxy extends CommonProxy {
   public ClientProxy() {
   }

   public void preInit(FMLPreInitializationEvent event) {
      super.preInit(event);
      CharmLoader.INSTANCE.preInitClient(event);
      MinecraftForge.EVENT_BUS.register(ModelHandler.class);
   }

   public void init(FMLInitializationEvent event) {
      super.init(event);
      CharmLoader.INSTANCE.initClient(event);
   }

   public void postInit(FMLPostInitializationEvent event) {
      super.postInit(event);
      CharmLoader.INSTANCE.postInitClient(event);
   }
}
