package net.silentchaos512.lib.proxy.internal;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.silentchaos512.lib.event.SilentLibClientEvents;

public final class SilentLibClientProxy extends SilentLibCommonProxy {
   public SilentLibClientProxy() {
   }

   public void preInit(FMLPreInitializationEvent event) {
      super.preInit(event);
      MinecraftForge.EVENT_BUS.register(new SilentLibClientEvents());
   }
}
