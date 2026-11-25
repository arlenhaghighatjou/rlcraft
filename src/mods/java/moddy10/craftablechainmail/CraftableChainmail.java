package moddy10.craftablechainmail;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.event.FMLConstructionEvent;
import net.minecraftforge.fml.common.event.FMLEvent;
import moddy10.craftablechainmail.proxy.CommonProxy;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.lax1dude.eaglercraft.v1_8.log4j.Logger;

@Mod(
   modid = "craftablechainmail",
   acceptedMinecraftVersions = "[1.12.2]"
)
public class CraftableChainmail {
   public static CraftableChainmail instance;
   public static CommonProxy proxy;
   public static Logger log;

   public CraftableChainmail() {
   }

   public void handleModStateEvent(FMLEvent event) {
      if (event instanceof FMLConstructionEvent) {
         instance = this;
         proxy = FMLCommonHandler.instance().getSide() == Side.CLIENT ? new moddy10.craftablechainmail.proxy.ClientProxy() : new moddy10.craftablechainmail.proxy.CommonProxy();
         moddy10.craftablechainmail.init.ModItems.registerHolders();
         if (FMLCommonHandler.instance().getSide() == Side.CLIENT) {
            moddy10.craftablechainmail.proxy.ClientProxy.register(MinecraftForge.EVENT_BUS);
         }
         moddy10.craftablechainmail.proxy.CommonProxy.register(MinecraftForge.EVENT_BUS);
      } else if (event instanceof FMLPreInitializationEvent) {
         CraftableChainmail.preInit((FMLPreInitializationEvent)event);
      } else if (event instanceof FMLInitializationEvent) {
         CraftableChainmail.init((FMLInitializationEvent)event);
      } else if (event instanceof FMLPostInitializationEvent) {
         CraftableChainmail.postInit((FMLPostInitializationEvent)event);
      }

   }

   public static void preInit(FMLPreInitializationEvent event) {
      log = event.getModLog();
      proxy.preInit(event);
      log.info("Pre-init OK!");
   }

   public static void init(FMLInitializationEvent event) {
      proxy.init(event);
      log.info("Init OK!");
   }

   public static void postInit(FMLPostInitializationEvent event) {
      proxy.postInit(event);
      log.info("Post-init OK!");
   }
}
