package se.gory_moon.globalgamerules;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.Mod.Instance;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import se.gory_moon.globalgamerules.config.GGRConfig;
import se.gory_moon.globalgamerules.proxy.CommonProxy;
import se.gory_moon.globalgamerules.reference.Reference;

@Mod(
   modid = "globalgamerules",
   name = "Global GameRules",
   version = "2.2",
   guiFactory = "se.gory_moon.globalgamerules.config.GGRGuiFactory",
   acceptableRemoteVersions = "*",
   acceptedMinecraftVersions = "[1.12.2]"
)
public class GlobalGR {
   @Instance
   private static GlobalGR instance;
   @SidedProxy(
      clientSide = "se.gory_moon.globalgamerules.proxy.ClientProxy",
      serverSide = "se.gory_moon.globalgamerules.proxy.ServerProxy"
   )
   private static CommonProxy proxy;
   private GGRConfig GGRConfig;

   public GlobalGR() {
   }

   @EventHandler
   public void preInit(FMLPreInitializationEvent event) {
      Reference.logger = event.getModLog();
      this.GGRConfig = (new GGRConfig(event.getSuggestedConfigurationFile())).loadConfig();
      proxy.setConfigEntryClasses();
   }

   @EventHandler
   public void init(FMLInitializationEvent event) {
      proxy.registerEvents();
   }

   public static GlobalGR getInstance() {
      return instance;
   }

   public static GGRConfig getConfig() {
      return instance.GGRConfig;
   }
}
