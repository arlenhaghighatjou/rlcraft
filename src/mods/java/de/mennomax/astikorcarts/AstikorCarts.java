package de.mennomax.astikorcarts;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.event.FMLConstructionEvent;
import net.minecraftforge.fml.common.event.FMLEvent;
import de.mennomax.astikorcarts.capabilities.IPull;
import de.mennomax.astikorcarts.capabilities.PullFactory;
import de.mennomax.astikorcarts.capabilities.PullStorage;
import de.mennomax.astikorcarts.handler.GuiHandler;
import de.mennomax.astikorcarts.handler.PacketHandler;
import de.mennomax.astikorcarts.proxy.IProxy;
import java.util.concurrent.Callable;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;

@Mod(
   modid = "astikorcarts",
   version = "1.12.2-0.1.2.8",
   acceptedMinecraftVersions = "[1.12,1.13)"
)
public class AstikorCarts {
   public static final String MODID = "astikorcarts";
   public static final String VERSION = "1.12.2-0.1.2.8";
   public static IProxy proxy;
   public static AstikorCarts instance;

   public AstikorCarts() {
   }

   public void handleModStateEvent(FMLEvent event) {
      if (event instanceof FMLConstructionEvent) {
         instance = this;
         proxy = FMLCommonHandler.instance().getSide() == Side.CLIENT ? new de.mennomax.astikorcarts.proxy.ClientProxy() : new de.mennomax.astikorcarts.proxy.ServerProxy();
         de.mennomax.astikorcarts.config.ModConfig.register();
         de.mennomax.astikorcarts.capabilities.PullProvider.registerCapabilities();
         de.mennomax.astikorcarts.init.ModItems.registerHolders();
         de.mennomax.astikorcarts.config.ModConfig.EventHandler.register(MinecraftForge.EVENT_BUS);
         if (FMLCommonHandler.instance().getSide() == Side.CLIENT) {
            de.mennomax.astikorcarts.handler.ClientEventHandler.register(MinecraftForge.EVENT_BUS);
         }
         de.mennomax.astikorcarts.handler.CommonEventHandler.register(MinecraftForge.EVENT_BUS);
         de.mennomax.astikorcarts.handler.MissingMappingHandler.register(MinecraftForge.EVENT_BUS);
         de.mennomax.astikorcarts.init.ModEntities.EntityRegistrationHandler.register(MinecraftForge.EVENT_BUS);
         de.mennomax.astikorcarts.init.ModItems.ItemRegistrationHandler.register(MinecraftForge.EVENT_BUS);
      } else if (event instanceof FMLPreInitializationEvent) {
         this.preInit((FMLPreInitializationEvent)event);
      } else if (event instanceof FMLInitializationEvent) {
         this.init((FMLInitializationEvent)event);
      } else if (event instanceof FMLPostInitializationEvent) {
         this.postInit((FMLPostInitializationEvent)event);
      }

   }

   public void preInit(FMLPreInitializationEvent event) {
      PacketHandler.registerPackets();
      CapabilityManager.INSTANCE.register(IPull.class, new PullStorage(), PullFactory::new);
      proxy.preInit();
   }

   public void init(FMLInitializationEvent event) {
      NetworkRegistry.INSTANCE.registerGuiHandler(instance, new GuiHandler());
      proxy.init();
   }

   public void postInit(FMLPostInitializationEvent event) {
      proxy.postInit();
   }
}
