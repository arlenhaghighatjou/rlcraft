package moddy10.craftablechainmail.proxy;

import moddy10.craftablechainmail.init.ModItems;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;

@EventBusSubscriber({Side.CLIENT})
public class ClientProxy extends CommonProxy {
   public ClientProxy() {
   }

   public void preInit(FMLPreInitializationEvent e) {
      super.preInit(e);
   }

   @SubscribeEvent
   public static void registerModels(ModelRegistryEvent event) {
      ModItems.initModels();
   }
}
