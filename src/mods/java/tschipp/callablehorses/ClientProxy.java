package tschipp.callablehorses;

import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import tschipp.callablehorses.client.keybinds.KeybindManager;

public class ClientProxy extends CommonProxy {
   public ClientProxy() {
   }

   public void preInit(FMLPreInitializationEvent event) {
      super.preInit(event);
   }

   public void init(FMLInitializationEvent event) {
      super.init(event);
      KeybindManager.init();
   }

   public void postInit(FMLPostInitializationEvent e) {
      super.postInit(e);
   }
}
