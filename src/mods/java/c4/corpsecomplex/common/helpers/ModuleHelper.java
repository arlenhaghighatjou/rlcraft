package c4.corpsecomplex.common.helpers;

import c4.corpsecomplex.CorpseComplex;
import c4.corpsecomplex.common.Module;
import c4.corpsecomplex.common.Submodule;
import c4.corpsecomplex.common.modules.ExperienceModule;
import c4.corpsecomplex.common.modules.HungerModule;
import c4.corpsecomplex.common.modules.compatibility.toughasnails.TANModule;
import c4.corpsecomplex.common.modules.effects.EffectsModule;
import c4.corpsecomplex.common.modules.inventory.InventoryModule;
import c4.corpsecomplex.common.modules.spawning.SpawningModule;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.client.event.ConfigChangedEvent.OnConfigChangedEvent;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.apache.logging.log4j.Level;

public final class ModuleHelper {
   public static Configuration cfg;
   private static ArrayList<Class<? extends Module>> moduleClasses = new ArrayList();
   private static Map<Class<? extends Module>, Module> instances = new HashMap();

   public ModuleHelper() {
   }

   public static void preInit(FMLPreInitializationEvent e) {
      moduleClasses.forEach((module) -> {
         try {
            instances.put(module, module.getConstructor().newInstance());
         } catch (Exception var2) {
            CorpseComplex.logger.log(Level.ERROR, "Failed to initialize module " + module, var2);
         }

      });
      forEachModule(Module::loadSubmodules);
      initConfig(e);
   }

   private static void initConfig(FMLPreInitializationEvent e) {
      File directory = e.getModConfigurationDirectory();
      cfg = new Configuration(new File(directory.getPath(), "corpsecomplex.cfg"));

      try {
         cfg.load();
         forEachModule((module) -> {
            registerEvents(module);
            module.setPropOrder();
         });
      } catch (Exception var6) {
         CorpseComplex.logger.log(Level.ERROR, "Problem loading config file!", var6);
      } finally {
         if (cfg.hasChanged()) {
            cfg.save();
         }

      }

   }

   private static void reloadConfigs() {
      forEachModule(ModuleHelper::registerEvents);
      if (cfg.hasChanged()) {
         cfg.save();
      }

   }

   private static void registerEvents(Module module) {
      module.loadModuleConfig();
      registerModuleEvents(module);
      module.forEachSubmodule((submodule) -> {
         submodule.loadModuleConfig();
         registerSubmoduleEvents(submodule);
      });
   }

   private static void registerModuleEvents(Module module) {
      module.setEnabled();
      if (!module.enabled && module.prevEnabled && module.hasEvents()) {
         MinecraftForge.EVENT_BUS.unregister(module);
      } else if (module.enabled && !module.prevEnabled && module.hasEvents()) {
         MinecraftForge.EVENT_BUS.register(module);
      }

      module.prevEnabled = module.enabled;
   }

   private static void registerSubmoduleEvents(Submodule submodule) {
      submodule.setEnabled();
      if (!submodule.enabled && submodule.prevEnabled && submodule.hasEvents()) {
         MinecraftForge.EVENT_BUS.unregister(submodule);
      } else if (submodule.enabled && !submodule.prevEnabled && submodule.hasEvents()) {
         MinecraftForge.EVENT_BUS.register(submodule);
      }

      submodule.prevEnabled = submodule.enabled;
   }

   private static void addModule(Class<? extends Module> module) {
      if (!moduleClasses.contains(module)) {
         moduleClasses.add(module);
      }

   }

   private static void addModule(String modid, Class<? extends Module> module) {
      if (Loader.isModLoaded(modid) && !moduleClasses.contains(module)) {
         moduleClasses.add(module);
      }

   }

   private static void forEachModule(Consumer<Module> module) {
      instances.values().forEach(module);
   }

   static {
      addModule(EffectsModule.class);
      addModule(ExperienceModule.class);
      addModule(InventoryModule.class);
      addModule(HungerModule.class);
      addModule(SpawningModule.class);
      addModule("toughasnails", TANModule.class);
   }

   @EventBusSubscriber
   private static class ConfigChangeHandler {
      private ConfigChangeHandler() {
      }

      @SubscribeEvent
      public static void onConfigChanged(OnConfigChangedEvent e) {
         if (e.getModID().equals("corpsecomplex")) {
            ModuleHelper.reloadConfigs();
         }

      }
   }
}
