package vazkii.quark.base.module;

import java.io.File;
import java.lang.management.ManagementFactory;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Predicate;
import net.minecraft.item.Item;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.event.RegistryEvent.Register;
import net.minecraftforge.fml.client.event.ConfigChangedEvent.OnConfigChangedEvent;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import vazkii.quark.api.module.ModuleLoadedEvent;
import vazkii.quark.automation.QuarkAutomation;
import vazkii.quark.base.Quark;
import vazkii.quark.base.handler.RecipeProcessor;
import vazkii.quark.building.QuarkBuilding;
import vazkii.quark.client.QuarkClient;
import vazkii.quark.decoration.QuarkDecoration;
import vazkii.quark.experimental.QuarkExperimental;
import vazkii.quark.management.QuarkManagement;
import vazkii.quark.misc.QuarkMisc;
import vazkii.quark.oddities.QuarkOddities;
import vazkii.quark.tweaks.QuarkTweaks;
import vazkii.quark.vanity.QuarkVanity;
import vazkii.quark.world.QuarkWorld;

public final class ModuleLoader {
   public static final boolean DEBUG_MODE = ManagementFactory.getRuntimeMXBean().getInputArguments().toString().contains("-agentlib:jdwp");
   private static List<Class<? extends Module>> moduleClasses;
   public static final Map<Class<? extends Module>, Module> moduleInstances = new HashMap();
   public static final Map<Class<? extends Feature>, Feature> featureInstances = new HashMap();
   public static final Map<String, Feature> featureClassnames = new HashMap();
   public static List<Module> enabledModules;
   public static final List<Runnable> lazyOreDictRegisters = new ArrayList();
   public static Configuration config;
   public static File configFile;
   public static boolean firstLoad;

   public ModuleLoader() {
   }

   private static void setupModuleClasses() {
      moduleClasses = new ArrayList();
      registerModule(QuarkTweaks.class);
      registerModule(QuarkWorld.class);
      registerModule(QuarkVanity.class);
      registerModule(QuarkDecoration.class);
      registerModule(QuarkBuilding.class);
      registerModule(QuarkAutomation.class);
      registerModule(QuarkManagement.class);
      registerModule(QuarkClient.class);
      registerModule(QuarkMisc.class);
      if (Loader.isModLoaded("quarkoddities")) {
         registerModule(QuarkOddities.class);
      }

      registerModule(QuarkExperimental.class);
   }

   public static void preInit(FMLPreInitializationEvent event) {
      setupModuleClasses();
      moduleClasses.forEach((clazz) -> {
         try {
            Module instance = (Module)clazz.newInstance();
            if (!MinecraftForge.EVENT_BUS.post(new ModuleLoadedEvent(instance))) {
               moduleInstances.put(clazz, instance);
            }

         } catch (Exception var2) {
            throw new RuntimeException("Can't initialize module " + clazz, var2);
         }
      });
      setupConfig(event);
      forEachModule((module) -> {
         Quark.LOG.info("Module " + module.name + " is " + (module.enabled ? "enabled" : "disabled"));
      });
      forEachEnabled((module) -> {
         module.preInit(event);
      });
      forEachEnabled(Module::postPreInit);
      RecipeProcessor.runConsumers();
   }

   public static void init(FMLInitializationEvent event) {
      forEachEnabled(Module::init);
   }

   public static void postInit(FMLPostInitializationEvent event) {
      forEachEnabled(Module::postInit);
   }

   public static void finalInit(FMLPostInitializationEvent event) {
      forEachEnabled(Module::finalInit);
   }

   @SideOnly(Side.CLIENT)
   public static void preInitClient(FMLPreInitializationEvent event) {
      forEachEnabled(Module::preInitClient);
   }

   @SideOnly(Side.CLIENT)
   public static void initClient(FMLInitializationEvent event) {
      forEachEnabled(Module::initClient);
   }

   @SideOnly(Side.CLIENT)
   public static void postInitClient(FMLPostInitializationEvent event) {
      forEachEnabled(Module::postInitClient);
   }

   public static void serverStarting(FMLServerStartingEvent event) {
      forEachEnabled(Module::serverStarting);
   }

   public static void setupConfig(FMLPreInitializationEvent event) {
      configFile = event.getSuggestedConfigurationFile();
      if (!configFile.exists()) {
         firstLoad = true;
      }

      config = new Configuration(configFile);
      config.load();
      loadConfig();
   }

   public static void loadConfig() {
      GlobalConfig.initGlobalConfig();
      forEachModule((module) -> {
         module.enabled = true;
         if (module.canBeDisabled()) {
            ConfigHelper.needsRestart = true;
            module.enabled = ConfigHelper.loadPropBool(module.name, "_modules", module.getModuleDescription(), module.isEnabledByDefault());
            module.prop = ConfigHelper.lastProp;
         }

      });
      enabledModules = new ArrayList(moduleInstances.values());
      enabledModules.removeIf((module) -> {
         return !module.enabled;
      });
      loadModuleConfigs();
      if (config.hasChanged()) {
         config.save();
      }

   }

   private static void loadModuleConfigs() {
      forEachModule(Module::setupConfig);
   }

   public static boolean isModuleEnabled(Class<? extends Module> clazz) {
      Module module = (Module)moduleInstances.get(clazz);
      return module != null && module.enabled;
   }

   public static boolean isFeatureEnabled(Class<? extends Feature> clazz) {
      Feature feature = (Feature)featureInstances.get(clazz);
      return feature != null && feature.enabled;
   }

   public static void forEachModule(Consumer<Module> consumer) {
      moduleInstances.values().forEach(consumer);
   }

   public static void forEachEnabled(Consumer<Module> consumer) {
      enabledModules.forEach(consumer);
   }

   private static void registerModule(Class<? extends Module> clazz) {
      if (!moduleClasses.contains(clazz)) {
         moduleClasses.add(clazz);
      }

   }

   @EventBusSubscriber(
      modid = "quark"
   )
   public static class EventHandler {
      public EventHandler() {
      }

      @SubscribeEvent
      public static void onConfigChanged(OnConfigChangedEvent eventArgs) {
         if (eventArgs.getModID().equals("quark")) {
            ModuleLoader.loadConfig();
         }

      }

      @SubscribeEvent(
         priority = EventPriority.LOWEST
      )
      public static void onRegistered(Register<Item> event) {
         ModuleLoader.lazyOreDictRegisters.forEach(Runnable::run);
      }
   }
}
