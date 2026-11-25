package stanhebben.zenscript.util;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Map;
import stanhebben.zenscript.compiler.ITypeRegistry;
import stanhebben.zenscript.compiler.TypeRegistry;
import stanhebben.zenscript.type.ZenType;

public class ZenTypeUtil {
   public static final ITypeRegistry EMPTY_REGISTRY = new TypeRegistry();
   private static final Map<String, String> SIGNATURE_MAP = new HashMap();

   public ZenTypeUtil() {
   }

   public static String signature(Class<?> cls) {
      if (SIGNATURE_MAP.containsKey(cls.getName())) {
         return (String)SIGNATURE_MAP.get(cls.getName());
      } else if (cls.isArray()) {
         return "[" + signature(cls.getComponentType());
      } else {
         String signature = "L" + internal(cls) + ";";
         SIGNATURE_MAP.put(cls.getName(), signature);
         return signature;
      }
   }

   public static String internal(Class<?> cls) {
      return cls.getName().replace('.', '/');
   }

   public static String descriptor(Method method) {
      StringBuilder output = new StringBuilder();
      output.append('(');

      for(Class param : method.getParameterTypes()) {
         output.append(signature(param));
      }

      output.append(')').append(signature(method.getReturnType()));
      return output.toString();
   }

   public static ZenType checkPrimitive(ZenType type) {
      Class javaClass = type.toJavaClass();
      if (javaClass == null) {
         return type;
      } else {
         if (javaClass.isPrimitive()) {
            if (type == ZenType.INT) {
               return ZenType.INTOBJECT;
            }

            if (type == ZenType.BOOL) {
               return ZenType.BOOLOBJECT;
            }

            if (type == ZenType.BYTE) {
               return ZenType.BYTEOBJECT;
            }

            if (type == ZenType.SHORT) {
               return ZenType.SHORTOBJECT;
            }

            if (type == ZenType.DOUBLE) {
               return ZenType.DOUBLEOBJECT;
            }

            if (type == ZenType.FLOAT) {
               return ZenType.FLOATOBJECT;
            }

            if (type == ZenType.LONG) {
               return ZenType.LONGOBJECT;
            }
         }

         return type;
      }
   }

   public static boolean isPrimitive(ZenType type) {
      return type != checkPrimitive(type);
   }

   public static Method findFunctionalInterfaceMethod(Class<?> cls) {
      if (!cls.isInterface()) {
         return null;
      } else {
         Method foundMethod = null;

         for(Method method : cls.getMethods()) {
            if (!method.isDefault() && Modifier.isPublic(method.getModifiers()) && Modifier.isAbstract(method.getModifiers())) {
               if (foundMethod != null) {
                  return null;
               }

               foundMethod = method;
            }
         }

         return foundMethod;
      }
   }

   static {
      SIGNATURE_MAP.put(Boolean.TYPE.getName(), "Z");
      SIGNATURE_MAP.put(Byte.TYPE.getName(), "B");
      SIGNATURE_MAP.put(Short.TYPE.getName(), "S");
      SIGNATURE_MAP.put(Integer.TYPE.getName(), "I");
      SIGNATURE_MAP.put(Long.TYPE.getName(), "J");
      SIGNATURE_MAP.put(Float.TYPE.getName(), "F");
      SIGNATURE_MAP.put(Double.TYPE.getName(), "D");
      SIGNATURE_MAP.put(Character.TYPE.getName(), "C");
      SIGNATURE_MAP.put(Void.TYPE.getName(), "V");
   }
}
