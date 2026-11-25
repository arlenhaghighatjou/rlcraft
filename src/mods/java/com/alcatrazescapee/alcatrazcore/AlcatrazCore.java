package com.alcatrazescapee.alcatrazcore;

import com.alcatrazescapee.alcatrazcore.proxy.IProxy;
import com.alcatrazescapee.alcatrazcore.util.OreDictionaryHelper;
import com.alcatrazescapee.alcatrazcore.util.compat.FireRegistry;
import com.alcatrazescapee.alcatrazcore.util.compat.WrenchRegistry;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.ICrashCallable;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.Mod.Instance;
import net.minecraftforge.fml.common.event.FMLFingerprintViolationEvent;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.lax1dude.eaglercraft.v1_8.log4j.Logger;

@Mod(
   modid = "alcatrazcore",
   version = "1.0.4",
   dependencies = "required-after:forge@[14.23.4.2705,15.0.0.0);",
   name = "Alcatraz Core",
   useMetadata = true,
   certificateFingerprint = "3c2d6be715971d1ed58a028cdb3fae72987fc934",
   acceptedMinecraftVersions = "[1.12.2]"
)
public final class AlcatrazCore {
   public static final String MOD_ID = "alcatrazcore";
   public static final String MOD_NAME = "Alcatraz Core";
   public static final String VERSION = "1.0.4";
   private static final String FORGE_MIN = "14.23.4.2705";
   private static final String FORGE_MAX = "15.0.0.0";
   public static final String DEPENDENCIES = "required-after:forge@[14.23.4.2705,15.0.0.0);";
   private static final String PROXY_PATH = "com.alcatrazescapee.alcatrazcore.proxy.";
   @Instance
   private static AlcatrazCore instance;
   @SidedProxy(
      modId = "alcatrazcore",
      clientSide = "com.alcatrazescapee.alcatrazcore.proxy.ClientProxy",
      serverSide = "com.alcatrazescapee.alcatrazcore.proxy.ServerProxy"
   )
   private static IProxy proxy;
   private Logger log;
   private boolean isSignedBuild = true;

   public AlcatrazCore() {
   }

   public static Logger getLog() {
      return instance.log;
   }

   public static AlcatrazCore getInstance() {
      return instance;
   }

   public static IProxy getProxy() {
      return proxy;
   }

   @EventHandler
   public void preInit(FMLPreInitializationEvent event) {
      this.log = event.getModLog();
      if (!this.isSignedBuild) {
         this.log.warn("You are not running an official build. This version will NOT be supported by the author.");
      }

   }

   @EventHandler
   public void init(FMLInitializationEvent event) {
      if (!this.isSignedBuild) {
         this.log.warn("You are not running an official build. This version will NOT be supported by the author.");
      }

      OreDictionaryHelper.init();
   }

   @EventHandler
   public void postInit(FMLPostInitializationEvent event) {
      if (!this.isSignedBuild) {
         this.log.warn("You are not running an official build. This version will NOT be supported by the author.");
      }

      FireRegistry.init();
      WrenchRegistry.init();
   }

   @EventHandler
   public void onFingerprintViolation(FMLFingerprintViolationEvent event) {
      this.isSignedBuild = false;
      FMLCommonHandler.instance().registerCrashCallable(new ICrashCallable() {
         public String getLabel() {
            return "Alcatraz Core";
         }

         public String call() {
            return "You are not running an official build. This version will NOT be supported by the author.";
         }
      });
   }
}
