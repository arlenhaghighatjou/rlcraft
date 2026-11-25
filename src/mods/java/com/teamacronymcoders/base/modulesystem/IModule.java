package com.teamacronymcoders.base.modulesystem;

import com.teamacronymcoders.base.IBaseMod;
import com.teamacronymcoders.base.modulesystem.dependencies.IDependency;
import com.teamacronymcoders.base.modulesystem.proxies.IModuleProxy;
import java.util.List;
import java.util.Optional;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

public interface IModule extends Comparable<IModule> {
   String getName();

   List<IDependency> getDependencies(List<IDependency> var1);

   void preInit(FMLPreInitializationEvent var1);

   void afterModulesPreInit(FMLPreInitializationEvent var1);

   void init(FMLInitializationEvent var1);

   void postInit(FMLPostInitializationEvent var1);

   @Nullable
   String getClientProxyPath();

   @Nullable
   String getServerProxyPath();

   @Nonnull
   Optional<IModuleProxy> getModuleProxy();

   void setModuleProxy(IModuleProxy var1);

   boolean getIsActive();

   void setIsActive(Boolean var1);

   boolean getActiveDefault();

   boolean isConfigurable();

   IBaseMod getMod();

   void setMod(@Nonnull IBaseMod var1);

   void setModuleHandler(@Nonnull ModuleHandler var1);
}
