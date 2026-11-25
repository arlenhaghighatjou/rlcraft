package com.chaosthedude.realistictorches;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.event.FMLConstructionEvent;
import net.minecraftforge.fml.common.event.FMLEvent;
import com.chaosthedude.realistictorches.blocks.RealisticTorchesBlocks;
import com.chaosthedude.realistictorches.config.ConfigHandler;
import com.chaosthedude.realistictorches.events.RealisticTorchesEvents;
import com.chaosthedude.realistictorches.handler.LightSourceHandler;
import com.chaosthedude.realistictorches.handler.RecipeHandler;
import com.chaosthedude.realistictorches.items.RealisticTorchesItems;
import com.chaosthedude.realistictorches.proxy.CommonProxy;
import com.chaosthedude.realistictorches.worldgen.TorchGenerator;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.lax1dude.eaglercraft.v1_8.log4j.LogManager;
import net.lax1dude.eaglercraft.v1_8.log4j.Logger;

@Mod(
   modid = "realistictorches",
   name = "Realistic Torches",
   version = "2.1.2",
   acceptedMinecraftVersions = "[1.12.2]"
)
public class RealisticTorches {
   public static final String MODID = "realistictorches";
   public static final String NAME = "Realistic Torches";
   public static final String VERSION = "2.1.2";
   public static final Logger logger = LogManager.getLogger("realistictorches");
   public static CommonProxy proxy;

   public RealisticTorches() {
   }

   public void handleModStateEvent(FMLEvent event) {
      if (event instanceof FMLConstructionEvent) {
         proxy = FMLCommonHandler.instance().getSide() == Side.CLIENT ? new com.chaosthedude.realistictorches.proxy.ClientProxy() : new com.chaosthedude.realistictorches.proxy.CommonProxy();
      } else if (event instanceof FMLPreInitializationEvent) {
         this.preInit((FMLPreInitializationEvent)event);
      } else if (event instanceof FMLInitializationEvent) {
         this.init((FMLInitializationEvent)event);
      } else if (event instanceof FMLPostInitializationEvent) {
         this.postInit((FMLPostInitializationEvent)event);
      }

   }

   public void preInit(FMLPreInitializationEvent event) {
      ConfigHandler.loadConfig(event.getSuggestedConfigurationFile());
      ConfigHandler.printConfigInfo();
      RealisticTorchesBlocks.register();
      RealisticTorchesItems.register();
      RecipeHandler.registerOres();
      proxy.registerModels();
      GameRegistry.registerWorldGenerator(new TorchGenerator(), 0);
   }

   public void init(FMLInitializationEvent event) {
      (new RealisticTorchesEvents()).register(MinecraftForge.EVENT_BUS);
   }

   public void postInit(FMLPostInitializationEvent event) {
      RecipeHandler.removeRecipes();
      LightSourceHandler.registerLightSources();
   }
}
