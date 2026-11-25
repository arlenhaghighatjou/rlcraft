package com.bloodnbonesgaming.lib.proxy;

import net.minecraftforge.fml.common.event.FMLServerStartingEvent;

public abstract class CommonProxy {
   public CommonProxy() {
   }

   public abstract void registerCommands(FMLServerStartingEvent var1);

   public abstract boolean client();

   public abstract void registerEventHandlers();
}
