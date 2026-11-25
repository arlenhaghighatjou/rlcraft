package com.tmtravlr.soundfilters;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.FMLCommonHandler;

public class ClientProxy extends CommonProxy {
   public ClientProxy() {
   }

   public void registerTickHandlers() {
      FMLCommonHandler.instance().bus().register(new SoundTickHandler());
   }

   public void registerEventHandlers() {
      MinecraftForge.EVENT_BUS.register(new SoundEventHandler());
   }
}
