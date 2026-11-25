package nonapi.io.github.classgraph.classpath;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.Map.Entry;
import nonapi.io.github.classgraph.classloaderhandler.ClassLoaderHandlerRegistry;
import nonapi.io.github.classgraph.scanspec.ScanSpec;
import nonapi.io.github.classgraph.utils.FastPathResolver;
import nonapi.io.github.classgraph.utils.FileUtils;
import nonapi.io.github.classgraph.utils.JarUtils;
import nonapi.io.github.classgraph.utils.LogNode;

public class ClasspathFinder {
   private final ClasspathOrder classpathOrder;
   private final ClassLoaderFinder classLoaderFinder;
   private ClassLoader[] classLoaderOrderRespectingParentDelegation;

   public ClasspathOrder getClasspathOrder() {
      return this.classpathOrder;
   }

   public Class<?>[] getCallStack() {
      return this.classLoaderFinder.getCallStack();
   }

   public ClassLoader[] getClassLoaderOrderRespectingParentDelegation() {
      return this.classLoaderOrderRespectingParentDelegation;
   }

   public ClasspathFinder(ScanSpec scanSpec, LogNode log) {
      LogNode classpathFinderLog = log == null ? null : log.log("Finding classpath and modules");
      String jreRtJar = SystemJarFinder.getJreRtJarPath();
      boolean scanAllLibOrExtJars = !scanSpec.libOrExtJarWhiteBlackList.whitelistAndBlacklistAreEmpty();
      this.classLoaderFinder = new ClassLoaderFinder(scanSpec, classpathFinderLog);
      this.classpathOrder = new ClasspathOrder(scanSpec);
      ClasspathOrder ignoredClasspathOrder = new ClasspathOrder(scanSpec);
      ClassLoader[] contextClassLoaders = this.classLoaderFinder.getContextClassLoaders();
      ClassLoader defaultClassLoader = contextClassLoaders != null && contextClassLoaders.length > 0 ? contextClassLoaders[0] : null;
      if (scanSpec.overrideClasspath != null) {
         if (scanSpec.overrideClassLoaders != null && classpathFinderLog != null) {
            classpathFinderLog.log("It is not possible to override both the classpath and the ClassLoaders -- ignoring the ClassLoader override");
         }

         LogNode overrideLog = classpathFinderLog == null ? null : classpathFinderLog.log("Overriding classpath with: " + scanSpec.overrideClasspath);
         this.classpathOrder.addClasspathEntries(scanSpec.overrideClasspath, defaultClassLoader, scanSpec, overrideLog);
         if (overrideLog != null) {
            overrideLog.log("WARNING: when the classpath is overridden, there is no guarantee that the classes found by classpath scanning will be the same as the classes loaded by the context classloader");
         }

         this.classLoaderOrderRespectingParentDelegation = contextClassLoaders;
      } else {
         LogNode systemJarsLog = classpathFinderLog == null ? null : classpathFinderLog.log("System jars:");
         if (jreRtJar != null) {
            if (scanSpec.enableSystemJarsAndModules) {
               this.classpathOrder.addSystemClasspathEntry(jreRtJar, defaultClassLoader);
               if (systemJarsLog != null) {
                  systemJarsLog.log("Found rt.jar: " + jreRtJar);
               }
            } else if (systemJarsLog != null) {
               systemJarsLog.log((scanSpec.enableSystemJarsAndModules ? "" : "Scanning disabled for rt.jar: ") + jreRtJar);
            }
         }

         for(String libOrExtJarPath : SystemJarFinder.getJreLibOrExtJars()) {
            if (!scanAllLibOrExtJars && !scanSpec.libOrExtJarWhiteBlackList.isSpecificallyWhitelistedAndNotBlacklisted(libOrExtJarPath)) {
               if (systemJarsLog != null) {
                  systemJarsLog.log("Scanning disabled for lib or ext jar: " + libOrExtJarPath);
               }
            } else {
               this.classpathOrder.addSystemClasspathEntry(libOrExtJarPath, defaultClassLoader);
               if (systemJarsLog != null) {
                  systemJarsLog.log("Found lib or ext jar: " + libOrExtJarPath);
               }
            }
         }

         if (classpathFinderLog != null) {
            LogNode classLoaderHandlerLog = classpathFinderLog.log("ClassLoaderHandlers:");

            for(ClassLoaderHandlerRegistry.ClassLoaderHandlerRegistryEntry classLoaderHandlerEntry : ClassLoaderHandlerRegistry.CLASS_LOADER_HANDLERS) {
               classLoaderHandlerLog.log(classLoaderHandlerEntry.classLoaderHandlerClass.getName());
            }
         }

         LogNode classloaderOrderLog = classpathFinderLog == null ? null : classpathFinderLog.log("Finding unique classloaders in delegation order");
         ClassLoaderOrder classLoaderOrder = new ClassLoaderOrder();
         ClassLoader[] origClassLoaderOrder = scanSpec.overrideClassLoaders != null ? (ClassLoader[])scanSpec.overrideClassLoaders.toArray(new ClassLoader[0]) : contextClassLoaders;
         if (origClassLoaderOrder != null) {
            for(ClassLoader classLoader : origClassLoaderOrder) {
               classLoaderOrder.delegateTo(classLoader, false, classloaderOrderLog);
            }
         }

         Set<ClassLoader> allParentClassLoaders = classLoaderOrder.getAllParentClassLoaders();
         LogNode classloaderURLLog = classpathFinderLog == null ? null : classpathFinderLog.log("Obtaining URLs from classloaders in delegation order");
         List<ClassLoader> finalClassLoaderOrder = new ArrayList();

         for(Entry<ClassLoader, ClassLoaderHandlerRegistry.ClassLoaderHandlerRegistryEntry> ent : classLoaderOrder.getClassLoaderOrder()) {
            ClassLoader classLoader = (ClassLoader)ent.getKey();
            ClassLoaderHandlerRegistry.ClassLoaderHandlerRegistryEntry classLoaderHandlerRegistryEntry = (ClassLoaderHandlerRegistry.ClassLoaderHandlerRegistryEntry)ent.getValue();
            if (scanSpec.ignoreParentClassLoaders && allParentClassLoaders.contains(classLoader)) {
               LogNode classloaderHandlerLog = classloaderURLLog == null ? null : classloaderURLLog.log("Ignoring parent classloader " + classLoader + ", normally handled by " + classLoaderHandlerRegistryEntry.classLoaderHandlerClass.getName());
               classLoaderHandlerRegistryEntry.findClasspathOrder(classLoader, ignoredClasspathOrder, scanSpec, classloaderHandlerLog);
            } else {
               LogNode classloaderHandlerLog = classloaderURLLog == null ? null : classloaderURLLog.log("Classloader " + classLoader + " is handled by " + classLoaderHandlerRegistryEntry.classLoaderHandlerClass.getName());
               classLoaderHandlerRegistryEntry.findClasspathOrder(classLoader, this.classpathOrder, scanSpec, classloaderHandlerLog);
               finalClassLoaderOrder.add(classLoader);
            }
         }

         this.classLoaderOrderRespectingParentDelegation = (ClassLoader[])finalClassLoaderOrder.toArray(new ClassLoader[0]);
         if (scanSpec.overrideClassLoaders == null && scanSpec.overrideClasspath == null) {
            String[] pathElements = JarUtils.smartPathSplit(System.getProperty("java.class.path"));
            if (pathElements.length > 0) {
               LogNode sysPropLog = classpathFinderLog == null ? null : classpathFinderLog.log("Getting classpath entries from java.class.path");

               for(String pathElement : pathElements) {
                  String pathElementResolved = FastPathResolver.resolve(FileUtils.CURR_DIR_PATH, pathElement);
                  if (!ignoredClasspathOrder.getClasspathEntryUniqueResolvedPaths().contains(pathElementResolved)) {
                     this.classpathOrder.addClasspathEntry(pathElement, defaultClassLoader, scanSpec, sysPropLog);
                  } else if (sysPropLog != null) {
                     sysPropLog.log("Found classpath element in java.class.path that will be ignored, since it is also found in an ignored parent classloader: " + pathElement);
                  }
               }
            }
         }
      }

   }
}
