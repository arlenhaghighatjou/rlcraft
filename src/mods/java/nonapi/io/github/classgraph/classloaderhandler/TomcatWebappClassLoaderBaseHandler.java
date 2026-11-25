package nonapi.io.github.classgraph.classloaderhandler;

import java.io.File;
import java.util.List;
import nonapi.io.github.classgraph.classpath.ClassLoaderOrder;
import nonapi.io.github.classgraph.classpath.ClasspathOrder;
import nonapi.io.github.classgraph.scanspec.ScanSpec;
import nonapi.io.github.classgraph.utils.LogNode;
import nonapi.io.github.classgraph.utils.ReflectionUtils;

class TomcatWebappClassLoaderBaseHandler implements ClassLoaderHandler {
   private TomcatWebappClassLoaderBaseHandler() {
   }

   public static boolean canHandle(Class<?> classLoaderClass, LogNode log) {
      return "org.apache.catalina.loader.WebappClassLoaderBase".equals(classLoaderClass.getName());
   }

   public static void findClassLoaderOrder(ClassLoader classLoader, ClassLoaderOrder classLoaderOrder, LogNode log) {
      classLoaderOrder.delegateTo(classLoader.getParent(), true, log);
      classLoaderOrder.add(classLoader, log);
   }

   public static void findClasspathOrder(ClassLoader classLoader, ClasspathOrder classpathOrder, ScanSpec scanSpec, LogNode log) {
      Object resources = ReflectionUtils.invokeMethod(classLoader, "getResources", false);
      Object baseURLs = ReflectionUtils.invokeMethod(resources, "getBaseUrls", false);
      classpathOrder.addClasspathEntryObject(baseURLs, classLoader, scanSpec, log);
      List<List<?>> allResources = (List)ReflectionUtils.getFieldVal(resources, "allResources", false);
      if (allResources != null) {
         for(List<?> webResourceSetList : allResources) {
            for(Object webResourceSet : webResourceSetList) {
               if (webResourceSet != null) {
                  File file = (File)ReflectionUtils.invokeMethod(webResourceSet, "getFileBase", false);
                  String base = file == null ? null : file.getPath();
                  if (base == null) {
                     base = (String)ReflectionUtils.invokeMethod(webResourceSet, "getBase", false);
                  }

                  if (base == null) {
                     base = (String)ReflectionUtils.invokeMethod(webResourceSet, "getBaseUrlString", false);
                  }

                  if (base != null) {
                     String archivePath = (String)ReflectionUtils.getFieldVal(webResourceSet, "archivePath", false);
                     if (archivePath != null && !archivePath.isEmpty()) {
                        base = base + "!" + (archivePath.startsWith("/") ? archivePath : "/" + archivePath);
                     }

                     String className = webResourceSet.getClass().getName();
                     boolean isJar = className.equals("java.org.apache.catalina.webresources.JarResourceSet") || className.equals("java.org.apache.catalina.webresources.JarWarResourceSet");
                     String internalPath = (String)ReflectionUtils.invokeMethod(webResourceSet, "getInternalPath", false);
                     if (internalPath != null && !internalPath.isEmpty() && !internalPath.equals("/")) {
                        classpathOrder.addClasspathEntryObject(base + (isJar ? "!" : "") + (internalPath.startsWith("/") ? internalPath : "/" + internalPath), classLoader, scanSpec, log);
                     } else {
                        classpathOrder.addClasspathEntryObject(base, classLoader, scanSpec, log);
                     }
                  }
               }
            }
         }
      }

      Object urls = ReflectionUtils.invokeMethod(classLoader, "getURLs", false);
      classpathOrder.addClasspathEntryObject(urls, classLoader, scanSpec, log);
   }
}
