package io.github.lxgaming.classloader;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;
import sun.misc.Unsafe;

public class ClassLoaderUtils {
   public static final String URL_CLASS_LOADER_CLASS = "java.net.URLClassLoader";
   public static final String BUILTIN_CLASS_LOADER_CLASS = "jdk.internal.loader.BuiltinClassLoader";
   public static final String URL_CLASS_PATH_CLASS = "jdk.internal.loader.URLClassPath";

   public ClassLoaderUtils() {
   }

   public static void appendToClassPath(URL url) throws Throwable {
      appendToClassPath(ClassLoaderUtils.class.getClassLoader(), url);
   }

   public static void appendToClassPath(ClassLoader classLoader, URL url) throws Throwable {
      try {
         Class<?> classLoaderClass = Class.forName("java.net.URLClassLoader");
         if (classLoaderClass.isInstance(classLoader)) {
            Method addURLMethod = classLoaderClass.getDeclaredMethod("addURL", URL.class);
            addURLMethod.setAccessible(true);
            addURLMethod.invoke(classLoader, url);
            return;
         }
      } catch (ClassNotFoundException var21) {
         ;
      }

      try {
         Class<?> classLoaderClass = Class.forName("jdk.internal.loader.BuiltinClassLoader");
         Class<?> classPathClass = Class.forName("jdk.internal.loader.URLClassPath");
         if (classLoaderClass.isInstance(classLoader)) {
            Unsafe unsafe = getUnsafe();
            Field ucpField = classLoaderClass.getDeclaredField("ucp");
            long ucpFieldOffset = unsafe.objectFieldOffset(ucpField);
            Object ucpObject = unsafe.getObject(classLoader, ucpFieldOffset);
            Field pathField = classPathClass.getDeclaredField("path");
            long pathFieldOffset = unsafe.objectFieldOffset(pathField);
            ArrayList<URL> path = (ArrayList)unsafe.getObject(ucpObject, pathFieldOffset);
            Field urlsField = getField(classPathClass.getDeclaredFields(), "urls", "unopenedUrls");
            long urlsFieldOffset = unsafe.objectFieldOffset(urlsField);
            Collection<URL> urls = (Collection)unsafe.getObject(ucpObject, urlsFieldOffset);
            synchronized(urls) {
               if (!path.contains(url)) {
                  urls.add(url);
                  path.add(url);
               }

               return;
            }
         }
      } catch (ClassNotFoundException var20) {
         ;
      }

      throw new UnsupportedOperationException("Unsupported ClassLoader");
   }

   public static URL[] getSystemClassPathURLs() throws Throwable {
      return getSystemClassPathURLs(ClassLoaderUtils.class.getClassLoader());
   }

   public static URL[] getSystemClassPathURLs(ClassLoader classLoader) throws Throwable {
      Objects.requireNonNull(classLoader, "ClassLoader cannot be null");

      try {
         Class<?> classLoaderClass = Class.forName("java.net.URLClassLoader");
         if (classLoaderClass.isInstance(classLoader)) {
            Method getURLsMethod = classLoaderClass.getMethod("getURLs");
            return (URL[])getURLsMethod.invoke(classLoader);
         }
      } catch (ClassNotFoundException var12) {
         ;
      }

      try {
         Class<?> classLoaderClass = Class.forName("jdk.internal.loader.BuiltinClassLoader");
         if (classLoaderClass.isInstance(classLoader)) {
            Unsafe unsafe = getUnsafe();
            Field ucpField = classLoaderClass.getDeclaredField("ucp");
            long ucpFieldOffset = unsafe.objectFieldOffset(ucpField);
            Object ucpObject = unsafe.getObject(classLoader, ucpFieldOffset);
            Field pathField = ucpField.getType().getDeclaredField("path");
            long pathFieldOffset = unsafe.objectFieldOffset(pathField);
            ArrayList<URL> path = (ArrayList)unsafe.getObject(ucpObject, pathFieldOffset);
            return (URL[])path.toArray(new URL[0]);
         }
      } catch (ClassNotFoundException var11) {
         ;
      }

      throw new UnsupportedOperationException("Unsupported ClassLoader");
   }

   private static Field getField(Field[] fields, String... names) throws NoSuchFieldException {
      for(Field field : fields) {
         for(String name : names) {
            if (field.getName().equals(name)) {
               return field;
            }
         }
      }

      throw new NoSuchFieldException(String.join(", ", names));
   }

   private static Unsafe getUnsafe() throws Throwable {
      Field unsafeField = Unsafe.class.getDeclaredField("theUnsafe");
      unsafeField.setAccessible(true);
      return (Unsafe)unsafeField.get((Object)null);
   }
}
