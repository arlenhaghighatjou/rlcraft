package tschipp.carryon;

import com.google.common.collect.ImmutableList;
import java.io.File;
import java.util.function.Consumer;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.Mod.Instance;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import net.minecraftforge.fml.common.event.FMLInterModComms.IMCEvent;
import net.minecraftforge.fml.common.event.FMLInterModComms.IMCMessage;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.lax1dude.eaglercraft.v1_8.log4j.LogManager;
import net.lax1dude.eaglercraft.v1_8.log4j.Logger;
import tschipp.carryon.common.CommonProxy;
import tschipp.carryon.common.command.CommandCarryOn;
import tschipp.carryon.common.command.CommandCarryOnReload;
import tschipp.carryon.common.handler.ListHandler;
import tschipp.carryon.common.handler.ModelOverridesHandler;

@EventBusSubscriber
@Mod(
   modid = "carryon",
   name = "Carry On",
   version = "1.12.3",
   guiFactory = "tschipp.carryon.client.gui.GuiFactoryCarryOn",
   dependencies = "required-after:forge@[13.20.1.2386,);after:gamestages;",
   updateJSON = "https://gist.githubusercontent.com/Tschipp/dccadee7c90d7a34e6e76a35d9d6fa2e/raw/",
   acceptedMinecraftVersions = "[1.12.2,1.13)",
   certificateFingerprint = "fd21553434f4905f2f73ea7838147ac4ea07bd88"
)
public class CarryOn {
   @SidedProxy(
      clientSide = "tschipp.carryon.client.ClientProxy",
      serverSide = "tschipp.carryon.common.CommonProxy"
   )
   public static CommonProxy proxy;
   @Instance("carryon")
   public static CarryOn instance;
   public static final String MODID = "carryon";
   public static final String VERSION = "1.12.3";
   public static final String NAME = "Carry On";
   public static final String ACCEPTED_VERSIONS = "[1.12.2,1.13)";
   public static final String UPDATE_JSON = "https://gist.githubusercontent.com/Tschipp/dccadee7c90d7a34e6e76a35d9d6fa2e/raw/";
   public static final Logger LOGGER = LogManager.getFormatterLogger("CarryOn");
   public static final String DEPENDENCIES = "required-after:forge@[13.20.1.2386,);after:gamestages;";
   public static final String CERTIFICATE_FINGERPRINT = "fd21553434f4905f2f73ea7838147ac4ea07bd88";
   public static File CONFIGURATION_FILE;
   public static boolean FINGERPRINT_VIOLATED = false;
   public static SimpleNetworkWrapper network;

   public CarryOn() {
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

   @EventHandler
   public void serverLoad(FMLServerStartingEvent event) {
      event.registerServerCommand(new CommandCarryOn());
      event.registerServerCommand(new CommandCarryOnReload());
   }

   @EventHandler
   public void imcEvent(IMCEvent event) {
      ImmutableList<IMCMessage> messages = event.getMessages();
      messages.forEach((msg) -> {
         String method = msg.key;
         String str = msg.getStringValue();
         byte var4 = -1;
         switch(method.hashCode()) {
         case -1827045622:
            if (method.equals("whitelistEntity")) {
               var4 = 3;
            }
            break;
         case -1724340090:
            if (method.equals("whitelistBlock")) {
               var4 = 2;
            }
            break;
         case -1367635817:
            if (method.equals("blacklistStacking")) {
               var4 = 4;
            }
            break;
         case -39397951:
            if (method.equals("whitelistStacking")) {
               var4 = 5;
            }
            break;
         case 447530740:
            if (method.equals("addModelOverride")) {
               var4 = 6;
            }
            break;
         case 1107881824:
            if (method.equals("blacklistEntity")) {
               var4 = 1;
            }
            break;
         case 1695470960:
            if (method.equals("blacklistBlock")) {
               var4 = 0;
            }
         }

         switch(var4) {
         case 0:
            ListHandler.FORBIDDEN_TILES.add(str);
            break;
         case 1:
            ListHandler.FORBIDDEN_ENTITIES.add(str);
            break;
         case 2:
            ListHandler.ALLOWED_TILES.add(str);
            break;
         case 3:
            ListHandler.ALLOWED_ENTITIES.add(str);
            break;
         case 4:
            ListHandler.FORBIDDEN_STACKING.add(str);
            break;
         case 5:
            ListHandler.ALLOWED_STACKING.add(str);
            break;
         case 6:
            ModelOverridesHandler.parseOverride(str, 0);
         }

      });
   }
}
