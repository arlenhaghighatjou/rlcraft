package tschipp.carryon.client;

import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import tschipp.carryon.client.keybinds.CarryOnKeybinds;
import tschipp.carryon.common.CommonProxy;
import tschipp.carryon.common.handler.RegistrationHandler;

public class ClientProxy extends CommonProxy {
   public ClientProxy() {
   }

   public void preInit(FMLPreInitializationEvent event) {
      super.preInit(event);
      RegistrationHandler.regItemRenders();
      RegistrationHandler.regClientEvents();
   }

   public void init(FMLInitializationEvent event) {
      CarryOnKeybinds.init();
      super.init(event);
   }

   public void postInit(FMLPostInitializationEvent e) {
      super.postInit(e);
   }
}
