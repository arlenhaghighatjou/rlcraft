package c4.comforts;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.event.FMLConstructionEvent;
import net.minecraftforge.fml.common.event.FMLEvent;
import c4.comforts.proxy.CommonProxy;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.lax1dude.eaglercraft.v1_8.log4j.Logger;

@Mod(
   modid = "comforts",
   name = "Comforts",
   version = "1.4.1.3",
   dependencies = "required-after:forge@[14.23.5.2776,);after:morpheus;after:toughasnails",
   acceptedMinecraftVersions = "[1.12, 1.13)"
)
public class Comforts {
   public static final String MODID = "comforts";
   public static final String MODNAME = "Comforts";
   public static final String MODVER = "1.4.1.3";
   public static CommonProxy proxy;
   public static Comforts instance;
   public static Logger logger;

   public Comforts() {
   }

   public void handleModStateEvent(FMLEvent event) {
      if (event instanceof FMLConstructionEvent) {
         instance = this;
         proxy = FMLCommonHandler.instance().getSide() == Side.CLIENT ? new c4.comforts.proxy.ClientProxy() : new c4.comforts.proxy.CommonProxy();
         c4.comforts.common.ConfigHandler.register();
         c4.comforts.common.blocks.ComfortsBlocks.registerHolders();
         c4.comforts.common.items.ComfortsItems.registerHolders();
         c4.comforts.common.capability.CapabilitySleeping.registerCapabilities();
         c4.comforts.common.capability.CapabilitySleepTime.registerCapabilities();
         c4.comforts.common.capability.CapabilityWellRested.registerCapabilities();
         c4.comforts.common.ConfigHandler.ConfigEventHandler.register(MinecraftForge.EVENT_BUS);
         if (FMLCommonHandler.instance().getSide() == Side.CLIENT) {
            c4.comforts.proxy.ClientProxy.register(MinecraftForge.EVENT_BUS);
         }
         c4.comforts.proxy.CommonProxy.register(MinecraftForge.EVENT_BUS);
      } else if (event instanceof FMLPreInitializationEvent) {
         this.preInit((FMLPreInitializationEvent)event);
      } else if (event instanceof FMLInitializationEvent) {
         this.init((FMLInitializationEvent)event);
      } else if (event instanceof FMLPostInitializationEvent) {
         this.postInit((FMLPostInitializationEvent)event);
      }

   }

   public void preInit(FMLPreInitializationEvent evt) {
      logger = evt.getModLog();
      proxy.preInit(evt);
   }

   public void init(FMLInitializationEvent evt) {
      proxy.init(evt);
   }

   public void postInit(FMLPostInitializationEvent evt) {
      proxy.postInit(evt);
   }
}
