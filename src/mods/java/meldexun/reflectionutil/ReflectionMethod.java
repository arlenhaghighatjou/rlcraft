package meldexun.reflectionutil;

import java.lang.reflect.Method;

public class ReflectionMethod<T> {
   private final Method method;

   public ReflectionMethod(Method method) {
      this.method = method;
   }

   public ReflectionMethod(Class<?> clazz, String obfuscatedName, String deobfuscatedName, Class<?>... parameterTypes) {
      Method m = null;

      try {
         m = clazz.getDeclaredMethod(obfuscatedName, parameterTypes);
         m.setAccessible(true);
      } catch (NoSuchMethodException var9) {
         try {
            m = clazz.getDeclaredMethod(deobfuscatedName, parameterTypes);
            m.setAccessible(true);
         } catch (NoSuchMethodException var8) {
            ;
         }
      }

      this.method = m;
   }

   public ReflectionMethod(String className, String obfuscatedName, String deobfuscatedName, Class<?>... parameterTypes) {
      Class<?> clazz;
      try {
         clazz = Class.forName(className);
      } catch (ClassNotFoundException var11) {
         this.method = null;
         return;
      }

      Method m = null;

      try {
         m = clazz.getDeclaredMethod(obfuscatedName, parameterTypes);
         m.setAccessible(true);
      } catch (NoSuchMethodException var10) {
         try {
            m = clazz.getDeclaredMethod(deobfuscatedName, parameterTypes);
            m.setAccessible(true);
         } catch (NoSuchMethodException var9) {
            ;
         }
      }

      this.method = m;
   }

   public T invoke(Object obj, Object... args) {
      try {
         return (T)this.method.invoke(obj, args);
      } catch (ReflectiveOperationException var4) {
         throw new RuntimeException(var4);
      }
   }

   public boolean isPresent() {
      return this.method != null;
   }
}
