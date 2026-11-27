package net.mcft.copy.backpacks;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.event.FMLConstructionEvent;
import net.minecraftforge.fml.common.event.FMLEvent;
import net.mcft.copy.backpacks.network.BackpacksChannel;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.lax1dude.eaglercraft.v1_8.log4j.Logger;

@Mod(
   modid = "wearablebackpacks",
   name = "Wearable Backpacks RLCraft Edition",
   version = "3.2.6",
   dependencies = "required-after:forge@[14.23.5.2855,);required-after:baubles@[1.5.2,)"
)
public class WearableBackpacks {
   public static final String MOD_ID = "wearablebackpacks";
   public static final String MOD_NAME = "Wearable Backpacks RLCraft Edition";
   public static final String VERSION = "3.2.6";
   public static WearableBackpacks INSTANCE;
   public static ProxyCommon PROXY;
   public static Logger LOG;
   public static BackpacksChannel CHANNEL;
   public static BackpacksContent CONTENT;

   public WearableBackpacks() {
   }

   public void handleModStateEvent(FMLEvent event) {
      if (event instanceof FMLConstructionEvent) {
         INSTANCE = this;
         PROXY = FMLCommonHandler.instance().getSide() == Side.CLIENT ? new net.mcft.copy.backpacks.ProxyClient() : new net.mcft.copy.backpacks.ProxyCommon();
         net.mcft.copy.backpacks.config.ModConfig.register();
         net.mcft.copy.backpacks.config.ModConfig.EventHandler.register(MinecraftForge.EVENT_BUS);
      } else if (event instanceof FMLPreInitializationEvent) {
         this.preInit((FMLPreInitializationEvent)event);
      } else if (event instanceof FMLInitializationEvent) {
         this.init((FMLInitializationEvent)event);
      }

   }

   public void preInit(FMLPreInitializationEvent event) {
      LOG = event.getModLog();
      CHANNEL = new BackpacksChannel();
      CONTENT = new BackpacksContent();
      CONTENT.registerRecipes();
      PROXY.preInit();
   }

   public void init(FMLInitializationEvent event) {
      PROXY.initBackpackLayers();
      PROXY.init();
   }
}
