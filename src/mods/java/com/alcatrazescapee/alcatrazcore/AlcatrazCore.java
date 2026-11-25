package com.alcatrazescapee.alcatrazcore;

import com.alcatrazescapee.alcatrazcore.client.ClientEventHandler;
import com.alcatrazescapee.alcatrazcore.proxy.ClientProxy;
import com.alcatrazescapee.alcatrazcore.proxy.IProxy;
import com.alcatrazescapee.alcatrazcore.proxy.ServerProxy;
import com.alcatrazescapee.alcatrazcore.util.OreDictionaryHelper;
import com.alcatrazescapee.alcatrazcore.util.compat.FireRegistry;
import com.alcatrazescapee.alcatrazcore.util.compat.WrenchRegistry;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.fml.client.event.ConfigChangedEvent.OnConfigChangedEvent;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLConstructionEvent;
import net.minecraftforge.fml.common.event.FMLEvent;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.lax1dude.eaglercraft.v1_8.log4j.Logger;

@Mod(
   modid = "alcatrazcore",
   version = "1.0.4",
   dependencies = "required-after:forge@[14.23.4.2705,15.0.0.0);",
   name = "Alcatraz Core",
   useMetadata = true,
   acceptedMinecraftVersions = "[1.12.2]"
)
public final class AlcatrazCore {
   public static final String MOD_ID = "alcatrazcore";
   public static final String MOD_NAME = "Alcatraz Core";
   public static final String VERSION = "1.0.4";
   public static final String DEPENDENCIES = "required-after:forge@[14.23.4.2705,15.0.0.0);";
   private static AlcatrazCore instance;
   private static IProxy proxy;
   private Logger log;

   public AlcatrazCore() {
   }

   public static Logger getLog() {
      return instance.log;
   }

   public static AlcatrazCore getInstance() {
      return instance;
   }

   public static IProxy getProxy() {
      return proxy;
   }

   public void handleModStateEvent(FMLEvent event) {
      if (event instanceof FMLConstructionEvent) {
         instance = this;
         CoreConfig.register();
         MinecraftForge.EVENT_BUS.register(OnConfigChangedEvent.class, ModEventHandler::onConfigChangedEvent);
         if (FMLCommonHandler.instance().getSide() == Side.CLIENT) {
            proxy = new ClientProxy();
            MinecraftForge.EVENT_BUS.register(ItemTooltipEvent.class, ClientEventHandler::onItemTooltip);
         } else {
            proxy = new ServerProxy();
         }
      } else if (event instanceof FMLPreInitializationEvent) {
         preInit((FMLPreInitializationEvent)event);
      } else if (event instanceof FMLInitializationEvent) {
         init((FMLInitializationEvent)event);
      } else if (event instanceof FMLPostInitializationEvent) {
         postInit((FMLPostInitializationEvent)event);
      }
   }

   public void preInit(FMLPreInitializationEvent event) {
      this.log = event.getModLog();
   }

   public void init(FMLInitializationEvent event) {
      OreDictionaryHelper.init();
   }

   public void postInit(FMLPostInitializationEvent event) {
      FireRegistry.init();
      WrenchRegistry.init();
   }
}
