package bettertabs.core.proxies;

import bettertabs.handlers.ConfigHandler;
import bettertabs.handlers.TabHandler;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

public class ClientProxy extends CommonProxy {
   public ClientProxy() {
   }

   public boolean isClient() {
      return true;
   }

   public void registerHandlers() {
      MinecraftForge.EVENT_BUS.register(TabHandler.class);
   }

   public void registerConfig(FMLPreInitializationEvent event) {
      ConfigHandler.initConfig(event.getSuggestedConfigurationFile());
   }
}
