package shieldbreak.core;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.event.FMLConstructionEvent;
import net.minecraftforge.fml.common.event.FMLEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.lax1dude.eaglercraft.v1_8.log4j.Logger;
import shieldbreak.core.proxies.CommonProxy;

@Mod(
   modid = "shieldbreak",
   version = "1.1.3",
   name = "ShieldBreak",
   acceptableRemoteVersions = "*"
)
public class ShieldBreak {
   public static final String MODID = "shieldbreak";
   public static final String VERSION = "1.1.3";
   public static final String NAME = "ShieldBreak";
   public static final String PROXY = "shieldbreak.core.proxies";
   public static final String CHANNEL = "SHIELDBREAK";
   public static ShieldBreak instance;
   public static CommonProxy proxy;
   public static Logger logger;

   public ShieldBreak() {
   }

   public void handleModStateEvent(FMLEvent event) {
      if (event instanceof FMLConstructionEvent) {
         instance = this;
         proxy = FMLCommonHandler.instance().getSide() == Side.CLIENT ? new shieldbreak.core.proxies.ClientProxy() : new shieldbreak.core.proxies.CommonProxy();
         shieldbreak.handlers.ModConfig.register();
         shieldbreak.handlers.ModConfig.EventHandler.register(MinecraftForge.EVENT_BUS);
      } else if (event instanceof FMLPreInitializationEvent) {
         this.preInit((FMLPreInitializationEvent)event);
      } else if (event instanceof FMLInitializationEvent) {
         this.onInit((FMLInitializationEvent)event);
      }

   }

   public void preInit(FMLPreInitializationEvent event) {
      logger = event.getModLog();
   }

   public void onInit(FMLInitializationEvent event) {
      proxy.registerHandlers();
   }
}
