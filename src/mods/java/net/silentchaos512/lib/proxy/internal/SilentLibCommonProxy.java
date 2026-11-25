package net.silentchaos512.lib.proxy.internal;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.silentchaos512.lib.event.SilentLibCommonEvents;

public class SilentLibCommonProxy {
   public SilentLibCommonProxy() {
   }

   public void preInit(FMLPreInitializationEvent event) {
      MinecraftForge.EVENT_BUS.register(new SilentLibCommonEvents());
   }

   public void init(FMLInitializationEvent event) {
   }

   public void postInit(FMLPostInitializationEvent event) {
   }
}
