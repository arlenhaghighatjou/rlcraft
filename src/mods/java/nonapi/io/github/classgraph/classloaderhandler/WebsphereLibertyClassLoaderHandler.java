package nonapi.io.github.classgraph.classloaderhandler;

import java.io.File;
import java.util.List;
import nonapi.io.github.classgraph.classpath.ClassLoaderOrder;
import nonapi.io.github.classgraph.classpath.ClasspathOrder;
import nonapi.io.github.classgraph.scanspec.ScanSpec;
import nonapi.io.github.classgraph.utils.LogNode;
import nonapi.io.github.classgraph.utils.ReflectionUtils;

class WebsphereLibertyClassLoaderHandler implements ClassLoaderHandler {
   private static final String PKG_PREFIX = "com.ibm.ws.classloading.internal.";
   private static final String IBM_APP_CLASS_LOADER = "com.ibm.ws.classloading.internal.AppClassLoader";
   private static final String IBM_THREAD_CONTEXT_CLASS_LOADER = "com.ibm.ws.classloading.internal.ThreadContextClassLoader";

   private WebsphereLibertyClassLoaderHandler() {
   }

   public static boolean canHandle(Class<?> classLoaderClass, LogNode log) {
      return "com.ibm.ws.classloading.internal.AppClassLoader".equals(classLoaderClass.getName()) || "com.ibm.ws.classloading.internal.ThreadContextClassLoader".equals(classLoaderClass.getName());
   }

   public static void findClassLoaderOrder(ClassLoader classLoader, ClassLoaderOrder classLoaderOrder, LogNode log) {
      classLoaderOrder.delegateTo(classLoader.getParent(), true, log);
      classLoaderOrder.add(classLoader, log);
   }

   private static String getPath(Object classpath) {
      Object container = ReflectionUtils.getFieldVal(classpath, "container", false);
      if (container == null) {
         return "";
      } else {
         Object delegate = ReflectionUtils.getFieldVal(container, "delegate", false);
         if (delegate == null) {
            return "";
         } else {
            String path = (String)ReflectionUtils.getFieldVal(delegate, "path", false);
            if (path != null && path.length() > 0) {
               return path;
            } else {
               Object base = ReflectionUtils.getFieldVal(delegate, "base", false);
               if (base == null) {
                  return "";
               } else {
                  Object archiveFile = ReflectionUtils.getFieldVal(base, "archiveFile", false);
                  if (archiveFile != null) {
                     File file = (File)archiveFile;
                     return file.getAbsolutePath();
                  } else {
                     return "";
                  }
               }
            }
         }
      }
   }

   public static void findClasspathOrder(ClassLoader classLoader, ClasspathOrder classpathOrder, ScanSpec scanSpec, LogNode log) {
      Object appLoader = ReflectionUtils.getFieldVal(classLoader, "appLoader", false);
      Object smartClassPath;
      if (appLoader != null) {
         smartClassPath = ReflectionUtils.getFieldVal(appLoader, "smartClassPath", false);
      } else {
         smartClassPath = ReflectionUtils.getFieldVal(classLoader, "smartClassPath", false);
      }

      if (smartClassPath != null) {
         List<?> classPathElements = (List)ReflectionUtils.getFieldVal(smartClassPath, "classPath", false);
         if (classPathElements != null) {
            for(Object classpath : classPathElements) {
               String path = getPath(classpath);
               if (path != null && path.length() > 0) {
                  classpathOrder.addClasspathEntry(path, classLoader, scanSpec, log);
               }
            }
         }
      }

   }
}
