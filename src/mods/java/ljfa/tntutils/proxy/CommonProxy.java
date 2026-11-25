package ljfa.tntutils.proxy;

import ljfa.tntutils.Config;
import ljfa.tntutils.handlers.EntityJoinHandler;
import ljfa.tntutils.handlers.ExplosionHandler;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

public class CommonProxy {
   public CommonProxy() {
   }

   public void preInit(FMLPreInitializationEvent event) {
      Config.loadConfig(event.getSuggestedConfigurationFile());
   }

   public void init(FMLInitializationEvent event) {
      (new ExplosionHandler()).register(MinecraftForge.EVENT_BUS);
      if (Config.disableTNT || Config.disableTNTMinecart) {
         (new EntityJoinHandler()).register(MinecraftForge.EVENT_BUS);
      }

   }

   public void postInit(FMLPostInitializationEvent event) {
   }
}
