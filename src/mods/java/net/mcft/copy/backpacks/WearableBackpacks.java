package net.mcft.copy.backpacks;

import net.mcft.copy.backpacks.network.BackpacksChannel;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.Mod.Instance;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import org.apache.logging.log4j.Logger;

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
   @Instance
   public static WearableBackpacks INSTANCE;
   @SidedProxy(
      serverSide = "net.mcft.copy.backpacks.ProxyCommon",
      clientSide = "net.mcft.copy.backpacks.ProxyClient"
   )
   public static ProxyCommon PROXY;
   public static Logger LOG;
   public static BackpacksChannel CHANNEL;
   public static BackpacksContent CONTENT;

   public WearableBackpacks() {
   }

   @EventHandler
   public void preInit(FMLPreInitializationEvent event) {
      LOG = event.getModLog();
      CHANNEL = new BackpacksChannel();
      CONTENT = new BackpacksContent();
      CONTENT.registerRecipes();
      PROXY.preInit();
   }

   @EventHandler
   public void init(FMLInitializationEvent event) {
      PROXY.initBackpackLayers();
      PROXY.init();
   }
}
