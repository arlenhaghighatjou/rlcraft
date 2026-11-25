package nonapi.io.github.classgraph.classloaderhandler;

import java.lang.reflect.Array;
import java.util.HashSet;
import java.util.Set;
import nonapi.io.github.classgraph.classpath.ClassLoaderOrder;
import nonapi.io.github.classgraph.classpath.ClasspathOrder;
import nonapi.io.github.classgraph.scanspec.ScanSpec;
import nonapi.io.github.classgraph.utils.LogNode;
import nonapi.io.github.classgraph.utils.ReflectionUtils;

class EquinoxClassLoaderHandler implements ClassLoaderHandler {
   private static boolean alreadyReadSystemBundles;
   private static final String[] FIELD_NAMES = new String[]{"cp", "nestedDirName"};

   private EquinoxClassLoaderHandler() {
   }

   public static boolean canHandle(Class<?> classLoaderClass, LogNode log) {
      return "org.eclipse.osgi.internal.loader.EquinoxClassLoader".equals(classLoaderClass.getName());
   }

   public static void findClassLoaderOrder(ClassLoader classLoader, ClassLoaderOrder classLoaderOrder, LogNode log) {
      classLoaderOrder.delegateTo(classLoader.getParent(), true, log);
      classLoaderOrder.add(classLoader, log);
   }

   private static void addBundleFile(Object bundlefile, Set<Object> path, ClassLoader classLoader, ClasspathOrder classpathOrderOut, ScanSpec scanSpec, LogNode log) {
      if (bundlefile != null && path.add(bundlefile)) {
         Object baseFile = ReflectionUtils.getFieldVal(bundlefile, "basefile", false);
         if (baseFile != null) {
            boolean foundClassPathElement = false;

            for(String fieldName : FIELD_NAMES) {
               Object fieldVal = ReflectionUtils.getFieldVal(bundlefile, fieldName, false);
               if (fieldVal != null) {
                  foundClassPathElement = true;
                  Object base = baseFile;
                  String sep = "/";
                  if (bundlefile.getClass().getName().equals("org.eclipse.osgi.storage.bundlefile.NestedDirBundleFile")) {
                     Object baseBundleFile = ReflectionUtils.getFieldVal(bundlefile, "baseBundleFile", false);
                     if (baseBundleFile != null && baseBundleFile.getClass().getName().equals("org.eclipse.osgi.storage.bundlefile.ZipBundleFile")) {
                        base = baseBundleFile;
                        sep = "!/";
                     }
                  }

                  String pathElement = base.toString() + sep + fieldVal.toString();
                  classpathOrderOut.addClasspathEntry(pathElement, classLoader, scanSpec, log);
                  break;
               }
            }

            if (!foundClassPathElement) {
               classpathOrderOut.addClasspathEntry(baseFile.toString(), classLoader, scanSpec, log);
            }
         }

         addBundleFile(ReflectionUtils.getFieldVal(bundlefile, "wrapped", false), path, classLoader, classpathOrderOut, scanSpec, log);
         addBundleFile(ReflectionUtils.getFieldVal(bundlefile, "next", false), path, classLoader, classpathOrderOut, scanSpec, log);
      }

   }

   private static void addClasspathEntries(Object owner, ClassLoader classLoader, ClasspathOrder classpathOrderOut, ScanSpec scanSpec, LogNode log) {
      Object entries = ReflectionUtils.getFieldVal(owner, "entries", false);
      if (entries != null) {
         int i = 0;

         for(int n = Array.getLength(entries); i < n; ++i) {
            Object entry = Array.get(entries, i);
            Object bundlefile = ReflectionUtils.getFieldVal(entry, "bundlefile", false);
            addBundleFile(bundlefile, new HashSet(), classLoader, classpathOrderOut, scanSpec, log);
         }
      }

   }

   public static void findClasspathOrder(ClassLoader classLoader, ClasspathOrder classpathOrder, ScanSpec scanSpec, LogNode log) {
      Object manager = ReflectionUtils.getFieldVal(classLoader, "manager", false);
      addClasspathEntries(manager, classLoader, classpathOrder, scanSpec, log);
      Object fragments = ReflectionUtils.getFieldVal(manager, "fragments", false);
      if (fragments != null) {
         int f = 0;

         for(int fragLength = Array.getLength(fragments); f < fragLength; ++f) {
            Object fragment = Array.get(fragments, f);
            addClasspathEntries(fragment, classLoader, classpathOrder, scanSpec, log);
         }
      }

      if (!alreadyReadSystemBundles) {
         Object delegate = ReflectionUtils.getFieldVal(classLoader, "delegate", false);
         Object container = ReflectionUtils.getFieldVal(delegate, "container", false);
         Object storage = ReflectionUtils.getFieldVal(container, "storage", false);
         Object moduleContainer = ReflectionUtils.getFieldVal(storage, "moduleContainer", false);
         Object moduleDatabase = ReflectionUtils.getFieldVal(moduleContainer, "moduleDatabase", false);
         Object modulesById = ReflectionUtils.getFieldVal(moduleDatabase, "modulesById", false);
         Object module0 = ReflectionUtils.invokeMethod(modulesById, "get", Object.class, Long.valueOf(0L), false);
         Object bundle = ReflectionUtils.invokeMethod(module0, "getBundle", false);
         Object bundleContext = ReflectionUtils.invokeMethod(bundle, "getBundleContext", false);
         Object bundles = ReflectionUtils.invokeMethod(bundleContext, "getBundles", false);
         if (bundles != null) {
            int i = 0;

            for(int n = Array.getLength(bundles); i < n; ++i) {
               Object equinoxBundle = Array.get(bundles, i);
               Object module = ReflectionUtils.getFieldVal(equinoxBundle, "module", false);
               String location = (String)ReflectionUtils.getFieldVal(module, "location", false);
               if (location != null) {
                  int fileIdx = location.indexOf("file:");
                  if (fileIdx >= 0) {
                     location = location.substring(fileIdx);
                     classpathOrder.addClasspathEntry(location, classLoader, scanSpec, log);
                  }
               }
            }
         }

         alreadyReadSystemBundles = true;
      }

   }
}
