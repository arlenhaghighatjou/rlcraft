package shieldbreak.core;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.Mod.Instance;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import org.apache.logging.log4j.Logger;
import shieldbreak.core.proxies.CommonProxy;

@Mod(
   modid = "shieldbreak",
   version = "1.1.3",
   name = "ShieldBreak",
   acceptableRemoteVersions = "*"
)
public class ShieldBreak {
   public static final String MODID = "shieldbreak";
   public static final String VERSION = "1.1.3";
   public static final String NAME = "ShieldBreak";
   public static final String PROXY = "shieldbreak.core.proxies";
   public static final String CHANNEL = "SHIELDBREAK";
   @Instance("shieldbreak")
   public static ShieldBreak instance;
   @SidedProxy(
      clientSide = "shieldbreak.core.proxies.ClientProxy",
      serverSide = "shieldbreak.core.proxies.CommonProxy"
   )
   public static CommonProxy proxy;
   public static Logger logger;

   public ShieldBreak() {
   }

   @EventHandler
   public void preInit(FMLPreInitializationEvent event) {
      logger = event.getModLog();
   }

   @EventHandler
   public void onInit(FMLInitializationEvent event) {
      proxy.registerHandlers();
   }
}
