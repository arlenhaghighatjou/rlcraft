package se.gory_moon.globalgamerules;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.event.FMLConstructionEvent;
import net.minecraftforge.fml.common.event.FMLEvent;
import net.minecraftforge.fml.common.Mod;
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
   private static GlobalGR instance;
   private static CommonProxy proxy;
   private GGRConfig GGRConfig;

   public GlobalGR() {
   }

   public void handleModStateEvent(FMLEvent event) {
      if (event instanceof FMLConstructionEvent) {
         instance = this;
         proxy = FMLCommonHandler.instance().getSide() == Side.CLIENT ? new se.gory_moon.globalgamerules.proxy.ClientProxy() : new se.gory_moon.globalgamerules.proxy.ServerProxy();
         se.gory_moon.globalgamerules.WorldEvents.register(MinecraftForge.EVENT_BUS);
      } else if (event instanceof FMLPreInitializationEvent) {
         this.preInit((FMLPreInitializationEvent)event);
      } else if (event instanceof FMLInitializationEvent) {
         this.init((FMLInitializationEvent)event);
      }

   }

   public void preInit(FMLPreInitializationEvent event) {
      Reference.logger = event.getModLog();
      this.GGRConfig = (new GGRConfig(event.getSuggestedConfigurationFile())).loadConfig();
      proxy.setConfigEntryClasses();
   }

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
