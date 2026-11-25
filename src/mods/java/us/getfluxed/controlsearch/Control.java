package us.getfluxed.controlsearch;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.event.FMLConstructionEvent;
import net.minecraftforge.fml.common.event.FMLEvent;
import net.minecraftforge.fml.common.Mod;
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
   public static CommonProxy PROXY;

   public Control() {
   }

   public void handleModStateEvent(FMLEvent event) {
      if (event instanceof FMLConstructionEvent) {
         PROXY = FMLCommonHandler.instance().getSide() == Side.CLIENT ? new us.getfluxed.controlsearch.proxy.ClientProxy() : new us.getfluxed.controlsearch.proxy.CommonProxy();
      } else if (event instanceof FMLPreInitializationEvent) {
         this.preInit((FMLPreInitializationEvent)event);
      } else if (event instanceof FMLInitializationEvent) {
         this.init((FMLInitializationEvent)event);
      } else if (event instanceof FMLPostInitializationEvent) {
         this.postInit((FMLPostInitializationEvent)event);
      }

   }

   public void preInit(FMLPreInitializationEvent e) {
      PROXY.registerEvents();
   }

   public void init(FMLInitializationEvent e) {
   }

   public void postInit(FMLPostInitializationEvent e) {
   }
}
