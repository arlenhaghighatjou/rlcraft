package me.ichun.mods.ichunutil.common;

import com.google.common.collect.UnmodifiableIterator;
import java.io.File;
import java.lang.reflect.Field;
import java.util.List;
import me.ichun.mods.ichunutil.client.core.event.EventHandlerClient;
import me.ichun.mods.ichunutil.common.core.Logger;
import me.ichun.mods.ichunutil.common.core.ProxyCommon;
import me.ichun.mods.ichunutil.common.core.config.ConfigBase;
import me.ichun.mods.ichunutil.common.core.config.ConfigHandler;
import me.ichun.mods.ichunutil.common.core.config.annotations.ConfigProp;
import me.ichun.mods.ichunutil.common.core.config.annotations.IntBool;
import me.ichun.mods.ichunutil.common.core.config.annotations.IntMinMax;
import me.ichun.mods.ichunutil.common.core.event.EventHandlerServer;
import me.ichun.mods.ichunutil.common.core.network.PacketChannel;
import me.ichun.mods.ichunutil.common.core.util.ObfHelper;
import me.ichun.mods.ichunutil.common.module.update.UpdateChecker;
import me.ichun.mods.ichunutil.common.module.worldportals.common.WorldPortals;
import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.Mod.Instance;
import net.minecraftforge.fml.common.event.FMLFingerprintViolationEvent;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartedEvent;
import net.minecraftforge.fml.common.event.FMLServerStoppingEvent;
import net.minecraftforge.fml.common.event.FMLInterModComms.IMCEvent;
import net.minecraftforge.fml.common.event.FMLInterModComms.IMCMessage;
import net.minecraftforge.fml.relauncher.Side;

@Mod(
   modid = "ichunutil",
   name = "iChunUtil",
   version = "7.2.2",
   certificateFingerprint = "4db5c2bd1b556f252a5b8b54b256d381b2a0a6b8",
   guiFactory = "me.ichun.mods.ichunutil.common.core.config.GenericModGuiFactory",
   dependencies = "required-after:forge@[14.23.5.2781,99999.24.0.0)",
   acceptableRemoteVersions = "[7.2.0,7.3.0)",
   acceptedMinecraftVersions = "[1.12,1.13)"
)
public class iChunUtil {
   public static final String VERSION_OF_MC = "1.12.2";
   public static final String MC_VERSION_RANGE = "[1.12,1.13)";
   public static final int VERSION_MAJOR = 7;
   public static final int VERSION_MINOR = 2;
   public static final String VERSION = "7.2.2";
   public static final String MOD_NAME = "iChunUtil";
   public static final String MOD_ID = "ichunutil";
   public static final String CERT_FINGERPRINT = "4db5c2bd1b556f252a5b8b54b256d381b2a0a6b8";
   public static final int REQ_FORGE_MAJOR = 14;
   public static final int REQ_FORGE_MINOR = 23;
   public static final int REQ_FORGE_REVISION = 5;
   public static final int REQ_FORGE_BUILD = 2781;
   public static final Logger LOGGER = Logger.createLogger("iChunUtil");
   public static final String GUI_CONFIG_FACTORY = "me.ichun.mods.ichunutil.common.core.config.GenericModGuiFactory";
   @Instance("ichunutil")
   public static iChunUtil instance;
   @SidedProxy(
      clientSide = "me.ichun.mods.ichunutil.client.core.ProxyClient",
      serverSide = "me.ichun.mods.ichunutil.common.core.ProxyCommon"
   )
   public static ProxyCommon proxy;
   public static iChunUtil.Config config;
   public static PacketChannel channel;
   public static EventHandlerServer eventHandlerServer;
   public static EventHandlerClient eventHandlerClient;
   public static Block blockCompactPorkchop;
   public static List<ItemStack> oreDictBlockCompactRawPorkchop;
   private static boolean hasPostInit;
   private static boolean hasMorphMod;
   public static boolean userIsPatron;

   public iChunUtil() {
   }

   @EventHandler
   public void onPreInit(FMLPreInitializationEvent event) {
      ObfHelper.detectObfuscation();
      config = (iChunUtil.Config)ConfigHandler.registerConfig(new iChunUtil.Config(event.getSuggestedConfigurationFile()));
      proxy.preInit();
      UpdateChecker.registerMod(new UpdateChecker.ModVersionInfo("iChunUtil", "1.12.2", "7.2.2", false));
   }

   @EventHandler
   public void onInit(FMLInitializationEvent event) {
      proxy.init();
   }

   @EventHandler
   public void onPostInit(FMLPostInitializationEvent event) {
      hasPostInit = true;
      hasMorphMod = Loader.isModLoaded("morph");
      proxy.postInit();
   }

