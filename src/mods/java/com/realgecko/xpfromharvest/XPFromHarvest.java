package com.realgecko.xpfromharvest;

import net.minecraftforge.fml.common.event.FMLConstructionEvent;
import net.minecraftforge.fml.common.event.FMLEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

@Mod(
   modid = "xpfromharvest",
   name = "XP From Harvest",
   version = "1.2.0",
   acceptableRemoteVersions = "*"
)
public class XPFromHarvest {
   public static final String MODID = "xpfromharvest";
   public static final String NAME = "XP From Harvest";
   public static final String VERSION = "1.2.0";
   private BlockBreakHandler blockBreakHandler;
   private SimpleHarvestHandler simpleHarvestHandler;
   private CuriosityHandler curiosityHandler;
   public static XPFromHarvest instance;

   public XPFromHarvest() {
   }

   public void handleModStateEvent(FMLEvent event) {
      if (event instanceof FMLConstructionEvent) {
         instance = this;
         com.realgecko.xpfromharvest.ModConfig.register();
         com.realgecko.xpfromharvest.ModConfig.EventHandler.register(MinecraftForge.EVENT_BUS);
      } else if (event instanceof FMLPreInitializationEvent) {
         this.preInit((FMLPreInitializationEvent)event);
      }

   }

   public void preInit(FMLPreInitializationEvent event) {
      this.blockBreakHandler = new BlockBreakHandler();
      this.simpleHarvestHandler = new SimpleHarvestHandler();
      this.curiosityHandler = new CuriosityHandler();
      this.blockBreakHandler.register(MinecraftForge.EVENT_BUS);
      if (ModConfig.simpleHarvest) {
         this.simpleHarvestHandler.register(MinecraftForge.EVENT_BUS);
      }

      if (ModConfig.curiosity) {
         this.curiosityHandler.register(MinecraftForge.EVENT_BUS);
      }

   }

   public void ConfigUpdated() {
      if (ModConfig.simpleHarvest) {
         this.simpleHarvestHandler.register(MinecraftForge.EVENT_BUS);
      } else {
         MinecraftForge.EVENT_BUS.unregister(this.simpleHarvestHandler);
      }

      if (ModConfig.curiosity) {
         this.curiosityHandler.register(MinecraftForge.EVENT_BUS);
      } else {
         MinecraftForge.EVENT_BUS.unregister(this.curiosityHandler);
      }

   }
}
