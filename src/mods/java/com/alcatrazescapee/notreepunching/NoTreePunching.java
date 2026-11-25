package com.alcatrazescapee.notreepunching;

import com.alcatrazescapee.notreepunching.client.ModGuiHandler;
import com.alcatrazescapee.notreepunching.client.ModSounds;
import com.alcatrazescapee.notreepunching.common.blocks.ModBlocks;
import com.alcatrazescapee.notreepunching.common.capability.CapabilityPlayerItem;
import com.alcatrazescapee.notreepunching.common.items.ModItems;
import com.alcatrazescapee.notreepunching.common.recipe.ModRecipes;
import com.alcatrazescapee.notreepunching.util.HarvestBlockHandler;
import com.alcatrazescapee.notreepunching.util.WoodRecipeHandler;
import com.alcatrazescapee.notreepunching.world.WorldGenRocks;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.ICrashCallable;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.Mod.Instance;
import net.minecraftforge.fml.common.event.FMLFingerprintViolationEvent;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.lax1dude.eaglercraft.v1_8.log4j.Logger;

@Mod(
   modid = "notreepunching",
   version = "2.0.21",
   dependencies = "required-after:forge@[14.23.4.2705,15.0.0.0);required-after:alcatrazcore@[1.0.0,2.0.0);",
   useMetadata = true,
   certificateFingerprint = "3c2d6be715971d1ed58a028cdb3fae72987fc934",
   acceptedMinecraftVersions = "[1.12.2]"
)
public final class NoTreePunching {
   public static final String MOD_ID = "notreepunching";
   public static final String MOD_NAME = "No Tree Punching";
   public static final String VERSION = "2.0.21";
   private static final String FORGE_MIN = "14.23.4.2705";
   private static final String FORGE_MAX = "15.0.0.0";
   private static final String ALC_MIN = "1.0.0";
   private static final String ALC_MAX = "2.0.0";
   public static final String DEPENDENCIES = "required-after:forge@[14.23.4.2705,15.0.0.0);required-after:alcatrazcore@[1.0.0,2.0.0);";
   @Instance
   private static NoTreePunching instance;
   private static Logger log;
   private static boolean isSignedBuild = true;

   public NoTreePunching() {
   }

   public static NoTreePunching getInstance() {
      return instance;
   }

   public static Logger getLog() {
      return log;
   }

   @EventHandler
   public void preInit(FMLPreInitializationEvent event) {
      log = event.getModLog();
      log.debug("If you can see this, debug logging is working :)");
      if (!isSignedBuild) {
         log.warn("You are not running an official build. This version will NOT be supported by the author.");
      }

      NetworkRegistry.INSTANCE.registerGuiHandler(this, new ModGuiHandler());
      CapabilityPlayerItem.preInit();
      ModBlocks.preInit();
      ModItems.preInit();
      ModSounds.preInit();
   }

   @EventHandler
   public void init(FMLInitializationEvent event) {
      if (!isSignedBuild) {
         log.warn("You are not running an official build. This version will NOT be supported by the author.");
      }

      GameRegistry.registerWorldGenerator(new WorldGenRocks(), 3);
      ModRecipes.init();
      ModItems.init();
   }

   @EventHandler
   public void postInit(FMLPostInitializationEvent event) {
      if (!isSignedBuild) {
         log.warn("You are not running an official build. This version will NOT be supported by the author.");
      }

      HarvestBlockHandler.postInit();
      if (ModConfig.GENERAL.enableAdvancedRecipeReplacement) {
         WoodRecipeHandler.postInit();
      }

      ModRecipes.postInit();
   }

   @EventHandler
   public void onFingerprintViolation(FMLFingerprintViolationEvent event) {
      isSignedBuild = false;
      FMLCommonHandler.instance().registerCrashCallable(new ICrashCallable() {
         public String getLabel() {
            return "No Tree Punching";
         }

         public String call() {
            return "You are not running an official build. This version will NOT be supported by the author.";
         }
      });
   }
}
