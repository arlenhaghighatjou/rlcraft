package com.mcwbridges.kikoz;

import com.mcwbridges.kikoz.proxy.CommonProxy;
import com.mcwbridges.kikoz.tabs.MacawBridgesTab;
import com.mcwbridges.kikoz.util.handlers.RegistryHandler;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.Mod.Instance;
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
   @Instance
   public static Mcwbridges instance;
   @SidedProxy(
      clientSide = "com.mcwbridges.kikoz.proxy.ClientProxy",
      serverSide = "com.mcwbridges.kikoz.proxy.CommonProxy"
   )
   public static CommonProxy proxy;
   public static final CreativeTabs MACAWBRIDGESTAB = new MacawBridgesTab("macawbridgestab");

   public Mcwbridges() {
   }

   @EventHandler
   public void preInit(FMLPreInitializationEvent event) {
      RegistryHandler.preInitRegistries();
   }

   @EventHandler
   public void Init(FMLInitializationEvent event) {
      RegistryHandler.initRegistries();
   }

   @EventHandler
   public void postInit(FMLPostInitializationEvent event) {
      RegistryHandler.postInitRegistries();
   }

   @EventHandler
   public void serverInit(FMLServerStartingEvent event) {
      RegistryHandler.serverRegistries(event);
   }
}
