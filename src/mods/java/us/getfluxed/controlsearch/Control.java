package us.getfluxed.controlsearch;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import us.getfluxed.controlsearch.proxy.CommonProxy;

@Mod(
   modid = "controlling",
   name = "Controlling",
   version = "3.0.10",
   clientSideOnly = true,
   acceptedMinecraftVersions = "[1.12.2]"
)
public class Control {
   @SidedProxy(
      clientSide = "us.getfluxed.controlsearch.proxy.ClientProxy",
      serverSide = "us.getfluxed.controlsearch.proxy.CommonProxy"
   )
   public static CommonProxy PROXY;

   public Control() {
   }

   @EventHandler
   public void preInit(FMLPreInitializationEvent e) {
      PROXY.registerEvents();
   }

   @EventHandler
   public void init(FMLInitializationEvent e) {
   }

   @EventHandler
   public void postInit(FMLPostInitializationEvent e) {
   }
}
