package nonapi.io.github.classgraph.classloaderhandler;

import java.util.SortedSet;
import nonapi.io.github.classgraph.classpath.ClassLoaderOrder;
import nonapi.io.github.classgraph.classpath.ClasspathOrder;
import nonapi.io.github.classgraph.scanspec.ScanSpec;
import nonapi.io.github.classgraph.utils.LogNode;
import nonapi.io.github.classgraph.utils.ReflectionUtils;

class PlexusClassWorldsClassRealmClassLoaderHandler implements ClassLoaderHandler {
   private PlexusClassWorldsClassRealmClassLoaderHandler() {
   }

   public static boolean canHandle(Class<?> classLoaderClass, LogNode log) {
      return "org.codehaus.plexus.classworlds.realm.ClassRealm".equals(classLoaderClass.getName());
   }

   private static boolean isParentFirstStrategy(ClassLoader classRealmInstance) {
      Object strategy = ReflectionUtils.getFieldVal(classRealmInstance, "strategy", false);
      if (strategy != null) {
         String strategyClassName = strategy.getClass().getName();
         if (strategyClassName.equals("org.codehaus.plexus.classworlds.strategy.SelfFirstStrategy") || strategyClassName.equals("org.codehaus.plexus.classworlds.strategy.OsgiBundleStrategy")) {
            return false;
         }
      }

      return true;
   }

   public static void findClassLoaderOrder(ClassLoader classRealm, ClassLoaderOrder classLoaderOrder, LogNode log) {
      Object foreignImports = ReflectionUtils.getFieldVal(classRealm, "foreignImports", false);
      if (foreignImports != null) {
         for(Object entry : (SortedSet)foreignImports) {
            ClassLoader foreignImportClassLoader = (ClassLoader)ReflectionUtils.invokeMethod(entry, "getClassLoader", false);
            classLoaderOrder.delegateTo(foreignImportClassLoader, true, log);
         }
      }

      boolean isParentFirst = isParentFirstStrategy(classRealm);
      if (!isParentFirst) {
         classLoaderOrder.add(classRealm, log);
      }

      ClassLoader parentClassLoader = (ClassLoader)ReflectionUtils.invokeMethod(classRealm, "getParentClassLoader", false);
      classLoaderOrder.delegateTo(parentClassLoader, true, log);
      classLoaderOrder.delegateTo(classRealm.getParent(), true, log);
      if (isParentFirst) {
         classLoaderOrder.add(classRealm, log);
      }

   }

   public static void findClasspathOrder(ClassLoader classLoader, ClasspathOrder classpathOrder, ScanSpec scanSpec, LogNode log) {
      URLClassLoaderHandler.findClasspathOrder(classLoader, classpathOrder, scanSpec, log);
   }
}
