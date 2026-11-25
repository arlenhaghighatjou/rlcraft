package moddy10.craftablechainmail.proxy;

import net.minecraftforge.fml.common.eventhandler.EventBus;
import moddy10.craftablechainmail.init.ModItems;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.relauncher.Side;

public class ClientProxy extends CommonProxy {
   public ClientProxy() {
   }

   public void preInit(FMLPreInitializationEvent e) {
      super.preInit(e);
   }

   public static void register(EventBus bus) {
      bus.register(ModelRegistryEvent.class, ClientProxy::registerModels);
   }

   public static void registerModels(ModelRegistryEvent event) {
      ModItems.initModels();
   }
}
