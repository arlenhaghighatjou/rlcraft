package ljfa.tntutils;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.event.FMLConstructionEvent;
import net.minecraftforge.fml.common.event.FMLEvent;
import ljfa.tntutils.command.CommandExplosion;
import ljfa.tntutils.proxy.CommonProxy;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import net.lax1dude.eaglercraft.v1_8.log4j.LogManager;
import net.lax1dude.eaglercraft.v1_8.log4j.Logger;

@Mod(
   modid = "tnt_utilities",
   name = "TNTUtils",
   version = "1.2.3",
   acceptableRemoteVersions = "*",
   guiFactory = "ljfa.tntutils.gui.TntuGuiFactory",
   acceptedMinecraftVersions = "[1.12,1.13)"
)
public class TNTUtils {
   public static TNTUtils instance;
   public static CommonProxy proxy;
   public static final Logger logger = LogManager.getLogger("TNTUtils");

   public TNTUtils() {
   }

   public void handleModStateEvent(FMLEvent event) {
      if (event instanceof FMLConstructionEvent) {
         instance = this;
         proxy = FMLCommonHandler.instance().getSide() == Side.CLIENT ? new ljfa.tntutils.proxy.ClientProxy() : new ljfa.tntutils.proxy.CommonProxy();
      } else if (event instanceof FMLPreInitializationEvent) {
         this.preInit((FMLPreInitializationEvent)event);
      } else if (event instanceof FMLInitializationEvent) {
         this.init((FMLInitializationEvent)event);
      } else if (event instanceof FMLPostInitializationEvent) {
         this.postInit((FMLPostInitializationEvent)event);
      } else if (event instanceof FMLServerStartingEvent) {
         this.serverStarting((FMLServerStartingEvent)event);
      }

   }

   public void preInit(FMLPreInitializationEvent event) {
      Config.loadConfig(event.getSuggestedConfigurationFile());
      proxy.preInit(event);
   }

   public void init(FMLInitializationEvent event) {
      proxy.init(event);
   }

   public void postInit(FMLPostInitializationEvent event) {
      Config.createBlacklistSet();
      Config.modifyResistances();
      Config.save();
      proxy.postInit(event);
   }

   public void serverStarting(FMLServerStartingEvent event) {
      if (Config.explosionCommand) {
         event.registerServerCommand(new CommandExplosion());
      }

   }
}
