package com.dhanantry.scapeandrunparasites;

import com.dhanantry.scapeandrunparasites.proxy.CommonProxy;
import com.dhanantry.scapeandrunparasites.util.SRPCreativeTabs;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.Mod.Instance;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import org.apache.logging.log4j.Logger;

@Mod(
   modid = "srparasites",
   name = "Scape and Run Parasites",
   version = "1.9.11",
   acceptedMinecraftVersions = "[1.12.2]"
)
public class SRPMain {
   @Instance
   public static SRPMain instance;
   @SidedProxy(
      clientSide = "com.dhanantry.scapeandrunparasites.proxy.ClientProxy",
      serverSide = "com.dhanantry.scapeandrunparasites.proxy.CommonProxy"
   )
   public static CommonProxy proxy;
   public static final SRPCreativeTabs SRP_CREATIVETAB = new SRPCreativeTabs("Scape & Run: Parasites");
   public static Logger logger;
   public static SimpleNetworkWrapper network;

   public SRPMain() {
   }

   @EventHandler
   public static void PreInit(FMLPreInitializationEvent event) {
      logger = event.getModLog();
      proxy.preInit(event);
   }

   @EventHandler
   public static void Init(FMLInitializationEvent event) {
      proxy.init(event);
   }

   @EventHandler
   public static void Postinit(FMLPostInitializationEvent event) {
      proxy.postInit(event);
   }

   @EventHandler
   public static void Serverinit(FMLServerStartingEvent event) {
      proxy.serverInit(event);
   }

   static {
      FluidRegistry.enableUniversalBucket();
   }
}
