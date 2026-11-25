package bettertabs.core;

import bettertabs.core.proxies.CommonProxy;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.ModContainer;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.Mod.Instance;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.lax1dude.eaglercraft.v1_8.log4j.Logger;

@Mod(
   modid = "bettertabs",
   version = "1.0.4",
   name = "BetterTabs",
   dependencies = "after:betterquesting;after:levelup2;after:lycanitesmobs;after:levelup;after:classyhats;after:librarianlib"
)
public class BetterTabs {
   public static final String MODID = "bettertabs";
   public static final String VERSION = "1.0.4";
   public static final String NAME = "BetterTabs";
   public static final String PROXY = "bettertabs.core.proxies";
   public static final String CHANNEL = "BETTERTABS";
   @Instance("bettertabs")
   public static BetterTabs instance;
   @SidedProxy(
      clientSide = "bettertabs.core.proxies.ClientProxy",
      serverSide = "bettertabs.core.proxies.CommonProxy"
   )
   public static CommonProxy proxy;
   public SimpleNetworkWrapper network;
   public static Logger logger;

   public BetterTabs() {
   }

   @EventHandler
   public void preInit(FMLPreInitializationEvent event) {
      logger = event.getModLog();
      this.network = NetworkRegistry.INSTANCE.newSimpleChannel("BETTERTABS");
      proxy.registerConfig(event);
   }

   @EventHandler
   public void init(FMLInitializationEvent event) {
      ModContainer modContainer = (ModContainer)Loader.instance().getIndexedModList().get("bettertabs");
      if (modContainer != null && modContainer.getMod() instanceof BetterTabs) {
         BetterTabs var3 = (BetterTabs)modContainer.getMod();
      }

   }

   @EventHandler
   public void postInit(FMLPostInitializationEvent event) {
      proxy.registerHandlers();
   }

   @EventHandler
   public void serverLoad(FMLServerStartingEvent event) {
   }
}
