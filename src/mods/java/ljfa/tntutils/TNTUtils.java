package ljfa.tntutils;

import ljfa.tntutils.command.CommandExplosion;
import ljfa.tntutils.proxy.CommonProxy;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.Mod.Instance;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import net.lax1dude.eaglercraft.v1_8.log4j.LogManager;
import net.lax1dude.eaglercraft.v1_8.log4j.Logger;
import org.apache.logging.log4j.message.StringFormatterMessageFactory;

@Mod(
   modid = "tnt_utilities",
   name = "TNTUtils",
   version = "1.2.3",
   acceptableRemoteVersions = "*",
   guiFactory = "ljfa.tntutils.gui.TntuGuiFactory",
   dependencies = "required-after:tnt_utilities_core",
   acceptedMinecraftVersions = "[1.12,1.13)",
   updateJSON = "https://raw.githubusercontent.com/ljfa-ag/TNTUtils/version/version.json"
)
public class TNTUtils {
   @Instance("tnt_utilities")
   public static TNTUtils instance;
   @SidedProxy(
      clientSide = "ljfa.tntutils.proxy.ClientProxy",
      serverSide = "ljfa.tntutils.proxy.CommonProxy"
   )
   public static CommonProxy proxy;
   public static final Logger logger = LogManager.getLogger("TNTUtils", StringFormatterMessageFactory.INSTANCE);

   public TNTUtils() {
   }

   @EventHandler
   public void preInit(FMLPreInitializationEvent event) {
      Config.loadConfig(event.getSuggestedConfigurationFile());
      proxy.preInit(event);
   }

   @EventHandler
   public void init(FMLInitializationEvent event) {
      proxy.init(event);
   }

   @EventHandler
   public void postInit(FMLPostInitializationEvent event) {
      Config.createBlacklistSet();
      Config.modifyResistances();
      Config.save();
      proxy.postInit(event);
   }

   @EventHandler
   public void serverStarting(FMLServerStartingEvent event) {
      if (Config.explosionCommand) {
         event.registerServerCommand(new CommandExplosion());
      }

   }
}
