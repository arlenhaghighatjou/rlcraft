package dev.satyrn.wolfarmor;

import dev.satyrn.wolfarmor.api.common.IProxy;
import dev.satyrn.wolfarmor.api.compatibility.Compatibility;
import dev.satyrn.wolfarmor.api.util.ILogHelper;
import dev.satyrn.wolfarmor.common.network.WolfArmorChannel;
import dev.satyrn.wolfarmor.config.WolfArmorConfig;
import dev.satyrn.wolfarmor.util.LogHelper;
import javax.annotation.Nonnull;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.Mod.Instance;
import net.minecraftforge.fml.common.event.FMLFingerprintViolationEvent;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLLoadCompleteEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import org.apache.logging.log4j.LogManager;

@Mod(
   useMetadata = true,
   modid = "wolfarmor",
   name = "Wolf Armor and Storage",
   version = "3.8.1+alpha.1",
   guiFactory = "dev.satyrn.wolfarmor.client.gui.config.WolfArmorGuiFactory",
   certificateFingerprint = "e94e38a605842477f3ec218e6fcf781f6b3f7f89"
)
public class WolfArmorMod {
   public static final String MOD_VERSION = "3.8.1+alpha.1";
   @SidedProxy(
      clientSide = "dev.satyrn.wolfarmor.client.ClientProxy",
      serverSide = "dev.satyrn.wolfarmor.common.CommonProxy"
   )
   public static IProxy proxy;
   @Instance("wolfarmor")
   public static WolfArmorMod instance;
   private final ILogHelper logger;
   private final WolfArmorConfig config;
   private WolfArmorChannel channel;

   public WolfArmorMod() {
      this(new WolfArmorConfig(), new LogHelper());
   }

   WolfArmorMod(WolfArmorConfig configInstance, ILogHelper loggerInstance) {
      this.logger = loggerInstance;
      this.config = configInstance;
   }

   @EventHandler
   public void preInit(@Nonnull FMLPreInitializationEvent preInitializationEvent) {
      this.logger.initializeLogger(preInitializationEvent.getModLog());
      this.channel = new WolfArmorChannel();
      Compatibility.register("dev.satyrn.wolfarmor.compatibility.sophisticatedwolves.SophisticatedWolvesProvider");
      Compatibility.register("dev.satyrn.wolfarmor.compatibility.dogslie.LetSleepingDogsLieProvider");
      Compatibility.register("dev.satyrn.wolfarmor.compatibility.ebwizardry.WizardryProvider");
      Compatibility.register("dev.satyrn.wolfarmor.compatibility.mobends.MoBendsProvider");
      this.config.initialize(preInitializationEvent.getModConfigurationDirectory() + "/satyrn");
      proxy.preInit(preInitializationEvent);
   }

   @EventHandler
   public void init(@Nonnull FMLInitializationEvent initializationEvent) {
      proxy.init(initializationEvent);
   }

   @EventHandler
   public void postInit(@Nonnull FMLPostInitializationEvent postInitializationEvent) {
      proxy.postInit(postInitializationEvent);
   }

   @EventHandler
   public void loadComplete(@Nonnull FMLLoadCompleteEvent loadCompleteEvent) {
      proxy.loadComplete(loadCompleteEvent);
   }

   @EventHandler
   public void onFingerprintViolation(FMLFingerprintViolationEvent event) {
      LogManager.getLogger("wolfarmor").warn("Invalid fingerprint detected! This might mean the mod is compromised, or maybe you're just in a dev environment...");
   }

   @Nonnull
   public static ILogHelper getLogger() {
      return getInstance().logger;
   }

   @Nonnull
   public static WolfArmorConfig getConfig() {
      return getInstance().config;
   }

   public static WolfArmorChannel getNetworkChannel() {
      return getInstance().channel;
   }

   public static IProxy getProxy() {
      return proxy;
   }

   public static WolfArmorMod getInstance() {
      return instance;
   }
}
