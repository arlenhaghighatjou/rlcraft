package net.theawesomegem.fishingmadebetter;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.event.FMLConstructionEvent;
import net.minecraftforge.fml.common.event.FMLEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import net.theawesomegem.fishingmadebetter.proxy.CommonProxy;

@Mod(
   modid = "fishingmadebetter",
   version = "2.2.6",
   name = "Fishing Made Better",
   dependencies = "after:advanced-fishing;after:aquaculture;after:levelup2"
)
public class Primary {
   public static Primary Instance;
   public static CommonProxy proxy;

   public Primary() {
   }

   public void handleModStateEvent(FMLEvent event) {
      if (event instanceof FMLConstructionEvent) {
         Instance = this;
         proxy = FMLCommonHandler.instance().getSide() == Side.CLIENT ? new net.theawesomegem.fishingmadebetter.proxy.ClientProxy() : new net.theawesomegem.fishingmadebetter.proxy.CommonProxy();
         net.theawesomegem.fishingmadebetter.common.configuration.ConfigurationManager.register();
         net.theawesomegem.fishingmadebetter.common.capability.fishing.FishingCapabilityProvider.registerCapabilities();
         net.theawesomegem.fishingmadebetter.common.capability.world.ChunkCapabilityProvider.registerCapabilities();
         if (FMLCommonHandler.instance().getSide() == Side.CLIENT) {
            net.theawesomegem.fishingmadebetter.client.registry.ClientRegistryManager.RegistryHandler.register(MinecraftForge.EVENT_BUS);
         }
         net.theawesomegem.fishingmadebetter.common.configuration.ConfigurationManager.EventHandler.register(MinecraftForge.EVENT_BUS);
         net.theawesomegem.fishingmadebetter.common.registry.RegistryManager.RegistryHandler.register(MinecraftForge.EVENT_BUS);
      } else if (event instanceof FMLPreInitializationEvent) {
         this.onPreInit((FMLPreInitializationEvent)event);
      } else if (event instanceof FMLInitializationEvent) {
         this.onInit((FMLInitializationEvent)event);
      } else if (event instanceof FMLPostInitializationEvent) {
         this.onPostInit((FMLPostInitializationEvent)event);
      } else if (event instanceof FMLServerStartingEvent) {
         this.onServerStarting((FMLServerStartingEvent)event);
      }

   }

   public void onPreInit(FMLPreInitializationEvent e) {
      proxy.onPreInit(e);
   }

   public void onInit(FMLInitializationEvent e) {
      proxy.onInit(e);
   }

   public void onPostInit(FMLPostInitializationEvent e) {
      proxy.onPostInit(e);
   }

   public void onServerStarting(FMLServerStartingEvent e) {
      proxy.onServerStarting(e);
   }
}
