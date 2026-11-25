package com.mcwbridges.kikoz;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.event.FMLConstructionEvent;
import net.minecraftforge.fml.common.event.FMLEvent;
import com.mcwbridges.kikoz.proxy.CommonProxy;
import com.mcwbridges.kikoz.tabs.MacawBridgesTab;
import com.mcwbridges.kikoz.util.handlers.RegistryHandler;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;

@Mod(
   modid = "mcwbridges",
   version = "1.0.6",
   name = "Mcwbridges"
)
public class Mcwbridges {
   public static Mcwbridges instance;
   public static CommonProxy proxy;
   public static final CreativeTabs MACAWBRIDGESTAB = new MacawBridgesTab("macawbridgestab");

   public Mcwbridges() {
   }

   public void handleModStateEvent(FMLEvent event) {
      if (event instanceof FMLConstructionEvent) {
         instance = this;
         proxy = FMLCommonHandler.instance().getSide() == Side.CLIENT ? new com.mcwbridges.kikoz.proxy.ClientProxy() : new com.mcwbridges.kikoz.proxy.CommonProxy();
         com.mcwbridges.kikoz.util.handlers.RegistryHandler.register(MinecraftForge.EVENT_BUS);
      } else if (event instanceof FMLPreInitializationEvent) {
         this.preInit((FMLPreInitializationEvent)event);
      } else if (event instanceof FMLInitializationEvent) {
         this.Init((FMLInitializationEvent)event);
      } else if (event instanceof FMLPostInitializationEvent) {
         this.postInit((FMLPostInitializationEvent)event);
      } else if (event instanceof FMLServerStartingEvent) {
         this.serverInit((FMLServerStartingEvent)event);
      }

   }

   public void preInit(FMLPreInitializationEvent event) {
      RegistryHandler.preInitRegistries();
   }

   public void Init(FMLInitializationEvent event) {
      RegistryHandler.initRegistries();
   }

   public void postInit(FMLPostInitializationEvent event) {
      RegistryHandler.postInitRegistries();
   }

   public void serverInit(FMLServerStartingEvent event) {
      RegistryHandler.serverRegistries(event);
   }
}
