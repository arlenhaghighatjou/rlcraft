package de.impelon.disenchanter.proxy;

import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

public class ServerProxy extends CommonProxy {
   public ServerProxy() {
   }

   public void preInit(FMLPreInitializationEvent ev) {
      super.preInit(ev);
   }

   public void load(FMLInitializationEvent ev) {
      super.load(ev);
   }

   public void postInit(FMLPostInitializationEvent ev) {
      super.postInit(ev);
   }
}
