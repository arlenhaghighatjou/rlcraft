package net.ilexiconn.llibrary;

import java.io.File;
import java.util.Map;
import javax.annotation.Nonnull;
import net.ilexiconn.llibrary.client.lang.LanguageHandler;
import net.ilexiconn.llibrary.client.util.ItemTESRContext;
import net.ilexiconn.llibrary.server.ServerProxy;
import net.ilexiconn.llibrary.server.capability.IEntityDataCapability;
import net.ilexiconn.llibrary.server.config.ConfigHandler;
import net.ilexiconn.llibrary.server.config.LLibraryConfig;
import net.ilexiconn.llibrary.server.core.api.LLibraryCoreAPI;
import net.ilexiconn.llibrary.server.core.plugin.LLibraryPlugin;
import net.ilexiconn.llibrary.server.network.AnimationMessage;
import net.ilexiconn.llibrary.server.network.BlockEntityMessage;
import net.ilexiconn.llibrary.server.network.NetworkHandler;
import net.ilexiconn.llibrary.server.network.NetworkWrapper;
import net.ilexiconn.llibrary.server.network.PropertiesMessage;
import net.ilexiconn.llibrary.server.network.SnackbarMessage;
import net.ilexiconn.llibrary.server.network.SurvivalTabMessage;
import net.ilexiconn.llibrary.server.world.TickRateHandler;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms.TransformType;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.ModContainer;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.Mod.Instance;
import net.minecraftforge.fml.common.event.FMLFingerprintViolationEvent;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(
   modid = "llibrary",
   name = "LLibrary",
   version = "1.7.20",
   acceptedMinecraftVersions = "1.12.2",
   certificateFingerprint = "b9f30a813bee3b9dd5652c460310cfcd54f6b7ec",
   guiFactory = "net.ilexiconn.llibrary.client.gui.LLibraryGUIFactory",
   updateJSON = "https://gist.githubusercontent.com/Gegy/a6639456aeb8edd92cbf7cbfcf9d65d9/raw/llibrary_updates.json",
   dependencies = "required-after:forge@[14.23.5.2772,)"
)
public class LLibrary {
   public static final String VERSION = "1.7.20";
   public static final Logger LOGGER = LogManager.getLogger("LLibrary");
   @SidedProxy(
      serverSide = "net.ilexiconn.llibrary.server.ServerProxy",
      clientSide = "net.ilexiconn.llibrary.client.ClientProxy"
   )
   public static ServerProxy PROXY;
   @Instance("llibrary")
   public static LLibrary INSTANCE;
   @CapabilityInject(IEntityDataCapability.class)
   public static Capability<IEntityDataCapability> ENTITY_DATA_CAPABILITY;
   public static LLibraryConfig CONFIG = new LLibraryConfig();
   @NetworkWrapper({AnimationMessage.class, PropertiesMessage.class, SnackbarMessage.class, BlockEntityMessage.class, SurvivalTabMessage.class})
   public static SimpleNetworkWrapper NETWORK_WRAPPER;
   public static int QUBBLE_VERSION = 1;
   public static int QUBBLE_VANILLA_VERSION = 1;
   public static final File LLIBRARY_ROOT = new File(".", "llibrary");

   public LLibrary() {
   }

   @EventHandler
   public void onPreInit(FMLPreInitializationEvent event) {
      if (!LLIBRARY_ROOT.exists()) {
         LLIBRARY_ROOT.mkdirs();
      }

      for(ModContainer mod : Loader.instance().getModList()) {
         ConfigHandler.INSTANCE.injectConfig(mod, event.getAsmData());
         NetworkHandler.INSTANCE.injectNetworkWrapper(mod, event.getAsmData());
      }

      CONFIG.load();
      PROXY.onPreInit();
   }

   @EventHandler
   public void onInit(FMLInitializationEvent event) {
      PROXY.onInit();
   }

   @EventHandler
   public void onPostInit(FMLPostInitializationEvent event) {
      PROXY.onPostInit();
   }

   @EventHandler
   public void onFingerprintViolation(FMLFingerprintViolationEvent event) {
      LOGGER.warn("Detected invalid fingerprint for file {}! You will not receive support with this tampered version of llibrary!", event.getSource().getName());
   }

   static {
      try {
         LLibraryPlugin.api = new LLibrary.CoreAPIHandler();
      } catch (Throwable var1) {
         LOGGER.error("Failed to load LLibrary Core API. Is it missing?", var1);
      }

   }

   static class CoreAPIHandler implements LLibraryCoreAPI {
      CoreAPIHandler() {
      }

      @SideOnly(Side.CLIENT)
      public void addRemoteLocalizations(String language, Map<String, String> properties) {
         LanguageHandler.INSTANCE.addRemoteLocalizations(language, properties);
      }

      @SideOnly(Side.CLIENT)
      public void provideStackContext(@Nonnull ItemStack stack) {
         ItemTESRContext.INSTANCE.provideStackContext(stack);
      }

      @SideOnly(Side.CLIENT)
      public void providePerspectiveContext(@Nonnull TransformType transform) {
         ItemTESRContext.INSTANCE.providePerspectiveContext(transform);
      }

      public long getTickRate() {
         return TickRateHandler.INSTANCE.getTickRate();
      }
   }
}
