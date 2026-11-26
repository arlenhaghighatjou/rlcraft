package net.silentchaos512.lib;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.event.FMLConstructionEvent;
import net.minecraftforge.fml.common.event.FMLEvent;
import javax.annotation.ParametersAreNonnullByDefault;
import mcp.MethodsReturnNonnullByDefault;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.silentchaos512.lib.advancements.LibTriggers;
import net.silentchaos512.lib.base.IModBase;
import net.silentchaos512.lib.command.internal.CommandDataDump;
import net.silentchaos512.lib.gui.GuiHandlerLibF;
import net.silentchaos512.lib.network.NetworkHandlerSL;
import net.silentchaos512.lib.network.internal.MessageLeftClick;
import net.silentchaos512.lib.proxy.internal.SilentLibCommonProxy;
import net.silentchaos512.lib.util.I18nHelper;
import net.silentchaos512.lib.util.LogHelper;
import net.lax1dude.eaglercraft.v1_8.log4j.LogManager;
import net.lax1dude.eaglercraft.v1_8.log4j.Logger;

@Mod(
   modid = "silentlib",
   name = "Silent Lib",
   version = "3.0.13",
   dependencies = "required-after:forge@[14.23.3.2669,);",
   acceptedMinecraftVersions = "[1.12.2]"
)
@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public final class SilentLib implements IModBase {
   public static final String MOD_ID = "silentlib";
   public static final String MOD_NAME = "Silent Lib";
   public static final String VERSION = "3.0.13";
   public static final int BUILD_NUM = 168;
   public static final String DEPENDENCIES = "required-after:forge@[14.23.3.2669,);";
   public static NetworkHandlerSL network;
   public static final Logger LOGGER = LogManager.getLogger("Silent Lib");
   /** @deprecated */
   @Deprecated
   public static LogHelper logHelper = new LogHelper("Silent Lib", 168);
   /** @deprecated */
   @Deprecated
   public static I18nHelper i18n = new I18nHelper("silentlib", logHelper, false);
   public static SilentLib instance;
   public static SilentLibCommonProxy proxy;

   public SilentLib() {
   }

   public void handleModStateEvent(FMLEvent event) {
      if (event instanceof FMLConstructionEvent) {
         instance = this;
         proxy = FMLCommonHandler.instance().getSide() == Side.CLIENT ? new net.silentchaos512.lib.proxy.internal.SilentLibClientProxy() : new net.silentchaos512.lib.proxy.internal.SilentLibServerProxy();
         if (FMLCommonHandler.instance().getSide() == Side.CLIENT) {
            net.silentchaos512.lib.event.ClientTicks.register(MinecraftForge.EVENT_BUS);
         }
         net.silentchaos512.lib.event.Greetings.register(MinecraftForge.EVENT_BUS);
         net.silentchaos512.lib.event.InitialSpawnItems.register(MinecraftForge.EVENT_BUS);
         net.silentchaos512.lib.event.ServerTicks.register(MinecraftForge.EVENT_BUS);
         net.silentchaos512.lib.util.EntityHelper.register(MinecraftForge.EVENT_BUS);
      } else if (event instanceof FMLPreInitializationEvent) {
         this.preInit((FMLPreInitializationEvent)event);
      } else if (event instanceof FMLInitializationEvent) {
         this.init((FMLInitializationEvent)event);
      } else if (event instanceof FMLPostInitializationEvent) {
         this.postInit((FMLPostInitializationEvent)event);
      } else if (event instanceof FMLServerStartingEvent) {
         this.onServerStarting((FMLServerStartingEvent)event);
      }

   }

   public void preInit(FMLPreInitializationEvent event) {
      network = new NetworkHandlerSL("silentlib");
      network.register(MessageLeftClick.class, MessageLeftClick::new, Side.SERVER);
      NetworkRegistry.INSTANCE.registerGuiHandler(this, new GuiHandlerLibF());
      LibTriggers.init();
      proxy.preInit(event);
   }

   public void init(FMLInitializationEvent event) {
      proxy.init(event);
   }

   public void postInit(FMLPostInitializationEvent event) {
      proxy.postInit(event);
   }

   public void onServerStarting(FMLServerStartingEvent event) {
      event.registerServerCommand(new CommandDataDump());
   }

   public String getModId() {
      return "silentlib";
   }

   public String getModName() {
      return "Silent Lib";
   }

   public String getVersion() {
      return "3.0.13";
   }

   public int getBuildNum() {
      return 168;
   }
}
