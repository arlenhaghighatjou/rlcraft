package meldexun.reflectionutil;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

public class ReflectionConstructor<C> {
   private final Constructor<C> constructor;

   public ReflectionConstructor(Constructor<C> constructor) {
      this.constructor = constructor;
   }

   public ReflectionConstructor(Class<C> clazz, Class<?>... parameterTypes) {
      Constructor<C> c = null;

      try {
         c = clazz.getDeclaredConstructor(parameterTypes);
         c.setAccessible(true);
      } catch (NoSuchMethodException var5) {
         ;
      }

      this.constructor = c;
   }

   public ReflectionConstructor(String className, Class<?>... parameterTypes) {
      Class<C> clazz;
      try {
         clazz = Class.forName(className);
      } catch (ClassNotFoundException var7) {
         this.constructor = null;
         return;
      }

      Constructor<C> c = null;

      try {
         c = clazz.getDeclaredConstructor(parameterTypes);
         c.setAccessible(true);
      } catch (NoSuchMethodException var6) {
         ;
      }

      this.constructor = c;
   }

   public C newInstance(Object... initargs) {
      try {
         return (C)this.constructor.newInstance(initargs);
      } catch (IllegalAccessException | InvocationTargetException | InstantiationException var3) {
         throw new RuntimeException(var3);
      }
   }

   public boolean isPresent() {
      return this.constructor != null;
   }
}
