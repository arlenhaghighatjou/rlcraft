package tschipp.callablehorses;

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
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.lax1dude.eaglercraft.v1_8.log4j.LogManager;
import net.lax1dude.eaglercraft.v1_8.log4j.Logger;

@Mod(
   modid = "callablehorses",
   name = "Callable Horses",
   version = "1.1.1",
   guiFactory = "tschipp.callablehorses.client.gui.GuiFactoryCallableHorses",
   dependencies = "required-after:forge@[13.20.1.2386,);",
   acceptedMinecraftVersions = "[1.12.2,1.13)"
)
public class CallableHorses {
   public static CommonProxy proxy;
   public static CallableHorses instance;
   public static final String MODID = "callablehorses";
   public static final String VERSION = "1.1.1";
   public static final String NAME = "Callable Horses";
   public static final String ACCEPTED_VERSIONS = "[1.12.2,1.13)";
   public static final Logger LOGGER = LogManager.getLogger("CallableHorses");
   public static final String DEPENDENCIES = "required-after:forge@[13.20.1.2386,);";
   public static VFile2 CONFIGURATION_FILE;
   public static SimpleNetworkWrapper network;

   public CallableHorses() {
   }

   public void handleModStateEvent(FMLEvent event) {
      if (event instanceof FMLConstructionEvent) {
         instance = this;
         proxy = FMLCommonHandler.instance().getSide() == Side.CLIENT ? new tschipp.callablehorses.ClientProxy() : new tschipp.callablehorses.CommonProxy();
         tschipp.callablehorses.common.config.CallableHorsesConfig.register();
         tschipp.callablehorses.common.capabilities.horseowner.HorseOwnerProvider.registerCapabilities();
         tschipp.callablehorses.common.capabilities.storedhorse.HorseProvider.registerCapabilities();
         if (FMLCommonHandler.instance().getSide() == Side.CLIENT) {
            tschipp.callablehorses.client.event.KeybindEvents.register(MinecraftForge.EVENT_BUS);
         }
         tschipp.callablehorses.common.config.CallableHorsesConfig.EventHandler.register(MinecraftForge.EVENT_BUS);
         tschipp.callablehorses.common.events.EntityEvents.register(MinecraftForge.EVENT_BUS);
      } else if (event instanceof FMLPreInitializationEvent) {
         this.preInit((FMLPreInitializationEvent)event);
      } else if (event instanceof FMLInitializationEvent) {
         this.init((FMLInitializationEvent)event);
      } else if (event instanceof FMLPostInitializationEvent) {
         this.postInit((FMLPostInitializationEvent)event);
      }

   }

   public void preInit(FMLPreInitializationEvent event) {
      proxy.preInit(event);
   }

   public void init(FMLInitializationEvent event) {
      proxy.init(event);
   }

   public void postInit(FMLPostInitializationEvent event) {
      proxy.postInit(event);
   }
}
