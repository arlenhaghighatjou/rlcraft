package com.yungnickyoung.minecraft.bettercaves;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.event.FMLConstructionEvent;
import net.minecraftforge.fml.common.event.FMLEvent;
import com.yungnickyoung.minecraft.bettercaves.event.EventBetterCaveGen;
import com.yungnickyoung.minecraft.bettercaves.proxy.IProxy;
import net.lax1dude.eaglercraft.v1_8.internal.vfs2.VFile2;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.lax1dude.eaglercraft.v1_8.log4j.LogManager;
import net.lax1dude.eaglercraft.v1_8.log4j.Logger;

@Mod(
   modid = "bettercaves",
   name = "YUNG's Better Caves",
   version = "1.12.2",
   useMetadata = true,
   acceptableRemoteVersions = "*",
   acceptedMinecraftVersions = "[1.12.2]"
)
public class BetterCaves {
   public static final Logger LOGGER = LogManager.getLogger("bettercaves");
   public static VFile2 customConfigDir;
   public static IProxy proxy;

   public BetterCaves() {
   }

   public void handleModStateEvent(FMLEvent event) {
      if (event instanceof FMLConstructionEvent) {
         proxy = FMLCommonHandler.instance().getSide() == Side.CLIENT ? new com.yungnickyoung.minecraft.bettercaves.proxy.ClientProxy() : new com.yungnickyoung.minecraft.bettercaves.proxy.ServerProxy();
         com.yungnickyoung.minecraft.bettercaves.config.Configuration.register();
      } else if (event instanceof FMLPreInitializationEvent) {
         this.preInit((FMLPreInitializationEvent)event);
      } else if (event instanceof FMLInitializationEvent) {
         this.init((FMLInitializationEvent)event);
      }

   }

   public void preInit(FMLPreInitializationEvent event) {
      proxy.preInit();
      customConfigDir = new VFile2(Loader.instance().getConfigDir(), "bettercaves-1_12_2");

   }

   public void init(FMLInitializationEvent event) {
      (new EventBetterCaveGen()).register(MinecraftForge.TERRAIN_GEN_BUS);
   }
}
