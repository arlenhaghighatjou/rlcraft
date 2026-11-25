package io.github.lxgaming.mixin.launch;

import cpw.mods.modlauncher.LaunchPluginHandler;
import cpw.mods.modlauncher.Launcher;
import cpw.mods.modlauncher.api.IEnvironment;
import cpw.mods.modlauncher.api.ITransformationService;
import cpw.mods.modlauncher.api.ITransformer;
import cpw.mods.modlauncher.api.IncompatibleEnvironmentException;
import cpw.mods.modlauncher.api.IEnvironment.Keys;
import cpw.mods.modlauncher.api.ITransformationService.OptionResult;
import cpw.mods.modlauncher.api.TypesafeMap.Key;
import cpw.mods.modlauncher.serviceapi.ILaunchPluginService;
import java.lang.reflect.Field;
import java.net.URL;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.Map.Entry;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;
import joptsimple.OptionSpecBuilder;

public class MixinTransformationService implements ITransformationService {
   private final Map<String, ILaunchPluginService> launchPluginServices;
   private final Set<ITransformationService> transformationServices;

   public MixinTransformationService() {
      if (Launcher.INSTANCE == null) {
         throw new IllegalStateException("Launcher has not been initialized!");
      } else {
         this.launchPluginServices = this.getLaunchPluginServices();
         this.transformationServices = new HashSet();
      }
   }

   public String name() {
      return "mixinbootstrap";
   }

   public void initialize(IEnvironment environment) {
      MixinBootstrap.initialize(environment);

      for(ITransformationService transformationService : this.transformationServices) {
         transformationService.initialize(environment);
      }

   }

   public void beginScanning(IEnvironment environment) {
      for(ITransformationService transformationService : this.transformationServices) {
         transformationService.beginScanning(environment);
      }

   }

   public void onLoad(IEnvironment env, Set<String> otherServices) throws IncompatibleEnvironmentException {
      MixinBootstrap.onLoad(env, this);

      for(ITransformationService transformationService : this.transformationServices) {
         transformationService.onLoad(env, otherServices);
      }

   }

   public List<ITransformer> transformers() {
      List<ITransformer> list = new ArrayList();

      for(ITransformationService transformationService : this.transformationServices) {
         list.addAll(transformationService.transformers());
      }

      return list;
   }

   public void arguments(BiFunction<String, String, OptionSpecBuilder> argumentBuilder) {
      for(ITransformationService transformationService : this.transformationServices) {
         transformationService.arguments(argumentBuilder);
      }

   }

   public void argumentValues(OptionResult option) {
      for(ITransformationService transformationService : this.transformationServices) {
         transformationService.argumentValues(option);
      }

   }

   public List<Entry<String, Path>> runScan(IEnvironment environment) {
      List<Entry<String, Path>> list = new ArrayList();

      for(ITransformationService transformationService : this.transformationServices) {
         list.addAll(transformationService.runScan(environment));
      }

      return list;
   }

   public Entry<Set<String>, Supplier<Function<String, Optional<URL>>>> additionalClassesLocator() {
      return null;
   }

   public Entry<Set<String>, Supplier<Function<String, Optional<URL>>>> additionalResourcesLocator() {
      return null;
   }

   private Map<String, ILaunchPluginService> getLaunchPluginServices() {
      try {
         Field launchPluginsField = Launcher.class.getDeclaredField("launchPlugins");
         launchPluginsField.setAccessible(true);
         LaunchPluginHandler launchPluginHandler = (LaunchPluginHandler)launchPluginsField.get(Launcher.INSTANCE);
         Field pluginsField = LaunchPluginHandler.class.getDeclaredField("plugins");
         pluginsField.setAccessible(true);
         return (Map)pluginsField.get(launchPluginHandler);
      } catch (Exception var4) {
         MixinBootstrap.LOGGER.error("Encountered an error while getting LaunchPluginServices", var4);
         return null;
      }
   }

   public void registerLaunchPluginService(String className, ClassLoader classLoader) throws IncompatibleEnvironmentException {
      try {
         Class<? extends ILaunchPluginService> launchPluginServiceClass = Class.forName(className, true, classLoader);
         if (this.isLaunchPluginServicePresent(launchPluginServiceClass)) {
            MixinBootstrap.LOGGER.warn("{} is already registered", launchPluginServiceClass.getSimpleName());
         } else {
            ILaunchPluginService launchPluginService = (ILaunchPluginService)launchPluginServiceClass.newInstance();
            String pluginName = launchPluginService.name();
            this.launchPluginServices.put(pluginName, launchPluginService);
            List<Map<String, String>> mods = (List)Launcher.INSTANCE.environment().getProperty((Key)Keys.MODLIST.get()).orElse((Object)null);
            if (mods != null) {
               Map<String, String> mod = new HashMap();
               mod.put("name", pluginName);
               mod.put("type", "PLUGINSERVICE");
               String fileName = launchPluginServiceClass.getProtectionDomain().getCodeSource().getLocation().getFile();
               mod.put("file", fileName.substring(fileName.lastIndexOf(47)));
               mods.add(mod);
            }

            MixinBootstrap.LOGGER.debug("Registered {} ({})", launchPluginServiceClass.getSimpleName(), pluginName);
         }
      } catch (Throwable var9) {
         MixinBootstrap.LOGGER.error("Encountered an error while registering {}", className, var9);
         throw new IncompatibleEnvironmentException(String.format("Failed to register %s", className));
      }
   }

   public void registerTransformationService(String className, ClassLoader classLoader) throws IncompatibleEnvironmentException {
      try {
         Class<? extends ITransformationService> transformationServiceClass = Class.forName(className, true, classLoader);
         if (this.isTransformationServicePresent(transformationServiceClass)) {
            MixinBootstrap.LOGGER.warn("{} is already registered", transformationServiceClass.getSimpleName());
         } else {
            ITransformationService transformationService = (ITransformationService)transformationServiceClass.newInstance();
            String name = transformationService.name();
            this.transformationServices.add(transformationService);
            MixinBootstrap.LOGGER.debug("Registered {} ({})", transformationServiceClass.getSimpleName(), name);
         }
      } catch (Exception var6) {
         MixinBootstrap.LOGGER.error("Encountered an error while registering {}", className, var6);
         throw new IncompatibleEnvironmentException(String.format("Failed to register %s", className));
      }
   }

   private boolean isLaunchPluginServicePresent(Class<? extends ILaunchPluginService> launchPluginServiceClass) {
      for(ILaunchPluginService launchPluginService : this.launchPluginServices.values()) {
         if (launchPluginServiceClass.isInstance(launchPluginService)) {
            return true;
         }
      }

      return false;
   }

   private boolean isTransformationServicePresent(Class<? extends ITransformationService> transformationServiceClass) {
      for(ITransformationService transformationService : this.transformationServices) {
         if (transformationServiceClass.isInstance(transformationService)) {
            return true;
         }
      }

      return false;
   }
}
