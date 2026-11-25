package nonapi.io.github.classgraph.classloaderhandler;

import io.github.classgraph.ClassGraph;
import nonapi.io.github.classgraph.classpath.ClassLoaderOrder;
import nonapi.io.github.classgraph.classpath.ClasspathOrder;
import nonapi.io.github.classgraph.scanspec.ScanSpec;
import nonapi.io.github.classgraph.utils.LogNode;

class SpringBootRestartClassLoaderHandler implements ClassLoaderHandler {
   private SpringBootRestartClassLoaderHandler() {
   }

   public static boolean canHandle(Class<?> classLoaderClass, LogNode log) {
      return "org.springframework.boot.devtools.restart.classloader.RestartClassLoader".equals(classLoaderClass.getName());
   }

   public static void findClassLoaderOrder(ClassLoader classLoader, ClassLoaderOrder classLoaderOrder, LogNode log) {
      classLoaderOrder.delegateTo(Thread.currentThread().getContextClassLoader(), false, log);
      classLoaderOrder.delegateTo(ClassGraph.class.getClassLoader(), false, log);
      classLoaderOrder.delegateTo(ClassLoader.getSystemClassLoader(), true, log);
      classLoaderOrder.delegateTo(classLoader.getParent(), true, log);
   }

   public static void findClasspathOrder(ClassLoader classLoader, ClasspathOrder classpathOrder, ScanSpec scanSpec, LogNode log) {
   }
}
