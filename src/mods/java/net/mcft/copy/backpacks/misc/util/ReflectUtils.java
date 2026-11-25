package net.mcft.copy.backpacks.misc.util;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.commons.lang3.tuple.Triple;

public final class ReflectUtils {
   private static Map<Pair<Class<?>, String>, Field> _cachedFields = new HashMap();
   private static Map<Triple<Class<?>, String, Class<?>[]>, Method> _cachedMethods = new HashMap();

   private ReflectUtils() {
   }

   public static Field findField(Class<?> clazz, String mcpName, String srgName) {
      return (Field)_cachedFields.computeIfAbsent(Pair.of(clazz, mcpName), (key) -> {
         Field field = null;

         for(String name : new String[]{srgName, mcpName}) {
            try {
               field = clazz.getDeclaredField(name);
            } catch (NoSuchFieldException var11) {
               ;
            }

            if (field != null) {
               try {
                  field.setAccessible(true);
                  return field;
               } catch (SecurityException var10) {
                  throw new RuntimeException(var10);
               }
            }
         }

         throw new RuntimeException(String.format("Could not find field '%s'/'%s' for class %s", mcpName, srgName, clazz.getName()));
      });
   }

   public static <T, V> V get(Class<? extends T> clazz, T instance, String mcpName, String srgName) {
      try {
         return (V)findField(clazz, mcpName, srgName).get(instance);
      } catch (ReflectiveOperationException var5) {
         throw new RuntimeException(var5);
      }
   }

   public static <T, V> V get(Class<T> clazz, String mcpName, String srgName) {
      return (V)get(clazz, (Object)null, mcpName, srgName);
   }

   public static <T, V> V get(T instance, String mcpName, String srgName) {
      return (V)get(instance.getClass(), instance, mcpName, srgName);
   }

   public static <T, V> void set(Class<? extends T> clazz, T instance, String mcpName, String srgName, V value) {
      try {
         findField(clazz, mcpName, srgName).set(instance, value);
      } catch (ReflectiveOperationException var6) {
         throw new RuntimeException(var6);
      }
   }

   public static <T, V> void set(Class<T> clazz, String mcpName, String srgName, V value) {
      set(clazz, (Object)null, mcpName, srgName, value);
   }

   public static <T, V> void set(T instance, String mcpName, String srgName, V value) {
      set(instance.getClass(), instance, mcpName, srgName, value);
   }

   public static Method findMethod(Class<?> clazz, String mcpName, String srgName, Class<?>... parameterTypes) {
      return (Method)_cachedMethods.computeIfAbsent(Triple.of(clazz, mcpName, parameterTypes), (key) -> {
         for(String name : new String[]{srgName, mcpName}) {
            Method method;
            try {
               method = clazz.getDeclaredMethod(name, parameterTypes);
            } catch (NoSuchMethodException var12) {
               continue;
            }

            if (method != null) {
               try {
                  method.setAccessible(true);
                  return method;
               } catch (SecurityException var11) {
                  throw new RuntimeException(var11);
               }
            }
         }

         throw new RuntimeException(String.format("Could not find method '%s'/'%s' for class %s", mcpName, srgName, clazz.getName()));
      });
   }
}
