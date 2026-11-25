package sereneseasons.core;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.event.FMLConstructionEvent;
import net.minecraftforge.fml.common.event.FMLEvent;
import net.lax1dude.eaglercraft.v1_8.internal.vfs2.VFile2;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import net.lax1dude.eaglercraft.v1_8.log4j.LogManager;
import net.lax1dude.eaglercraft.v1_8.log4j.Logger;
import sereneseasons.command.SSCommand;
import sereneseasons.init.ModBlocks;
import sereneseasons.init.ModConfig;
import sereneseasons.init.ModFertility;
import sereneseasons.init.ModHandlers;
import sereneseasons.init.ModItems;

@Mod(
   modid = "sereneseasons",
   version = "1.2.18",
   name = "Serene Seasons",
   dependencies = "required-after:forge@[14.23.5.2768,)",
   acceptedMinecraftVersions = "[1.12.2]"
)
public class SereneSeasons {
   public static final String MOD_NAME = "Serene Seasons";
   public static final String MOD_ID = "sereneseasons";
   public static final String MOD_VERSION = "1.2.18";
   public static SereneSeasons instance;
   public static CommonProxy proxy;
   public static Logger logger = LogManager.getLogger("sereneseasons");
   public static VFile2 configDirectory;

   public SereneSeasons() {
   }

   public void handleModStateEvent(FMLEvent event) {
      if (event instanceof FMLConstructionEvent) {
         instance = this;
         proxy = FMLCommonHandler.instance().getSide() == Side.CLIENT ? new sereneseasons.core.ClientProxy() : new sereneseasons.core.CommonProxy();
         sereneseasons.config.FertilityConfig.register();
         sereneseasons.config.FertilityConfig.register(MinecraftForge.EVENT_BUS);
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
      configDirectory = new VFile2(event.getModConfigurationDirectory(), "sereneseasons");
      ModConfig.preInit(configDirectory);
      ModBlocks.init();
      ModItems.init();
      ModHandlers.init();
      proxy.registerRenderers();
   }

   public void init(FMLInitializationEvent event) {
      ModConfig.init(configDirectory);
   }

   public void postInit(FMLPostInitializationEvent event) {
      ModFertility.init();
      ModHandlers.postInit();
   }

   public void serverStarting(FMLServerStartingEvent event) {
      event.registerServerCommand(new SSCommand());
   }
}
