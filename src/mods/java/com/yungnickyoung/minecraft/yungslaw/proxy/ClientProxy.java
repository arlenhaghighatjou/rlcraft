package com.yungnickyoung.minecraft.yungslaw.proxy;

import com.yungnickyoung.minecraft.yungslaw.event.EventConfig;
import net.minecraftforge.common.MinecraftForge;

public class ClientProxy extends Proxy {
   public ClientProxy() {
   }

   public void preInit() {
      super.preInit();
      MinecraftForge.EVENT_BUS.register(new EventConfig());
   }
}
