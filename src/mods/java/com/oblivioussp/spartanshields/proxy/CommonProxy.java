package com.oblivioussp.spartanshields.proxy;

import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

public abstract class CommonProxy {
   public CommonProxy() {
   }

   public abstract void preInit(FMLPreInitializationEvent var1);

   public abstract void init(FMLInitializationEvent var1);

   public abstract void postInit(FMLPostInitializationEvent var1);
}
