package io.github.lxgaming.mixin.launch;

import cpw.mods.modlauncher.Launcher;
import cpw.mods.modlauncher.api.IEnvironment;
import cpw.mods.modlauncher.api.ITransformationService;
import cpw.mods.modlauncher.api.IncompatibleEnvironmentException;
import cpw.mods.modlauncher.api.IEnvironment.Keys;
import cpw.mods.modlauncher.api.TypesafeMap.Key;
import io.github.lxgaming.classloader.ClassLoaderUtils;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.URI;
import java.net.URL;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import net.lax1dude.eaglercraft.v1_8.log4j.LogManager;
import net.lax1dude.eaglercraft.v1_8.log4j.Logger;

public class MixinBootstrap {
   public static final String ID = "mixinbootstrap";
   public static final String NAME = "MixinBootstrap";
   public static final String VERSION = "1.1.0";
   public static final Logger LOGGER = LogManager.getLogger("MixinBootstrap Launch");

   public MixinBootstrap() {
   }

   public static void initialize(IEnvironment environment) {
      ensureTransformerExclusion();
   }

   public static void onLoad(IEnvironment environment, MixinTransformationService service) throws IncompatibleEnvironmentException {
      if (environment.findLaunchPlugin("mixin").isPresent()) {
         LOGGER.debug("MixinLaunchPlugin detected");
      } else if (IEnvironment.class.getPackage().isCompatibleWith("8.0")) {
         setFallbackClassLoader(service.getClass().getClassLoader());
         service.registerLaunchPluginService("org.spongepowered.asm.launch.MixinLaunchPlugin", MixinBootstrap.class.getClassLoader());
         service.registerTransformationService("org.spongepowered.asm.launch.MixinTransformationService", MixinBootstrap.class.getClassLoader());
      } else if (IEnvironment.class.getPackage().isCompatibleWith("4.0")) {
         appendToClassPath();
         service.registerLaunchPluginService("org.spongepowered.asm.launch.MixinLaunchPluginLegacy", Launcher.class.getClassLoader());
         service.registerTransformationService("org.spongepowered.asm.launch.MixinTransformationServiceLegacy", Thread.currentThread().getContextClassLoader());
         service.registerLaunchPluginService("io.github.lxgaming.mixin.launch.MixinLaunchPluginService", Launcher.class.getClassLoader());
      } else {
         LOGGER.error("-------------------------[ ERROR ]-------------------------");
         LOGGER.error("Mixin is not compatible with ModLauncher v{}", ITransformationService.class.getPackage().getImplementationVersion());
         LOGGER.error("Ensure you are running Forge v28.1.45 or later");
         LOGGER.error("-------------------------[ ERROR ]-------------------------");
         throw new IncompatibleEnvironmentException("Incompatibility with ModLauncher");
      }
   }

   private static void appendToClassPath() throws IncompatibleEnvironmentException {
      try {
         Map<String, String> map = new HashMap();
         map.put("create", "true");
         FileSystem fileSystem = FileSystems.newFileSystem(URI.create("jar:" + MixinBootstrap.class.getProtectionDomain().getCodeSource().getLocation().toURI()), map);
         Map<Path, String> libraries = (Map)Files.list(fileSystem.getPath("META-INF", "libraries")).filter((path) -> {
            return !Files.isDirectory(path, new LinkOption[0]) && path.getFileName().toString().endsWith(".jar");
         }).collect(Collectors.toMap((path) -> {
            return path;
         }, (path) -> {
            String fileName = path.getFileName().toString();
            int index = fileName.lastIndexOf(46);
            return index != -1 ? fileName.substring(0, index) : fileName;
         }));
         LOGGER.debug("Found {} libraries", Integer.valueOf(libraries.size()));

         for(Entry<Path, String> entry : libraries.entrySet()) {
            Path temporaryPath = Files.createTempFile("mixinbootstrap-" + (String)entry.getValue() + "-", ".jar");
            LOGGER.debug("Copying {} -> {}", entry.getKey(), temporaryPath);
            Files.copy((Path)entry.getKey(), temporaryPath, StandardCopyOption.REPLACE_EXISTING);
            URL url = temporaryPath.toUri().toURL();
            LOGGER.debug("Loading {}", url);
            ClassLoaderUtils.appendToClassPath(Thread.currentThread().getContextClassLoader(), url);
         }

         URL url = MixinBootstrap.class.getProtectionDomain().getCodeSource().getLocation().toURI().toURL();
         LOGGER.debug("Loading {}", url);
         ClassLoaderUtils.appendToClassPath(Thread.currentThread().getContextClassLoader(), url);
      } catch (Throwable var7) {
         LOGGER.error("Encountered an error while appending to the class path", var7);
         throw new IncompatibleEnvironmentException("Failed to append to the class path");
      }
   }

