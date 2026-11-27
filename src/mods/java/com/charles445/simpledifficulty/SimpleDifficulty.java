package com.charles445.simpledifficulty;

import net.lax1dude.eaglercraft.v1_8.internal.vfs2.VFile2;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.event.FMLConstructionEvent;
import net.minecraftforge.fml.common.event.FMLEvent;
import com.charles445.simpledifficulty.command.CommandSimpleDifficulty;
import com.charles445.simpledifficulty.debug.DebugVerifier;
import com.charles445.simpledifficulty.network.PacketHandler;
import com.charles445.simpledifficulty.proxy.IProxy;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLLoadCompleteEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import net.lax1dude.eaglercraft.v1_8.log4j.LogManager;
import net.lax1dude.eaglercraft.v1_8.log4j.Logger;

@Mod(
   modid = "simpledifficulty",
   name = "SimpleDifficulty",
   version = "0.3.9",
   acceptedMinecraftVersions = "[1.12, 1.13)",
   dependencies = "required-after:forge@[14.23.5.2859,);",
   updateJSON = "https://raw.githubusercontent.com/Charles445/SimpleDifficulty/master/modupdatechecker.json"
)
public class SimpleDifficulty {
   public static final String MODID = "simpledifficulty";
   public static final String NAME = "SimpleDifficulty";
   public static final String VERSION = "0.3.9";
   public static SimpleDifficulty instance;
   public static Logger logger = LogManager.getLogger("SimpleDifficulty");
   public static IProxy proxy;
   public static VFile2 jsonDirectory;

   public SimpleDifficulty() {
   }

   public void handleModStateEvent(FMLEvent event) {
      if (event instanceof FMLConstructionEvent) {
         instance = this;
         proxy = FMLCommonHandler.instance().getSide() == Side.CLIENT ? new com.charles445.simpledifficulty.proxy.ClientProxy() : new com.charles445.simpledifficulty.proxy.ServerProxy();
         com.charles445.simpledifficulty.config.ModConfig.register();
         com.charles445.simpledifficulty.api.SDCapabilities.registerCapabilities();
         net.minecraftforge.common.crafting.CraftingHelper.register(new net.minecraft.util.ResourceLocation("simpledifficulty", "canteen_charcoal_recipe"), new com.charles445.simpledifficulty.register.crafting.CanteenCharcoalRecipe.Factory());
         com.charles445.simpledifficulty.config.ModConfig.EventHandler.register(MinecraftForge.EVENT_BUS);
         com.charles445.simpledifficulty.register.RegisterBlocks.Registrar.register(MinecraftForge.EVENT_BUS);
         if (FMLCommonHandler.instance().getSide() == Side.CLIENT) {
            com.charles445.simpledifficulty.register.RegisterClientModels.register(MinecraftForge.EVENT_BUS);
         }
         com.charles445.simpledifficulty.register.RegisterEnchantments.Registrar.register(MinecraftForge.EVENT_BUS);
         com.charles445.simpledifficulty.register.RegisterFluids.Registrar.register(MinecraftForge.EVENT_BUS);
         com.charles445.simpledifficulty.register.RegisterItems.Registrar.register(MinecraftForge.EVENT_BUS);
         com.charles445.simpledifficulty.register.RegisterPotions.Registrar.register(MinecraftForge.EVENT_BUS);
         com.charles445.simpledifficulty.register.RegisterRecipes.Registrar.register(MinecraftForge.EVENT_BUS);
      } else if (event instanceof FMLPreInitializationEvent) {
         this.preInit((FMLPreInitializationEvent)event);
      } else if (event instanceof FMLInitializationEvent) {
         this.init((FMLInitializationEvent)event);
      } else if (event instanceof FMLPostInitializationEvent) {
         this.postInit((FMLPostInitializationEvent)event);
      } else if (event instanceof FMLLoadCompleteEvent) {
         this.loadComplete((FMLLoadCompleteEvent)event);
      } else if (event instanceof FMLServerStartingEvent) {
         this.serverStarting((FMLServerStartingEvent)event);
      }

   }

   public void preInit(FMLPreInitializationEvent event) {
      jsonDirectory = new VFile2(event.getModConfigurationDirectory(), "simpledifficulty");
      PacketHandler.init();
      proxy.preInit();
   }

   public void init(FMLInitializationEvent event) {
      proxy.init();
   }

   public void postInit(FMLPostInitializationEvent event) {
      proxy.postInit();
   }

   public void loadComplete(FMLLoadCompleteEvent event) {
      DebugVerifier verifier = new DebugVerifier();
      verifier.verify();
   }

   public void serverStarting(FMLServerStartingEvent event) {
      event.registerServerCommand(new CommandSimpleDifficulty());
   }

   static {
      FluidRegistry.enableUniversalBucket();
   }
}
