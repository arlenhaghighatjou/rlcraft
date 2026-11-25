package nonapi.io.github.classgraph.classloaderhandler;

import java.io.File;
import java.lang.reflect.Array;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import nonapi.io.github.classgraph.classpath.ClassLoaderOrder;
import nonapi.io.github.classgraph.classpath.ClasspathOrder;
import nonapi.io.github.classgraph.scanspec.ScanSpec;
import nonapi.io.github.classgraph.utils.FileUtils;
import nonapi.io.github.classgraph.utils.LogNode;
import nonapi.io.github.classgraph.utils.ReflectionUtils;

class JBossClassLoaderHandler implements ClassLoaderHandler {
   private JBossClassLoaderHandler() {
   }

   public static boolean canHandle(Class<?> classLoaderClass, LogNode log) {
      return "org.jboss.modules.ModuleClassLoader".equals(classLoaderClass.getName());
   }

   public static void findClassLoaderOrder(ClassLoader classLoader, ClassLoaderOrder classLoaderOrder, LogNode log) {
      classLoaderOrder.delegateTo(classLoader.getParent(), true, log);
      classLoaderOrder.add(classLoader, log);
   }

   private static void handleResourceLoader(Object resourceLoader, ClassLoader classLoader, ClasspathOrder classpathOrderOut, ScanSpec scanSpec, LogNode log) {
      if (resourceLoader != null) {
         Object root = ReflectionUtils.getFieldVal(resourceLoader, "root", false);
         File physicalFile = (File)ReflectionUtils.invokeMethod(root, "getPhysicalFile", false);
         String path = null;
         if (physicalFile != null) {
            String name = (String)ReflectionUtils.invokeMethod(root, "getName", false);
            if (name != null) {
               File file = new File(physicalFile.getParentFile(), name);
               if (FileUtils.canRead(file)) {
                  path = file.getAbsolutePath();
               } else {
                  path = physicalFile.getAbsolutePath();
               }
            } else {
               path = physicalFile.getAbsolutePath();
            }
         } else {
            path = (String)ReflectionUtils.invokeMethod(root, "getPathName", false);
            if (path == null) {
               File file = root instanceof Path ? ((Path)root).toFile() : (root instanceof File ? (File)root : null);
               if (file != null) {
                  path = file.getAbsolutePath();
               }
            }
         }

         if (path == null) {
            File file = (File)ReflectionUtils.getFieldVal(resourceLoader, "fileOfJar", false);
            if (file != null) {
               path = file.getAbsolutePath();
            }
         }

         if (path != null) {
            classpathOrderOut.addClasspathEntry(path, classLoader, scanSpec, log);
         } else if (log != null) {
            log.log("Could not determine classpath for ResourceLoader: " + resourceLoader);
         }

      }
   }

   private static void handleRealModule(Object module, Set<Object> visitedModules, ClassLoader classLoader, ClasspathOrder classpathOrderOut, ScanSpec scanSpec, LogNode log) {
      if (visitedModules.add(module)) {
         ClassLoader moduleLoader = (ClassLoader)ReflectionUtils.invokeMethod(module, "getClassLoader", false);
         if (moduleLoader == null) {
            moduleLoader = classLoader;
         }

         Object vfsResourceLoaders = ReflectionUtils.invokeMethod(moduleLoader, "getResourceLoaders", false);
         if (vfsResourceLoaders != null) {
            int i = 0;

            for(int n = Array.getLength(vfsResourceLoaders); i < n; ++i) {
               Object resourceLoader = Array.get(vfsResourceLoaders, i);
               handleResourceLoader(resourceLoader, moduleLoader, classpathOrderOut, scanSpec, log);
            }
         }

      }
   }

   public static void findClasspathOrder(ClassLoader classLoader, ClasspathOrder classpathOrder, ScanSpec scanSpec, LogNode log) {
      Object module = ReflectionUtils.invokeMethod(classLoader, "getModule", false);
      Object callerModuleLoader = ReflectionUtils.invokeMethod(module, "getCallerModuleLoader", false);
      Set<Object> visitedModules = new HashSet();
      Map<Object, Object> moduleMap = (Map)ReflectionUtils.getFieldVal(callerModuleLoader, "moduleMap", false);

      for(Entry<Object, Object> ent : moduleMap.entrySet()) {
         Object val = ent.getValue();
         Object realModule = ReflectionUtils.invokeMethod(val, "getModule", false);
         handleRealModule(realModule, visitedModules, classLoader, classpathOrder, scanSpec, log);
      }

      Map<String, List<?>> pathsMap = (Map)ReflectionUtils.invokeMethod(module, "getPaths", false);

      for(Entry<String, List<?>> ent : pathsMap.entrySet()) {
         for(Object localLoader : (List)ent.getValue()) {
            Object moduleClassLoader = ReflectionUtils.getFieldVal(localLoader, "this$0", false);
            Object realModule = ReflectionUtils.getFieldVal(moduleClassLoader, "module", false);
            handleRealModule(realModule, visitedModules, classLoader, classpathOrder, scanSpec, log);
         }
      }

   }
}
