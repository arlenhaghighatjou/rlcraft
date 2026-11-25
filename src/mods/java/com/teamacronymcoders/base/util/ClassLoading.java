package com.teamacronymcoders.base.util;

import com.google.common.collect.Maps;
import com.teamacronymcoders.base.proxies.LibCommonProxy;
import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.discovery.ASMDataTable;
import net.minecraftforge.fml.common.discovery.ASMDataTable.ASMData;
import net.minecraftforge.fml.relauncher.Side;

public class ClassLoading {
   private ClassLoading() {
   }

   @Nullable
   public static LibCommonProxy createProxy(String clientString, String serverString) {
      Side side = FMLCommonHandler.instance().getEffectiveSide();
      String proxyString = side == Side.CLIENT ? clientString : serverString;
      Object proxyObject = createObjectInstance(proxyString);
      return proxyObject instanceof LibCommonProxy ? (LibCommonProxy)proxyObject : null;
   }

   @Nullable
   public static <T> T createInstanceOf(Class<T> tClass, String path) {
      Object object = createObjectInstance(path);
      return (T)(object != null ? tClass.cast(object) : null);
   }

   @Nullable
   public static Object createObjectInstance(String path) {
      try {
         Class classToGrab = Class.forName(path);
         return createObjectInstance(classToGrab);
      } catch (ClassNotFoundException var2) {
         Platform.attemptLogExceptionToCurrentMod(var2);
         Platform.attemptLogErrorToCurrentMod(path + " did not initialize. Something's gonna break.");
         return null;
      }
   }

   @Nullable
   public static <T> T createObjectInstance(Class<T> clazz) {
      try {
         return (T)clazz.newInstance();
      } catch (IllegalAccessException | InstantiationException var2) {
         Platform.attemptLogExceptionToCurrentMod(var2);
         Platform.attemptLogErrorToCurrentMod(clazz.getName() + " did not initialize. Something's gonna break.");
         return (T)null;
      }
   }

   public static <T> List<T> getInstances(@Nonnull ASMDataTable asmDataTable, Class annotationClass, Class<T> instanceClass) {
      return getInstances(asmDataTable, annotationClass, instanceClass, (aClass) -> {
         return true;
      });
   }

   public static <T> List<T> getInstances(@Nonnull ASMDataTable asmDataTable, Class annotationClass, Class<T> instanceClass, Function<Class<? extends T>, Boolean> createInstance) {
      String annotationClassName = annotationClass.getCanonicalName();
      Set<ASMData> asmDataSet = asmDataTable.getAll(annotationClassName);
      List<T> instances = new ArrayList();

      for(ASMData asmData : asmDataSet) {
         try {
            Class<?> asmClass = Class.forName(asmData.getClassName());
            Class<? extends T> asmInstanceClass = asmClass.asSubclass(instanceClass);
            if (((Boolean)createInstance.apply(asmInstanceClass)).booleanValue()) {
               T instance = (T)asmInstanceClass.newInstance();
               instances.add(instance);
            }
         } catch (InstantiationException | IllegalAccessException var12) {
            Platform.attemptLogErrorToCurrentMod("Failed to load: " + asmData.getClassName());
            Platform.attemptLogExceptionToCurrentMod(var12);
         } catch (ClassNotFoundException var13) {
            ;
         }
      }

      return instances;
   }

   public static <T, U extends Annotation> Map<String, T> getInstances(@Nonnull ASMDataTable asmDataTable, Class<U> annotationClass, Class<T> instanceClass, Function<Class<? extends T>, Boolean> createInstance, Function<U, String> getKey) {
      String annotationClassName = annotationClass.getCanonicalName();
      Set<ASMData> asmDataSet = asmDataTable.getAll(annotationClassName);
      Map<String, T> instances = Maps.newHashMap();

      for(ASMData asmData : asmDataSet) {
         try {
            Class<?> asmClass = Class.forName(asmData.getClassName());
            Class<? extends T> asmInstanceClass = asmClass.asSubclass(instanceClass);
            if (((Boolean)createInstance.apply(asmInstanceClass)).booleanValue()) {
               T instance = (T)asmInstanceClass.newInstance();
               U annotation = asmInstanceClass.getAnnotation(annotationClass);
               String key = (String)getKey.apply(annotation);
               instances.put(key, instance);
            }
         } catch (InstantiationException | IllegalAccessException var15) {
            Platform.attemptLogErrorToCurrentMod("Failed to load: " + asmData.getClassName());
            Platform.attemptLogExceptionToCurrentMod(var15);
         } catch (ClassNotFoundException var16) {
            ;
         }
      }

      return instances;
   }

   public static <T> T createInstanceOf(Class<T> tClass, String path, Class[] classes, Object[] inputs) {
      try {
         Class pathClass = Class.forName(path);
         Constructor constructor = pathClass.getConstructor(classes);
         Object object = constructor.newInstance(inputs);
         return (T)tClass.cast(object);
      } catch (ClassNotFoundException var7) {
         throw new IllegalArgumentException("Couldn't find class for path: " + path);
      } catch (NoSuchMethodException var8) {
         throw new IllegalArgumentException("Couldn't find constructor for inputs");
      } catch (InvocationTargetException | IllegalAccessException var9) {
         throw new IllegalStateException("Couldn't access constructor");
      } catch (InstantiationException var10) {
         throw new IllegalArgumentException("Couldn't create object from constructor");
      }
   }
}
