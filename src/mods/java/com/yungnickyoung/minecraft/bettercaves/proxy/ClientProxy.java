package com.yungnickyoung.minecraft.bettercaves.proxy;

import com.yungnickyoung.minecraft.bettercaves.event.EventConfigReload;
import net.minecraftforge.common.MinecraftForge;

public class ClientProxy implements IProxy {
   public ClientProxy() {
   }

   public void preInit() {
      (new EventConfigReload()).register(MinecraftForge.EVENT_BUS);
   }
}
