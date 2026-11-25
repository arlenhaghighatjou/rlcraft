package org.orecruncher.dsurround;

import java.io.File;
import java.util.Map;
import javax.annotation.Nonnull;
import net.minecraft.client.Minecraft;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.Mod.Instance;
import net.minecraftforge.fml.common.event.FMLFingerprintViolationEvent;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLLoadCompleteEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerAboutToStartEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import net.minecraftforge.fml.common.event.FMLServerStoppedEvent;
import net.minecraftforge.fml.common.event.FMLServerStoppingEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerLoggedInEvent;
import net.minecraftforge.fml.common.network.NetworkCheckHandler;
import net.minecraftforge.fml.common.network.FMLNetworkEvent.ClientConnectedToServerEvent;
import net.minecraftforge.fml.common.network.FMLNetworkEvent.ClientDisconnectionFromServerEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.orecruncher.dsurround.proxy.IProxy;
import org.orecruncher.lib.VersionChecker;
import org.orecruncher.lib.VersionHelper;
import org.orecruncher.lib.logging.ModLog;

@Mod(
   modid = "dsurround",
   useMetadata = true,
   dependencies = "required-after:forge@[14.23.5.2779,);required-after:orelib@[3.6.0.1,);after:sereneseasons@[1.2.18,);after:animania@[2.0.0,);after:galacticraftcore;after:ambientsounds;",
   version = "3.6.2.1",
   acceptedMinecraftVersions = "[1.12.2,)",
   acceptableRemoteVersions = "*",
   guiFactory = "org.orecruncher.dsurround.client.gui.ConfigGuiFactory",
   updateJSON = "https://raw.githubusercontent.com/OreCruncher/DynamicSurroundings/master/version.json",
   certificateFingerprint = "7a2128d395ad96ceb9d9030fbd41d035b435753a"
)
public class ModBase {
   public static final String SERVER_VERSION = "3.5.0.0";
   @Instance("dsurround")
   protected static ModBase instance;
   @SidedProxy(
      clientSide = "org.orecruncher.dsurround.proxy.ProxyClient",
      serverSide = "org.orecruncher.dsurround.proxy.Proxy"
   )
   protected static IProxy proxy;
   protected static ModLog logger = ModLog.NULL_LOGGER;
   protected static Configuration config;
   protected static File dataDirectory;
   protected static boolean installedOnServer;
   protected static boolean devMode;
   protected static boolean isInitialized = false;

   @Nonnull
   public static ModBase instance() {
      return instance;
   }

   @Nonnull
   public static IProxy proxy() {
      return proxy;
   }

   @Nonnull
   public static Configuration config() {
      return config;
   }

   @Nonnull
   public static ModLog log() {
      return logger;
   }

   @Nonnull
   public static File dataDirectory() {
      return dataDirectory;
   }

   public static boolean isInstalledOnServer() {
      return installedOnServer;
   }

   public static boolean isDeveloperMode() {
      return devMode;
   }

   public static boolean isInitialized() {
      return isInitialized;
   }

   @SideOnly(Side.CLIENT)
   public static boolean routePacketToServer() {
      return isInstalledOnServer() && !Minecraft.getMinecraft().isIntegratedServerRunning();
   }

   public ModBase() {
      logger = ModLog.setLogger("dsurround", LogManager.getLogger("dsurround"));
      String cmdText = System.getProperty("dsurround.devMode");
      if (!StringUtils.isEmpty(cmdText) && "true".equals(cmdText)) {
         devMode = true;
         logger.info("RUNNING IN DEVELOPMENT MODE", new Object[0]);
      }

   }

   @EventHandler
   public void preInit(@Nonnull FMLPreInitializationEvent event) {
      MinecraftForge.EVENT_BUS.register(this);
      dataDirectory = new File(event.getModConfigurationDirectory(), "dsurround");
      dataDirectory.mkdirs();
      config = new Configuration(new File(dataDirectory, "dsurround.cfg"), "3.6.2.1");
      config.load();
      ModOptions.load(config);
      config.save();
      logger.setDebug(ModOptions.logging.enableDebugLogging);
      logger.setTraceMask(ModOptions.logging.debugFlagMask);
      proxy.preInit(event);
   }

   @EventHandler
   public void init(@Nonnull FMLInitializationEvent event) {
      proxy.init(event);
   }

   @EventHandler
   public void postInit(@Nonnull FMLPostInitializationEvent event) {
      proxy.postInit(event);
      config.save();
   }

   @EventHandler
   public void loadCompleted(@Nonnull FMLLoadCompleteEvent event) {
      proxy.loadCompleted(event);
      isInitialized = true;
   }

   @EventHandler
   public void onFingerprintViolation(@Nonnull FMLFingerprintViolationEvent event) {
      log().warn("Invalid fingerprint detected!", new Object[0]);
   }

   @NetworkCheckHandler
   public boolean checkModLists(@Nonnull Map<String, String> modList, @Nonnull Side side) {
      String modVersion = (String)modList.get("dsurround");
      if (side == Side.SERVER) {
         installedOnServer = !StringUtils.isEmpty(modVersion);
         if (installedOnServer) {
            log().info("%s version %s is installed on the server", new Object[]{"Dynamic Surroundings", modVersion});
            if (VersionHelper.compareVersions(modVersion, "3.5.0.0") < 0) {
               log().info("For the best experience the server should be running at least version %s", new Object[]{"3.5.0.0"});
            }
         }
      }

      return true;
   }

   @SubscribeEvent(
      priority = EventPriority.LOWEST
   )
   public void clientConnect(@Nonnull ClientConnectedToServerEvent event) {
      proxy.clientConnect(event);
   }

   @SubscribeEvent(
      priority = EventPriority.LOWEST
   )
   public void clientDisconnect(@Nonnull ClientDisconnectionFromServerEvent event) {
      proxy.clientDisconnect(event);
      installedOnServer = false;
   }

   @SubscribeEvent
   public void playerLogin(PlayerLoggedInEvent event) {
      if (ModOptions.logging.enableVersionChecking) {
         VersionChecker.doCheck(event, "dsurround");
      }

   }

   @EventHandler
   public void serverAboutToStart(@Nonnull FMLServerAboutToStartEvent event) {
      proxy.serverAboutToStart(event);
   }

   @EventHandler
   public void serverStarting(@Nonnull FMLServerStartingEvent event) {
      proxy.serverStarting(event);
   }

   @EventHandler
   public void serverStopping(@Nonnull FMLServerStoppingEvent event) {
      proxy.serverStopping(event);
   }

   @EventHandler
   public void serverStopped(@Nonnull FMLServerStoppedEvent event) {
      proxy.serverStopped(event);
   }
}
