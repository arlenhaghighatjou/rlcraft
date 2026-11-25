package yalter.mousetweaks;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;

public class Reflection {
   private static Obfuscation obfuscation;
   private static boolean checkObfuscation = true;
   private static HashMap<Class<?>, Method> HMCCache = new HashMap();
   public static ReflectionCache guiContainerClass;

   public Reflection() {
   }

   static void reflectGuiContainer() {
      Logger.Log("Reflecting GuiContainer...");
      guiContainerClass = new ReflectionCache();

      try {
         Field f = getField(bmg.class, getObfuscatedName(Constants.IGNOREMOUSEUP_NAME));
         guiContainerClass.storeField(Constants.IGNOREMOUSEUP_NAME.forgeName, f);
      } catch (NoSuchFieldException var4) {
         Logger.Log("Could not retrieve GuiContainer.ignoreMouseUp.");
         guiContainerClass = null;
         return;
      }

      try {
         Field f = getField(bmg.class, getObfuscatedName(Constants.DRAGSPLITTING_NAME));
         guiContainerClass.storeField(Constants.DRAGSPLITTING_NAME.forgeName, f);
      } catch (NoSuchFieldException var3) {
         Logger.Log("Could not retrieve GuiContainer.dragSplitting.");
         guiContainerClass = null;
         return;
      }

      try {
         Field f = getField(bmg.class, getObfuscatedName(Constants.DRAGSPLITTINGBUTTON_NAME));
         guiContainerClass.storeField(Constants.DRAGSPLITTINGBUTTON_NAME.forgeName, f);
      } catch (NoSuchFieldException var2) {
         Logger.Log("Could not retrieve GuiContainer.dragSplittingButton.");
         guiContainerClass = null;
         return;
      }

      try {
         Method m = getMethod(bmg.class, getObfuscatedName(Constants.GETSLOTATPOSITION_NAME), Integer.TYPE, Integer.TYPE);
         guiContainerClass.storeMethod(Constants.GETSLOTATPOSITION_NAME.forgeName, m);
      } catch (NoSuchMethodException var1) {
         Logger.Log("Could not retrieve GuiContainer.getSlotAtPosition().");
         guiContainerClass = null;
         return;
      }

      Logger.Log("Success.");
   }

   public static Method getHMCMethod(bmg object) {
      if (HMCCache.containsKey(object.getClass())) {
         return (Method)HMCCache.get(object.getClass());
      } else {
         try {
            Method method = searchMethod(object.getClass(), getObfuscatedName(Constants.HANDLEMOUSECLICK_NAME), agr.class, Integer.TYPE, Integer.TYPE, afw.class);
            Logger.DebugLog("Found handleMouseClick() for " + object.getClass().getSimpleName() + ", caching.");
            HMCCache.put(object.getClass(), method);
            return method;
         } catch (NoSuchMethodException var3) {
            b crashreport = b.a(var3, "MouseTweaks could not find handleMouseClick() in a GuiContainer.");
            throw new f(crashreport);
         }
      }
   }

   public static Method getHMCMethod(Object object) {
      if (HMCCache.containsKey(object.getClass())) {
         return (Method)HMCCache.get(object.getClass());
      } else {
         try {
            Method method = searchMethod(object.getClass(), getObfuscatedName(Constants.HANDLEMOUSECLICK_NAME), agr.class, Integer.TYPE, Integer.TYPE, afw.class);
            Logger.DebugLog("Found handleMouseClick() for " + object.getClass().getSimpleName() + ", caching.");
            HMCCache.put(object.getClass(), method);
            return method;
         } catch (NoSuchMethodException var2) {
            Logger.DebugLog("Could not find handleMouseClick() for " + object.getClass().getSimpleName() + ", using windowClick().");
            return null;
         }
      }
   }

   static boolean doesClassExist(String name) {
      try {
         Class.forName(name);
         return true;
      } catch (ClassNotFoundException var2) {
         return false;
      }
   }

   private static Field getField(Class<?> clazz, String name) throws NoSuchFieldException {
      Field field;
      try {
         field = clazz.getField(name);
      } catch (NoSuchFieldException var4) {
         field = clazz.getDeclaredField(name);
      }

      field.setAccessible(true);
      return field;
   }

   private static Method getMethod(Class<?> clazz, String name, Class<?>... args) throws NoSuchMethodException {
      Method method;
      try {
         method = clazz.getMethod(name, args);
      } catch (NoSuchMethodException var5) {
         method = clazz.getDeclaredMethod(name, args);
      }

      method.setAccessible(true);
      return method;
   }

   private static Method searchMethod(Class<?> clazz, String name, Class<?>... args) throws NoSuchMethodException {
      while(true) {
         try {
            Method method = clazz.getDeclaredMethod(name, args);
            method.setAccessible(true);
            return method;
         } catch (NoSuchMethodException var5) {
            clazz = clazz.getSuperclass();
            if (clazz == null) {
               throw new NoSuchMethodException();
            }
         }
      }
   }

   private static String getObfuscatedName(ObfuscatedName obfuscatedName) {
      if (checkObfuscation) {
         checkObfuscation();
      }

      return obfuscatedName.get(obfuscation);
   }

   private static void checkObfuscation() {
      checkObfuscation = false;

      try {
         getField(bmg.class, Constants.IGNOREMOUSEUP_NAME.mcpName);
         obfuscation = Obfuscation.MCP;
      } catch (NoSuchFieldException var3) {
         try {
            getField(bmg.class, Constants.IGNOREMOUSEUP_NAME.forgeName);
            obfuscation = Obfuscation.FORGE;
         } catch (NoSuchFieldException var2) {
            obfuscation = Obfuscation.VANILLA;
         }
      }

      Logger.Log("Detected obfuscation: " + obfuscation + ".");
   }
}