   @EventHandler
   public void onServerStarted(FMLServerStartedEvent event) {
      UpdateChecker.serverStarted();
   }

   @EventHandler
   public void onServerStopping(FMLServerStoppingEvent event) {
      eventHandlerServer.shuttingDownServer();
      WorldPortals.onServerStopping();
   }

   @EventHandler
   public void onIMCMessage(IMCEvent event) {
      UnmodifiableIterator var2 = event.getMessages().iterator();

      while(var2.hasNext()) {
         IMCMessage message = (IMCMessage)var2.next();
         if (message.key.equalsIgnoreCase("update") && message.isStringMessage()) {
            String[] split = message.getStringValue().split(">");
            if (split.length != 4) {
               LOGGER.info("Invalid update checker string " + message.getStringValue() + ". Invalid argument count!");
            } else {
               UpdateChecker.registerMod(new UpdateChecker.ModVersionInfo(split[0], split[1], split[2], split[3].equalsIgnoreCase("true")));
            }
         }
      }

   }

   @EventHandler
   public void onFingerprintViolation(FMLFingerprintViolationEvent event) {
      if (event.getSource() != null && event.getSource().isFile()) {
         LOGGER.warn("The file " + event.getSource().getName() + " has been modified. Support for the mod will not be provided.");
      }

   }

   public static boolean hasPostInit() {
      return hasPostInit;
   }

   public static boolean hasMorphMod() {
      return hasMorphMod;
   }

   public class Config extends ConfigBase {
      @ConfigProp(
         category = "clientOnly",
         side = Side.CLIENT,
         changeable = false
      )
      @IntBool
      public int enableStencils = 1;
      @ConfigProp(
         category = "clientOnly",
         side = Side.CLIENT
      )
      @IntBool
      public int enableLatchedRendererSpawn = 1;
      @ConfigProp(
         module = "compactPorkchop",
         category = "block",
         useSession = true
      )
      @IntBool
      public int enableCompactPorkchop = 1;
      @ConfigProp(
         module = "eula"
      )
      public String eulaAcknowledged = "";
      @ConfigProp(
         module = "patreon",
         side = Side.CLIENT,
         hidden = true
      )
      @IntBool
      public int showPatronReward = 1;
      @ConfigProp(
         module = "patreon",
         side = Side.CLIENT,
         hidden = true
      )
      @IntMinMax(
         min = 1,
         max = 6
      )
      public int patronRewardType = 1;
      @ConfigProp(
         module = "versionCheck"
      )
      @IntMinMax(
         min = 0,
         max = 2
      )
      public int versionNotificationTypes = 0;
      @ConfigProp(
         module = "versionCheck",
         side = Side.CLIENT
      )
      @IntMinMax(
         min = 0,
         max = 2
      )
      public int versionNotificationFrequency = 2;
      @ConfigProp(
         module = "versionCheck",
         side = Side.CLIENT
      )
      @IntMinMax(
         min = 0,
         max = 35
      )
      public int versionSave = 0;
      @ConfigProp(
         module = "headTracking",
         side = Side.CLIENT,
         hidden = true
      )
      @IntMinMax(
         min = 0,
         max = 2
      )
      public int aggressiveHeadTracking = 0;
      @ConfigProp(
         module = "headTracking",
         side = Side.CLIENT,
         hidden = true
      )
      @IntBool
      public int horseEasterEgg = 1;
      @ConfigProp(
         module = "worldPortals",
         side = Side.CLIENT,
         hidden = true
      )
      @IntMinMax(
         min = 0,
         max = 10
      )
      public int maxRecursion = 2;
      @ConfigProp(
         module = "worldPortals",
         side = Side.CLIENT,
         hidden = true
      )
      @IntMinMax(
         min = 1,
         max = 255
      )
      public int stencilValue = 47;
      @ConfigProp(
         module = "worldPortals",
         side = Side.CLIENT,
         hidden = true
      )
      @IntMinMax(
         min = 0,
         max = 16
      )
      public int renderDistanceChunks = 0;
      @ConfigProp(
         module = "worldPortals",
         side = Side.CLIENT,
         hidden = true
      )
      @IntMinMax(
         min = 1,
         max = 100
      )
      public int maxRendersPerTick = 10;

      public Config(File file) {
         super(file);
      }

      public String getModId() {
         return "ichunutil";
      }

      public String getModName() {
         return "iChunUtil";
      }

      public void onConfigChange(Field field, Object original) {
         if (field.getName().equals("showPatronReward") || field.getName().equals("patronRewardType")) {
            iChunUtil.eventHandlerClient.patronUpdateServerAsPatron = true;
         }

      }
   }
}
