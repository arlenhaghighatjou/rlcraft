package nonapi.io.github.classgraph.classloaderhandler;

import java.io.File;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import nonapi.io.github.classgraph.classpath.ClassLoaderOrder;
import nonapi.io.github.classgraph.classpath.ClasspathOrder;
import nonapi.io.github.classgraph.scanspec.ScanSpec;
import nonapi.io.github.classgraph.utils.LogNode;
import nonapi.io.github.classgraph.utils.ReflectionUtils;

class FelixClassLoaderHandler implements ClassLoaderHandler {
   private FelixClassLoaderHandler() {
   }

   public static boolean canHandle(Class<?> classLoaderClass, LogNode log) {
      return "org.apache.felix.framework.BundleWiringImpl$BundleClassLoaderJava5".equals(classLoaderClass.getName()) || "org.apache.felix.framework.BundleWiringImpl$BundleClassLoader".equals(classLoaderClass.getName());
   }

   public static void findClassLoaderOrder(ClassLoader classLoader, ClassLoaderOrder classLoaderOrder, LogNode log) {
      classLoaderOrder.delegateTo(classLoader.getParent(), true, log);
      classLoaderOrder.add(classLoader, log);
   }

   private static String getContentLocation(Object content) {
      File file = (File)ReflectionUtils.invokeMethod(content, "getFile", false);
      return file != null ? file.toURI().toString() : null;
   }

   private static void addBundle(Object bundleWiring, ClassLoader classLoader, ClasspathOrder classpathOrderOut, Set<Object> bundles, ScanSpec scanSpec, LogNode log) {
      bundles.add(bundleWiring);
      Object revision = ReflectionUtils.invokeMethod(bundleWiring, "getRevision", false);
      Object content = ReflectionUtils.invokeMethod(revision, "getContent", false);
      String location = content != null ? getContentLocation(content) : null;
      if (location != null) {
         classpathOrderOut.addClasspathEntry(location, classLoader, scanSpec, log);
         List<?> embeddedContent = (List)ReflectionUtils.invokeMethod(revision, "getContentPath", false);
         if (embeddedContent != null) {
            for(Object embedded : embeddedContent) {
               if (embedded != content) {
                  String embeddedLocation = embedded != null ? getContentLocation(embedded) : null;
                  if (embeddedLocation != null) {
                     classpathOrderOut.addClasspathEntry(embeddedLocation, classLoader, scanSpec, log);
                  }
               }
            }
         }
      }

   }

   public static void findClasspathOrder(ClassLoader classLoader, ClasspathOrder classpathOrder, ScanSpec scanSpec, LogNode log) {
      Set<Object> bundles = new HashSet();
      Object bundleWiring = ReflectionUtils.getFieldVal(classLoader, "m_wiring", false);
      addBundle(bundleWiring, classLoader, classpathOrder, bundles, scanSpec, log);
      List<?> requiredWires = (List)ReflectionUtils.invokeMethod(bundleWiring, "getRequiredWires", String.class, (Object)null, false);
      if (requiredWires != null) {
         for(Object wire : requiredWires) {
            Object provider = ReflectionUtils.invokeMethod(wire, "getProviderWiring", false);
            if (!bundles.contains(provider)) {
               addBundle(provider, classLoader, classpathOrder, bundles, scanSpec, log);
            }
         }
      }

   }
}
