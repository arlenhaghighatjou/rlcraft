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
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLConstructionEvent;
import net.minecraftforge.fml.common.event.FMLEvent;
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
   acceptedMinecraftVersions = "[1.12.2]"
)
public final class NoTreePunching {
   public static final String MOD_ID = "notreepunching";
   public static final String MOD_NAME = "No Tree Punching";
   public static final String VERSION = "2.0.21";
   public static final String DEPENDENCIES = "required-after:forge@[14.23.4.2705,15.0.0.0);required-after:alcatrazcore@[1.0.0,2.0.0);";
   private static NoTreePunching instance;
   private static Logger log;

   public NoTreePunching() {
   }

   public static NoTreePunching getInstance() {
      return instance;
   }

   public static Logger getLog() {
      return log;
   }

   public void handleModStateEvent(FMLEvent event) {
      if (event instanceof FMLConstructionEvent) {
         instance = this;
         ModConfig.register();
         ModEventHandler.register(MinecraftForge.EVENT_BUS);
      } else if (event instanceof FMLPreInitializationEvent) {
         preInit((FMLPreInitializationEvent)event);
      } else if (event instanceof FMLInitializationEvent) {
         init((FMLInitializationEvent)event);
      } else if (event instanceof FMLPostInitializationEvent) {
         postInit((FMLPostInitializationEvent)event);
      }
   }

   public void preInit(FMLPreInitializationEvent event) {
      log = event.getModLog();
      log.debug("If you can see this, debug logging is working :)");

      NetworkRegistry.INSTANCE.registerGuiHandler(this, new ModGuiHandler());
      CapabilityPlayerItem.preInit();
      ModBlocks.preInit();
      ModItems.preInit();
      ModSounds.preInit();
   }

   public void init(FMLInitializationEvent event) {
      GameRegistry.registerWorldGenerator(new WorldGenRocks(), 3);
      ModRecipes.init();
      ModItems.init();
   }

   public void postInit(FMLPostInitializationEvent event) {
      HarvestBlockHandler.postInit();
      if (ModConfig.GENERAL.enableAdvancedRecipeReplacement) {
         WoodRecipeHandler.postInit();
      }

      ModRecipes.postInit();
   }

}