   private static void setFallbackClassLoader(ClassLoader classLoader) throws IncompatibleEnvironmentException {
      try {
         Class<?> moduleClassLoaderClass = Class.forName("cpw.mods.cl.ModuleClassLoader");
         ClassLoader contextClassLoader = Thread.currentThread().getContextClassLoader();
         if (moduleClassLoaderClass.isInstance(contextClassLoader) && !classLoader.equals(contextClassLoader)) {
            Method getPlatformClassLoaderMethod = ClassLoader.class.getMethod("getPlatformClassLoader");
            getPlatformClassLoaderMethod.setAccessible(true);
            ClassLoader platformClassLoader = (ClassLoader)getPlatformClassLoaderMethod.invoke((Object)null);
            Method setFallbackClassLoaderMethod = moduleClassLoaderClass.getMethod("setFallbackClassLoader", ClassLoader.class);
            setFallbackClassLoaderMethod.setAccessible(true);
            setFallbackClassLoaderMethod.invoke(contextClassLoader, new MixinClassLoader(platformClassLoader, classLoader));
         } else {
            throw new IllegalStateException("Unexpected ClassLoader: " + contextClassLoader.getClass().getName());
         }
      } catch (Throwable var6) {
         LOGGER.error("Encountered an error while setting fallback classloader", var6);
         throw new IncompatibleEnvironmentException("Failed to set fallback classloader");
      }
   }

   private static void ensureTransformerExclusion() {
      try {
         Path path = (Path)Launcher.INSTANCE.environment().getProperty((Key)Keys.GAMEDIR.get()).map((parentPath) -> {
            return parentPath.resolve("mods");
         }).map(Path::toAbsolutePath).map(Path::normalize).map((parentPath) -> {
            String file = MixinBootstrap.class.getProtectionDomain().getCodeSource().getLocation().getFile();
            return parentPath.resolve(file.substring(file.lastIndexOf(47) + 1));
         }).filter((x$0) -> {
            return Files.exists(x$0, new LinkOption[0]);
         }).orElse((Object)null);
         if (path == null) {
            return;
         }

         if (path.equals(path.toRealPath())) {
            return;
         }

         Class<?> modDirTransformerDiscovererClass = Class.forName("net.minecraftforge.fml.loading.ModDirTransformerDiscoverer", true, Launcher.class.getClassLoader());
         Field transformersField = modDirTransformerDiscovererClass.getDeclaredField("transformers");
         transformersField.setAccessible(true);
         List<Path> transformers = (List)transformersField.get((Object)null);
         if (transformers != null && !transformers.contains(path)) {
            transformers.add(path);
         }
      } catch (Throwable var4) {
         LOGGER.error("Encountered an error while getting ensuring transformer exclusion", var4);
      }

   }

   static {
      LOGGER.info("{} v{}", "MixinBootstrap", "1.1.0");
      LOGGER.info("Mixin v{}", "0.8.4");
      LOGGER.info("ModLauncher v{} ({})", IEnvironment.class.getPackage().getImplementationVersion(), IEnvironment.class.getPackage().getSpecificationVersion());
   }
}
