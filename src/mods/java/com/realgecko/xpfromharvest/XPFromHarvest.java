package com.realgecko.xpfromharvest;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.Mod.Instance;
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
   @Instance("xpfromharvest")
   public static XPFromHarvest instance;

   public XPFromHarvest() {
   }

   @EventHandler
   public void preInit(FMLPreInitializationEvent event) {
      this.blockBreakHandler = new BlockBreakHandler();
      this.simpleHarvestHandler = new SimpleHarvestHandler();
      this.curiosityHandler = new CuriosityHandler();
      MinecraftForge.EVENT_BUS.register(this.blockBreakHandler);
      if (ModConfig.simpleHarvest) {
         MinecraftForge.EVENT_BUS.register(this.simpleHarvestHandler);
      }

      if (ModConfig.curiosity) {
         MinecraftForge.EVENT_BUS.register(this.curiosityHandler);
      }

   }

   public void ConfigUpdated() {
      if (ModConfig.simpleHarvest) {
         MinecraftForge.EVENT_BUS.register(this.simpleHarvestHandler);
      } else {
         MinecraftForge.EVENT_BUS.unregister(this.simpleHarvestHandler);
      }

      if (ModConfig.curiosity) {
         MinecraftForge.EVENT_BUS.register(this.curiosityHandler);
      } else {
         MinecraftForge.EVENT_BUS.unregister(this.curiosityHandler);
      }

   }
}
