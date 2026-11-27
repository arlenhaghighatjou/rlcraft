package com.bloodnbonesgaming.loadingscreens;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.event.FMLConstructionEvent;
import net.minecraftforge.fml.common.event.FMLEvent;
import com.bloodnbonesgaming.lib.BNBGamingMod;
import com.bloodnbonesgaming.loadingscreens.client.LoadingScreenHandler;
import com.bloodnbonesgaming.loadingscreens.proxy.CommonProxy;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLLoadCompleteEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerAboutToStartEvent;
import net.minecraftforge.fml.common.event.FMLServerStartedEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import net.minecraftforge.fml.common.event.FMLServerStoppedEvent;

@Mod(
   modid = "loadingscreens",
   name = "Loading Screens",
   version = "0.3.1",
   clientSideOnly = true,
   dependencies = "required-after:bnbgaminglib@[2.14.0,);after:gamestages;",
   acceptedMinecraftVersions = "[1.12,1.13)"
)
public class LoadingScreens extends BNBGamingMod {
   public static LoadingScreens instance;
   public static CommonProxy proxy;

   public LoadingScreens() {
      super("loadingscreens");
   }

   public void handleModStateEvent(FMLEvent event) {
      if (event instanceof FMLConstructionEvent) {
         instance = this;
         proxy = FMLCommonHandler.instance().getSide() == Side.CLIENT ? new com.bloodnbonesgaming.loadingscreens.proxy.ClientProxy() : new com.bloodnbonesgaming.loadingscreens.proxy.ServerProxy();
      } else if (event instanceof FMLPreInitializationEvent) {
         this.preInit((FMLPreInitializationEvent)event);
      } else if (event instanceof FMLInitializationEvent) {
         this.init((FMLInitializationEvent)event);
      } else if (event instanceof FMLPostInitializationEvent) {
         this.postInit((FMLPostInitializationEvent)event);
      } else if (event instanceof FMLLoadCompleteEvent) {
         this.loadComplete((FMLLoadCompleteEvent)event);
      } else if (event instanceof FMLServerAboutToStartEvent) {
         this.serverAboutToSTart((FMLServerAboutToStartEvent)event);
      } else if (event instanceof FMLServerStartingEvent) {
         this.serverStarting((FMLServerStartingEvent)event);
      } else if (event instanceof FMLServerStartedEvent) {
         this.serverStarted((FMLServerStartedEvent)event);
      } else if (event instanceof FMLServerStoppedEvent) {
         this.serverStopped((FMLServerStoppedEvent)event);
      }

   }

   public void preInit(FMLPreInitializationEvent event) {
      proxy.registerEventHandlers();
      LoadingScreenHandler.load();
   }

   public void init(FMLInitializationEvent event) {
   }

   public void postInit(FMLPostInitializationEvent event) {
   }

   public void loadComplete(FMLLoadCompleteEvent event) {
   }

   public void serverAboutToSTart(FMLServerAboutToStartEvent event) {
   }

   public void serverStarting(FMLServerStartingEvent event) {
   }

   public void serverStarted(FMLServerStartedEvent event) {
   }

   public void serverStopped(FMLServerStoppedEvent event) {
   }
}
