package com.ferreusveritas.dynamictrees;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.event.FMLConstructionEvent;
import net.minecraftforge.fml.common.event.FMLEvent;
import com.ferreusveritas.dynamictrees.command.DTCommand;
import com.ferreusveritas.dynamictrees.compat.CompatHandler;
import com.ferreusveritas.dynamictrees.proxy.CommonProxy;
import com.ferreusveritas.dynamictrees.seasons.SeasonHelper;
import com.ferreusveritas.dynamictrees.worldgen.WorldGeneratorTrees;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import net.minecraftforge.fml.common.event.FMLServerStoppedEvent;
import net.lax1dude.eaglercraft.v1_8.log4j.Logger;

@Mod(
   modid = "dynamictrees",
   name = "Dynamic Trees",
   version = "1.12.2-0.9.29",
   dependencies = "required-after:forge@[14.23.5.2859,);before:rustic@(1.0.14,);before:dynamictreesbop@[1.12.2-1.4.3,);before:dynamictreestc@[1.12.2-1.4.2,);before:@[1.12.2-2.0.3,)@[1.12.2-2.0.3,);before:dttraverse@[1.12.2-2.0,);before:dynamictreeshnc@(1.1,);after:reccomplex",
   updateJSON = "https://github.com/DynamicTreesTeam/DynamicTreesVersionInfo/blob/master/DynamicTrees.json?raw=true",
   acceptedMinecraftVersions = "[1.12.2]"
)
public class DynamicTrees {
   public static DynamicTrees instance;
   public static CommonProxy proxy;
   public static Logger log;

   public DynamicTrees() {
   }

   public void handleModStateEvent(FMLEvent event) {
      if (event instanceof FMLConstructionEvent) {
         instance = this;
         proxy = FMLCommonHandler.instance().getSide() == Side.CLIENT ? new com.ferreusveritas.dynamictrees.proxy.ClientProxy() : new com.ferreusveritas.dynamictrees.proxy.CommonProxy();
         com.ferreusveritas.dynamictrees.ModBlocks.register(MinecraftForge.EVENT_BUS);
         com.ferreusveritas.dynamictrees.ModEntities.register(MinecraftForge.EVENT_BUS);
         com.ferreusveritas.dynamictrees.ModItems.register(MinecraftForge.EVENT_BUS);
         com.ferreusveritas.dynamictrees.ModModels.register(MinecraftForge.EVENT_BUS);
         com.ferreusveritas.dynamictrees.ModRecipes.register(MinecraftForge.EVENT_BUS);
         com.ferreusveritas.dynamictrees.ModTrees.register(MinecraftForge.EVENT_BUS);
      } else if (event instanceof FMLPreInitializationEvent) {
         this.preInit((FMLPreInitializationEvent)event);
      } else if (event instanceof FMLInitializationEvent) {
         this.init((FMLInitializationEvent)event);
      } else if (event instanceof FMLPostInitializationEvent) {
         this.postInit((FMLPostInitializationEvent)event);
      } else if (event instanceof FMLServerStartingEvent) {
         DynamicTrees.serverStarting((FMLServerStartingEvent)event);
      } else if (event instanceof FMLServerStoppedEvent) {
         DynamicTrees.serverStopped((FMLServerStoppedEvent)event);
      }

   }

   public void preInit(FMLPreInitializationEvent event) {
      log = event.getModLog();
      proxy.preInit(event);
      CompatHandler.preInit();
   }

   public void init(FMLInitializationEvent event) {
      proxy.init(event);
   }

   public void postInit(FMLPostInitializationEvent event) {
      proxy.postInit();
      proxy.cleanUp();
   }

   public static void serverStarting(FMLServerStartingEvent event) {
      event.registerServerCommand(new DTCommand());
      SeasonHelper.getSeasonManager().flushMappings();
   }

   public static void serverStopped(FMLServerStoppedEvent event) {
      WorldGeneratorTrees.clearFlatWorldCache();
   }
}
