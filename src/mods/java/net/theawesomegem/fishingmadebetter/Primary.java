package net.theawesomegem.fishingmadebetter;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.Mod.Instance;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import net.theawesomegem.fishingmadebetter.proxy.CommonProxy;

@Mod(
   modid = "fishingmadebetter",
   version = "2.2.6",
   name = "Fishing Made Better",
   dependencies = "after:advanced-fishing;after:aquaculture;after:levelup2"
)
public class Primary {
   @Instance("fishingmadebetter")
   public static Primary Instance;
   @SidedProxy(
      clientSide = "net.theawesomegem.fishingmadebetter.proxy.ClientProxy",
      serverSide = "net.theawesomegem.fishingmadebetter.proxy.CommonProxy"
   )
   public static CommonProxy proxy;

   public Primary() {
   }

   @EventHandler
   public void onPreInit(FMLPreInitializationEvent e) {
      proxy.onPreInit(e);
   }

   @EventHandler
   public void onInit(FMLInitializationEvent e) {
      proxy.onInit(e);
   }

   @EventHandler
   public void onPostInit(FMLPostInitializationEvent e) {
      proxy.onPostInit(e);
   }

   @EventHandler
   public void onServerStarting(FMLServerStartingEvent e) {
      proxy.onServerStarting(e);
   }
}
