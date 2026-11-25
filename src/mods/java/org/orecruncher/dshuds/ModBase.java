package org.orecruncher.dshuds;

import javax.annotation.Nonnull;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.Mod.Instance;
import net.minecraftforge.fml.common.event.FMLFingerprintViolationEvent;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLLoadCompleteEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerLoggedInEvent;
import net.minecraftforge.fml.common.network.FMLNetworkEvent.ClientConnectedToServerEvent;
import net.minecraftforge.fml.common.network.FMLNetworkEvent.ClientDisconnectionFromServerEvent;
import org.apache.logging.log4j.LogManager;
import org.orecruncher.dshuds.proxy.Proxy;
import org.orecruncher.lib.VersionChecker;
import org.orecruncher.lib.logging.ModLog;

@Mod(
   modid = "dshuds",
   clientSideOnly = true,
   useMetadata = true,
   dependencies = "required-after:forge@[14.23.5.2779,);required-after:orelib@[3.6.0.0,);after:sereneseasons@[1.2.18,);after:dsurround@[3.6.0.0,)",
   version = "3.6.0.0",
   acceptedMinecraftVersions = "[1.12.2,)",
   updateJSON = "https://raw.githubusercontent.com/OreCruncher/DynamicSurroundingsHuds/master/version.json",
   certificateFingerprint = "7a2128d395ad96ceb9d9030fbd41d035b435753a"
)
public class ModBase {
   @Instance("dshuds")
   protected static ModBase instance;
   @SidedProxy(
      clientSide = "org.orecruncher.dshuds.proxy.ProxyClient"
   )
   protected static Proxy proxy;
   protected static ModLog logger = ModLog.NULL_LOGGER;

   @Nonnull
   public static ModBase instance() {
      return instance;
   }

   @Nonnull
   public static Proxy proxy() {
      return proxy;
   }

   @Nonnull
   public static ModLog log() {
      return logger;
   }

   public ModBase() {
      logger = ModLog.setLogger("dshuds", LogManager.getLogger("dshuds"));
   }

   @EventHandler
   public void preInit(@Nonnull FMLPreInitializationEvent event) {
      logger = ModLog.setLogger("dshuds", event.getModLog());
      logger.setDebug(ModOptions.logging.enableLogging);
      MinecraftForge.EVENT_BUS.register(this);
      proxy().preInit(event);
   }

   @EventHandler
   public void init(@Nonnull FMLInitializationEvent event) {
      proxy.init(event);
   }

   @EventHandler
   public void postInit(@Nonnull FMLPostInitializationEvent event) {
      proxy.postInit(event);
   }

   @EventHandler
   public void loadCompleted(@Nonnull FMLLoadCompleteEvent event) {
      proxy.loadCompleted(event);
   }

   @EventHandler
   public void onFingerprintViolation(@Nonnull FMLFingerprintViolationEvent event) {
      log().warn("Invalid fingerprint detected!", new Object[0]);
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
   }

   @SubscribeEvent
   public void playerLogin(PlayerLoggedInEvent event) {
      if (ModOptions.logging.enableVersionCheck) {
         VersionChecker.doCheck(event, "dshuds");
      }

   }
}
