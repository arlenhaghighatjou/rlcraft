package me.ichun.mods.googlyeyes.common;

import java.util.function.BooleanSupplier;
import me.ichun.mods.googlyeyes.common.core.Config;
import me.ichun.mods.googlyeyes.common.core.EventHandler;
import me.ichun.mods.ichunutil.api.client.head.HeadBase;
import me.ichun.mods.ichunutil.common.iChunUtil;
import me.ichun.mods.ichunutil.common.core.config.ConfigHandler;
import me.ichun.mods.ichunutil.common.module.update.UpdateChecker;
import me.ichun.mods.ichunutil.common.module.update.UpdateChecker.ModVersionInfo;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLFingerprintViolationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

@Mod(
   name = "GooglyEyes",
   modid = "googlyeyes",
   version = "7.1.1",
   certificateFingerprint = "4db5c2bd1b556f252a5b8b54b256d381b2a0a6b8",
   clientSideOnly = true,
   acceptableRemoteVersions = "*",
   guiFactory = "me.ichun.mods.ichunutil.common.core.config.GenericModGuiFactory",
   dependencies = "required-after:ichunutil@[7.2.0,8.0.0)",
   acceptedMinecraftVersions = "[1.12,1.13)"
)
public class GooglyEyes {
   public static final String VERSION = "7.1.1";
   public static final String MOD_NAME = "GooglyEyes";
   public static final String MOD_ID = "googlyeyes";
   public static EventHandler eventHandler;
   public static Config config;

   public GooglyEyes() {
   }

   @net.minecraftforge.fml.common.Mod.EventHandler
   public void preInit(FMLPreInitializationEvent event) {
      config = (Config)ConfigHandler.registerConfig(new Config(event.getSuggestedConfigurationFile()));
      eventHandler = new EventHandler();
      MinecraftForge.EVENT_BUS.register(eventHandler);
      UpdateChecker.registerMod(new ModVersionInfo("GooglyEyes", "1.12.2", "7.1.1", true));
      BooleanSupplier oldAcidEyesBooleanSupplier = HeadBase.acidEyesBooleanSupplier;
      HeadBase.acidEyesBooleanSupplier = () -> {
         return config.acidTripEyes == 1 || oldAcidEyesBooleanSupplier.getAsBoolean();
      };
      iChunUtil.config.reveal(new String[]{"aggressiveHeadTracking", "horseEasterEgg"});
   }

   @net.minecraftforge.fml.common.Mod.EventHandler
   public void onFingerprintViolation(FMLFingerprintViolationEvent event) {
      if (event.getSource() != null && event.getSource().isFile()) {
         System.out.println("GOOGLYEYES - The file " + event.getSource().getName() + " has been modified. Support for the mod will not be provided.");
      }

   }
}
