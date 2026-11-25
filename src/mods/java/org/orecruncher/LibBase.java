package org.orecruncher;

import javax.annotation.Nonnull;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.Mod.InstanceFactory;
import net.minecraftforge.fml.common.event.FMLFingerprintViolationEvent;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLLoadCompleteEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerLoggedInEvent;
import org.apache.logging.log4j.LogManager;
import org.orecruncher.lib.VersionChecker;
import org.orecruncher.lib.logging.ModLog;
import org.orecruncher.proxy.Proxy;

@Mod(
   modid = "orelib",
   useMetadata = true,
   dependencies = "required-after:forge@[14.23.5.2779,);",
   version = "3.6.0.1",
   acceptedMinecraftVersions = "[1.12.2,)",
   acceptableRemoteVersions = "*",
   updateJSON = "https://raw.githubusercontent.com/OreCruncher/OreLib/master/version.json",
   certificateFingerprint = "7a2128d395ad96ceb9d9030fbd41d035b435753a"
)
public class LibBase {
   private static final ModLog logger = ModLog.setLogger("orelib", LogManager.getLogger("orelib"));
   private static final LibBase instance = new LibBase();
   @SidedProxy(
      clientSide = "org.orecruncher.proxy.ProxyClient",
      serverSide = "org.orecruncher.proxy.Proxy"
   )
   protected static Proxy proxy;

   @InstanceFactory
   @Nonnull
   public static LibBase instance() {
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

   public LibBase() {
      MinecraftForge.EVENT_BUS.register(this);
   }

   @EventHandler
   public void preInit(@Nonnull FMLPreInitializationEvent event) {
      proxy.preInit(event);
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
      log().warn("Invalid fingerprint detected!");
   }

   @SubscribeEvent
   public void playerLogin(PlayerLoggedInEvent event) {
      if (LibOptions.logging.enableVersionCheck) {
         VersionChecker.doCheck(event, "orelib");
      }

   }
}
