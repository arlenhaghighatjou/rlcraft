package baubles.common;

import baubles.api.BaubleType;
import baubles.api.IBauble;
import baubles.api.cap.BaubleItem;
import baubles.api.cap.BaublesCapabilities;
import baubles.api.cap.BaublesContainer;
import baubles.api.cap.IBaublesItemHandler;
import baubles.client.ClientProxy;
import baubles.common.event.CommandBaubles;
import baubles.common.items.ItemRing;
import baubles.common.network.PacketHandler;
import net.lax1dude.eaglercraft.v1_8.internal.vfs2.VFile2;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLConstructionEvent;
import net.minecraftforge.fml.common.event.FMLEvent;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.lax1dude.eaglercraft.v1_8.log4j.LogManager;
import net.lax1dude.eaglercraft.v1_8.log4j.Logger;

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
   public static CommonProxy proxy;
   public static Baubles instance;
   public VFile2 modDir;
   public static final Logger log = LogManager.getLogger("baubles".toUpperCase());
   public static final int GUI = 0;

   public Baubles() {
   }

   public void handleModStateEvent(FMLEvent event) {
      if (event instanceof FMLConstructionEvent) {
         instance = this;
         proxy = FMLCommonHandler.instance().getSide() == Side.CLIENT ? new ClientProxy() : new CommonProxy();
         BaublesCapabilities.register();
         ItemRing.register(MinecraftForge.EVENT_BUS);
      } else if (event instanceof FMLPreInitializationEvent) {
         this.preInit((FMLPreInitializationEvent)event);
      } else if (event instanceof FMLInitializationEvent) {
         this.init((FMLInitializationEvent)event);
      } else if (event instanceof FMLServerStartingEvent) {
         this.serverLoad((FMLServerStartingEvent)event);
      }

   }

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

      CapabilityManager.INSTANCE.register(IBaublesItemHandler.class, new BaublesCapabilities.CapabilityBaubles(), BaublesContainer::new);
      CapabilityManager.INSTANCE.register(IBauble.class, new BaublesCapabilities.CapabilityItemBaubleStorage(), () -> {
         return new BaubleItem(BaubleType.TRINKET);
      });
      proxy.registerEventHandlers();
      PacketHandler.init();
      Config.save();
   }

   public void init(FMLInitializationEvent evt) {
      NetworkRegistry.INSTANCE.registerGuiHandler(instance, proxy);
      proxy.init();
   }

   public void serverLoad(FMLServerStartingEvent event) {
      event.registerServerCommand(new CommandBaubles());
   }
}
