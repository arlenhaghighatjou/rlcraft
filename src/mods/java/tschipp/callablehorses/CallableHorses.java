package tschipp.callablehorses;

import java.io.File;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.Mod.Instance;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@EventBusSubscriber
@Mod(
   modid = "callablehorses",
   name = "Callable Horses",
   version = "1.1.1",
   guiFactory = "tschipp.callablehorses.client.gui.GuiFactoryCallableHorses",
   dependencies = "required-after:forge@[13.20.1.2386,);",
   acceptedMinecraftVersions = "[1.12.2,1.13)"
)
public class CallableHorses {
   @SidedProxy(
      clientSide = "tschipp.callablehorses.ClientProxy",
      serverSide = "tschipp.callablehorses.CommonProxy"
   )
   public static CommonProxy proxy;
   @Instance("callablehorses")
   public static CallableHorses instance;
   public static final String MODID = "callablehorses";
   public static final String VERSION = "1.1.1";
   public static final String NAME = "Callable Horses";
   public static final String ACCEPTED_VERSIONS = "[1.12.2,1.13)";
   public static final Logger LOGGER = LogManager.getFormatterLogger("CallableHorses");
   public static final String DEPENDENCIES = "required-after:forge@[13.20.1.2386,);";
   public static File CONFIGURATION_FILE;
   public static SimpleNetworkWrapper network;

   public CallableHorses() {
   }

   @EventHandler
   public void preInit(FMLPreInitializationEvent event) {
      proxy.preInit(event);
   }

   @EventHandler
   public void init(FMLInitializationEvent event) {
      proxy.init(event);
   }

   @EventHandler
   public void postInit(FMLPostInitializationEvent event) {
      proxy.postInit(event);
   }
}
