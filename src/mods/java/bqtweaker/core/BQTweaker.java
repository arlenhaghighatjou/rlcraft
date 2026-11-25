package bqtweaker.core;

import bqtweaker.core.proxies.CommonProxy;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.Mod.Instance;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import org.apache.logging.log4j.Logger;

@Mod(
   modid = "bqtweaker",
   version = "1.3.5",
   name = "BQTweaker",
   dependencies = "after:betterquesting"
)
public class BQTweaker {
   public static final String MODID = "bqtweaker";
   public static final String VERSION = "1.3.5";
   public static final String NAME = "BQTweaker";
   public static final String PROXY = "bqtweaker.core.proxies";
   public static final String CHANNEL = "BQTWEAKER";
   @Instance("bqtweaker")
   public static BQTweaker instance;
   @SidedProxy(
      clientSide = "bqtweaker.core.proxies.ClientProxy",
      serverSide = "bqtweaker.core.proxies.CommonProxy"
   )
   public static CommonProxy proxy;
   public static Logger logger;

   public BQTweaker() {
   }

   @EventHandler
   public void preInit(FMLPreInitializationEvent event) {
      logger = event.getModLog();
   }

   @EventHandler
   public void init(FMLInitializationEvent event) {
      if (Loader.isModLoaded("betterquesting")) {
         proxy.registerHandlers();
      }

   }
}
