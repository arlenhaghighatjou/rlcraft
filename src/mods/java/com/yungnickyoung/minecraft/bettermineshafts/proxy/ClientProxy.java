package com.yungnickyoung.minecraft.bettermineshafts.proxy;

import com.yungnickyoung.minecraft.bettermineshafts.init.ModConfig;

public class ClientProxy implements IProxy {
   public ClientProxy() {
   }

   public void preInit() {
      ModConfig.preInit();
   }
}
