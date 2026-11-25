package baubles.common;

import baubles.api.BaubleType;
import baubles.api.IBauble;
import baubles.api.cap.BaubleItem;
import baubles.api.cap.BaublesCapabilities;
import baubles.api.cap.BaublesContainer;
import baubles.api.cap.IBaublesItemHandler;
import baubles.common.event.CommandBaubles;
import baubles.common.network.PacketHandler;
import java.io.File;
import java.util.concurrent.Callable;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.Mod.Instance;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(
   modid = "baubles",
   name = "Baubles",
   version = "1.5.2",
   guiFactory = "baubles.client.gui.BaublesGuiFactory",
   dependencies = "required-after:forge@[14.21.0.2348,);",
   acceptedMinecraftVersions = "[1.12]"
)
public class Baubles {
   public static final String MODID = "baubles";
   public static final String MODNAME = "Baubles";
   public static final String VERSION = "1.5.2";
   @SidedProxy(
      clientSide = "baubles.client.ClientProxy",
      serverSide = "baubles.common.CommonProxy"
   )
   public static CommonProxy proxy;
   @Instance("baubles")
   public static Baubles instance;
   public File modDir;
   public static final Logger log = LogManager.getLogger("baubles".toUpperCase());
   public static final int GUI = 0;

   public Baubles() {
   }

   @EventHandler
   public void preInit(FMLPreInitializationEvent event) {
      this.modDir = event.getModConfigurationDirectory();

      try {
         Config.initialize(event.getSuggestedConfigurationFile());
      } catch (Exception var6) {
         log.error("BAUBLES has a problem loading it's configuration");
      } finally {
         if (Config.config != null) {
            Config.save();
         }

      }

      CapabilityManager.INSTANCE.register(IBaublesItemHandler.class, new BaublesCapabilities.CapabilityBaubles(), BaublesContainer.class);
      CapabilityManager.INSTANCE.register(IBauble.class, new BaublesCapabilities.CapabilityItemBaubleStorage(), () -> {
         return new BaubleItem(BaubleType.TRINKET);
      });
      proxy.registerEventHandlers();
      PacketHandler.init();
      Config.save();
   }

   @EventHandler
   public void init(FMLInitializationEvent evt) {
      NetworkRegistry.INSTANCE.registerGuiHandler(instance, proxy);
      proxy.init();
   }

   @EventHandler
   public void serverLoad(FMLServerStartingEvent event) {
      event.registerServerCommand(new CommandBaubles());
   }
}
