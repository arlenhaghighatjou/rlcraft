package com.bloodnbonesgaming.loadingscreens;

import com.bloodnbonesgaming.lib.BNBGamingMod;
import com.bloodnbonesgaming.loadingscreens.client.LoadingScreenHandler;
import com.bloodnbonesgaming.loadingscreens.proxy.CommonProxy;
import java.io.IOException;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.Mod.Instance;
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
   @Instance("loadingscreens")
   public static LoadingScreens instance;
   @SidedProxy(
      clientSide = "com.bloodnbonesgaming.loadingscreens.proxy.ClientProxy",
      serverSide = "com.bloodnbonesgaming.loadingscreens.proxy.ServerProxy"
   )
   public static CommonProxy proxy;
   public static boolean gamestages = false;

   public LoadingScreens() {
   }

   @EventHandler
   public void preInit(FMLPreInitializationEvent event) {
      if (Loader.isModLoaded("gamestages")) {
         gamestages = true;
      }

      proxy.registerEventHandlers();
      LoadingScreenHandler.load();
   }

   @EventHandler
   public void init(FMLInitializationEvent event) {
   }

   @EventHandler
   public void postInit(FMLPostInitializationEvent event) {
   }

   @EventHandler
   public void loadComplete(FMLLoadCompleteEvent event) {
   }

   @EventHandler
   public void serverAboutToSTart(FMLServerAboutToStartEvent event) {
   }

   @EventHandler
   public void serverStarting(FMLServerStartingEvent event) throws IOException {
   }

   @EventHandler
   public void serverStarted(FMLServerStartedEvent event) throws IOException {
   }

   @EventHandler
   public void serverStopped(FMLServerStoppedEvent event) {
   }
}
