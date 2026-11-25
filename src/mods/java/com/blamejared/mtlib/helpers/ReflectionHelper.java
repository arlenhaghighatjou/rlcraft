package com.blamejared.mtlib.helpers;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;

public class ReflectionHelper {
   public ReflectionHelper() {
   }

   public static <T> T getInstance(Constructor<T> constructor, Object... arguments) {
      try {
         return (T)constructor.newInstance(arguments);
      } catch (Exception var3) {
         LogHelper.logError("Exception creating instance of " + constructor.getClass().getName(), var3);
         return (T)null;
      }
   }

   public static Constructor<?> getConstructor(String className, Class<?>... argumentTypes) {
      if (className != null && argumentTypes != null) {
         try {
            Class<?> clazz = Class.forName(className);
            Constructor<?> constructor = clazz.getDeclaredConstructor(argumentTypes);
            constructor.setAccessible(true);
            return constructor;
         } catch (Exception var4) {
            LogHelper.logError("Exception getting constructor of " + className, var4);
            return null;
         }
      } else {
         LogHelper.logError("The provided class name or arguments can't be null.");
         return null;
      }
   }

   public static <T> Constructor<T> getConstructor(Class<T> clazz, Class<?>... types) {
      try {
         Constructor<T> constructor = clazz.getDeclaredConstructor(types);
         constructor.setAccessible(true);
         return constructor;
      } catch (Exception var3) {
         LogHelper.logError("Exception getting constructor of " + clazz.getName(), var3);
         return null;
      }
   }

   public static <T> T getObject(Object object, String... fieldNames) {
      Class<?> cls = object.getClass();

      for(String field : fieldNames) {
         try {
            Field result = cls.getDeclaredField(field);
            result.setAccessible(true);
            return (T)result.get(object);
         } catch (Exception var8) {
            LogHelper.logError("Exception in getObject()", var8);
         }
      }

      LogHelper.logError("Could not retrieve any object for the provided field names.");
      return (T)null;
   }

   public static <T> T getFinalObject(Object object, String... fieldNames) {
      Class<?> cls = object.getClass();

      for(String field : fieldNames) {
         try {
            Field result = cls.getDeclaredField(field);
            result.setAccessible(true);
            Field modifiersField = Field.class.getDeclaredField("modifiers");
            modifiersField.setAccessible(true);
            modifiersField.setInt(result, result.getModifiers() & -17);
            return (T)result.get(object);
         } catch (Exception var9) {
            LogHelper.logError("Exception in getFinalObject()", var9);
         }
      }

      LogHelper.logError("Could not retrieve any final object for the provided field names.");
      return (T)null;
   }

   public static <T> T getStaticObject(Class<?> clazz, String... fieldNames) {
      for(String field : fieldNames) {
         try {
            Field result = clazz.getDeclaredField(field);
            result.setAccessible(true);
            return (T)result.get((Object)null);
         } catch (Exception var7) {
            LogHelper.logError("Exception in getStaticObject()", var7);
         }
      }

      LogHelper.logError("Could not retrieve any static object for the provided field names.");
      return (T)null;
   }

   public static <T> T getStaticObject(String className, String... fieldNames) {
      try {
         Class<?> clazz = Class.forName(className);
         return (T)getStaticObject(clazz, fieldNames);
      } catch (ClassNotFoundException var3) {
         LogHelper.logError("Exception in getStaticObject()", var3);
         LogHelper.logError("Could not retrieve any static object for the provided field names.");
         return (T)null;
      }
   }

   public static void setPrivateValue(Class cls, Object o, String field, Object var) {
      net.minecraftforge.fml.relauncher.ReflectionHelper.setPrivateValue(cls, o, var, new String[]{field});
   }
}
