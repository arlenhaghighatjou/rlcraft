package com.dhanantry.scapeandrunparasites;

import com.dhanantry.scapeandrunparasites.world.SRPWorldData;
import com.dhanantry.scapeandrunparasites.world.SRPSaveData;
import net.minecraft.world.storage.MapStorage;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.event.FMLConstructionEvent;
import net.minecraftforge.fml.common.event.FMLEvent;
import com.dhanantry.scapeandrunparasites.proxy.CommonProxy;
import com.dhanantry.scapeandrunparasites.util.SRPCreativeTabs;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.lax1dude.eaglercraft.v1_8.log4j.Logger;

@Mod(
   modid = "srparasites",
   name = "Scape and Run Parasites",
   version = "1.9.11",
   acceptedMinecraftVersions = "[1.12.2]"
)
public class SRPMain {
   public static SRPMain instance;
   public static CommonProxy proxy;
   public static final SRPCreativeTabs SRP_CREATIVETAB = new SRPCreativeTabs("Scape & Run: Parasites");
   public static Logger logger;
   public static SimpleNetworkWrapper network;

   public SRPMain() {
   }

   public void handleModStateEvent(FMLEvent event) {
      if (event instanceof FMLConstructionEvent) {
         instance = this;
         proxy = FMLCommonHandler.instance().getSide() == Side.CLIENT ? new com.dhanantry.scapeandrunparasites.proxy.ClientProxy() : new com.dhanantry.scapeandrunparasites.proxy.CommonProxy();
         com.dhanantry.scapeandrunparasites.init.SRPBiomes.RegistrationHandler.register(MinecraftForge.EVENT_BUS);
         com.dhanantry.scapeandrunparasites.init.SRPBlocks.RegistrationHandler.register(MinecraftForge.EVENT_BUS);
         com.dhanantry.scapeandrunparasites.init.SRPEntities.RegistrationHandler.register(MinecraftForge.EVENT_BUS);
         com.dhanantry.scapeandrunparasites.init.SRPItems.RegistrationHandler.register(MinecraftForge.EVENT_BUS);
         com.dhanantry.scapeandrunparasites.init.SRPPotions.RegistrationHandler.register(MinecraftForge.EVENT_BUS);
         com.dhanantry.scapeandrunparasites.init.SRPSounds.RegistrationHandler.register(MinecraftForge.EVENT_BUS);
         com.dhanantry.scapeandrunparasites.init.SRPSpawning.DimensionHandler.register(MinecraftForge.EVENT_BUS);
         if (FMLCommonHandler.instance().getSide() == Side.CLIENT) {
            com.dhanantry.scapeandrunparasites.util.handlers.SRPRegisterModels.register(MinecraftForge.EVENT_BUS);
         }
      } else if (event instanceof FMLPreInitializationEvent) {
         SRPMain.PreInit((FMLPreInitializationEvent)event);
      } else if (event instanceof FMLInitializationEvent) {
         SRPMain.Init((FMLInitializationEvent)event);
      } else if (event instanceof FMLPostInitializationEvent) {
         SRPMain.Postinit((FMLPostInitializationEvent)event);
      } else if (event instanceof FMLServerStartingEvent) {
         SRPMain.Serverinit((FMLServerStartingEvent)event);
      }

   }

   public static void PreInit(FMLPreInitializationEvent event) {
      logger = event.getModLog();
      MapStorage.storageProviders.put(SRPSaveData.class, SRPSaveData::new);
      MapStorage.storageProviders.put(SRPWorldData.class, SRPWorldData::new);
      proxy.preInit(event);
   }

   public static void Init(FMLInitializationEvent event) {
      proxy.init(event);
   }

   public static void Postinit(FMLPostInitializationEvent event) {
      proxy.postInit(event);
   }

   public static void Serverinit(FMLServerStartingEvent event) {
      proxy.serverInit(event);
   }

   static {
      FluidRegistry.enableUniversalBucket();
   }
}
