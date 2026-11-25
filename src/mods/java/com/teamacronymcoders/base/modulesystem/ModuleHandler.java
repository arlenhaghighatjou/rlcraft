package com.teamacronymcoders.base.modulesystem;

import com.teamacronymcoders.base.IBaseMod;
import com.teamacronymcoders.base.modulesystem.dependencies.IDependency;
import com.teamacronymcoders.base.registrysystem.IRegistryHolder;
import com.teamacronymcoders.base.registrysystem.config.ConfigEntry;
import com.teamacronymcoders.base.registrysystem.config.ConfigRegistry;
import com.teamacronymcoders.base.util.ClassLoading;
import com.teamacronymcoders.base.util.collections.MapUtils;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import javax.annotation.Nonnull;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Property.Type;
import net.minecraftforge.fml.common.discovery.ASMDataTable;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class ModuleHandler {
   private Map<String, IModule> modules;
   private IRegistryHolder registryHolder;
   private IBaseMod mod;
   private String handlerName;

   public ModuleHandler(IBaseMod mod, ASMDataTable asmDataTable) {
      this(mod.getID(), mod, asmDataTable);
   }

   public ModuleHandler(String handlerName, IBaseMod mod, ASMDataTable asmDataTable) {
      this.mod = mod;
      this.handlerName = handlerName;
      this.registryHolder = mod.getRegistryHolder();
      this.modules = MapUtils.<String, IModule>sortByValue(this.loadModules(asmDataTable));
      mod.getLibProxy().loadEntityRenderers(asmDataTable, this);
   }

   public void preInit(FMLPreInitializationEvent event) {
      this.modules.values().stream().filter(IModule::getIsActive).forEachOrdered((module) -> {
         module.preInit(event);
      });
      this.modules.values().stream().filter(IModule::getIsActive).forEachOrdered((module) -> {
         module.afterModulesPreInit(event);
      });
   }

   public void init(FMLInitializationEvent event) {
      this.modules.values().stream().filter(IModule::getIsActive).forEachOrdered((module) -> {
         module.init(event);
      });
   }

   public void postInit(FMLPostInitializationEvent event) {
      this.modules.values().stream().filter(IModule::getIsActive).forEachOrdered((module) -> {
         module.postInit(event);
      });
   }

   public void setupModules() {
      for(IModule module : this.getModules().values()) {
         if (module.isConfigurable() && this.mod.hasConfig()) {
            String configCategory = this.handlerName.equals(this.mod.getID()) ? "Module" : this.handlerName;
            this.getConfig().addEntry(module.getName(), new ModuleHandler.ModuleConfigEntry(configCategory, module));
            module.setIsActive(Boolean.valueOf(this.getConfig().getBoolean(module.getName(), module.getActiveDefault())));
         }

         module.setMod(this.mod);
         module.setModuleHandler(this);
      }

      this.modules.values().stream().filter(IModule::getIsActive).forEach(this::checkDependencies);
      this.modules.values().stream().filter(IModule::getIsActive).forEach(this::setProxy);
   }

   private void checkDependencies(IModule module) {
      module.getDependencies(new ArrayList()).forEach((dependency) -> {
         this.printModuleDependencyOutcome(module, dependency);
      });
      if (module.getIsActive()) {
         this.mod.getLogger().info("Module " + module.getName() + " has successfully loaded");
      }

   }

   private void printModuleDependencyOutcome(IModule module, IDependency dependency) {
      if (!dependency.isMet(this)) {
         if (dependency.isSilent() || dependency.notMetMessage() != null) {
            this.mod.getLogger().error("Module " + module.getName() + " did not load due to issue: " + dependency.notMetMessage());
         }

         module.setIsActive(Boolean.valueOf(false));
      }

   }

   private void setProxy(IModule module) {
      module.setModuleProxy(this.mod.getLibProxy().getModuleProxy(module));
   }

   public Map<String, IModule> getModules() {
      return this.modules;
   }

   public IModule getModule(String name) {
      return (IModule)this.modules.get(name);
   }

   public boolean isModuleEnabled(String name) {
      return this.getModule(name) != null && this.getModule(name).getIsActive();
   }

   private ConfigRegistry getConfig() {
      return (ConfigRegistry)this.registryHolder.getRegistry(ConfigRegistry.class, "CONFIG");
   }

   private Map<String, IModule> loadModules(@Nonnull ASMDataTable asmDataTable) {
      Map<String, IModule> moduleMap = new HashMap();
      ClassLoading.getInstances(asmDataTable, Module.class, IModule.class, (aClass) -> {
         Module moduleAnnotation = (Module)aClass.getAnnotation(Module.class);
         boolean load = false;
         if (moduleAnnotation != null) {
            String modid = moduleAnnotation.value().trim();
            load = modid.isEmpty() || modid.equalsIgnoreCase(this.getName());
            load = load & this.mod.getLibProxy().isRightSide(moduleAnnotation.side());
         }

         return load;
      }).forEach((module) -> {
         if (!moduleMap.containsKey(module.getName())) {
            moduleMap.put(module.getName(), module);
            Method[] methods = module.getClass().getMethods();

            for(Method method : methods) {
               if (method.getDeclaredAnnotation(SubscribeEvent.class) != null) {
                  MinecraftForge.EVENT_BUS.register(module);
               }
            }

         } else {
            throw new UnsupportedOperationException("Module Names must be Unique");
         }
      });
      return moduleMap;
   }

   public String getName() {
      return this.handlerName;
   }

   private static class ModuleConfigEntry extends ConfigEntry {
      public ModuleConfigEntry(String handlerName, IModule module) {
         super(handlerName, module.getName() + " enabled", Type.BOOLEAN, module.getActiveDefault() + "");
      }
   }
}
