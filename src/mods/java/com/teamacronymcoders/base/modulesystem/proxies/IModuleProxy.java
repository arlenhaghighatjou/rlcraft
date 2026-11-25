package com.teamacronymcoders.base.modulesystem.proxies;

import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

public interface IModuleProxy {
   void preInit(FMLPreInitializationEvent var1);

   void init(FMLInitializationEvent var1);

   void postInit(FMLPostInitializationEvent var1);
}
