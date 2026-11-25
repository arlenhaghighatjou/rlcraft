package nonapi.io.github.classgraph.utils;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

public final class ReflectionUtils {
   private ReflectionUtils() {
   }

   private static Object getFieldVal(Class<?> cls, Object obj, String fieldName, boolean throwException) throws IllegalArgumentException {
      Field field = null;

      for(Class<?> currClass = cls; currClass != null; currClass = currClass.getSuperclass()) {
         try {
            field = currClass.getDeclaredField(fieldName);
            break;
         }
      }

      if (field == null) {
         if (throwException) {
            throw new IllegalArgumentException((obj == null ? "Static field " : "Field ") + "\"" + fieldName + "\" not found or not accessible");
         }
      } else {
         try {
            field.setAccessible(true);
         } catch (RuntimeException var7) {
            ;
         }

         try {
            return field.get(obj);
         } catch (IllegalAccessException var8) {
            if (throwException) {
               throw new IllegalArgumentException("Can't read " + (obj == null ? "static " : "") + " field \"" + fieldName + "\": " + var8);
            }
         }
      }

      return null;
   }

   public static Object getFieldVal(Object obj, String fieldName, boolean throwException) throws IllegalArgumentException {
      if (obj != null && fieldName != null) {
         return getFieldVal(obj.getClass(), obj, fieldName, throwException);
      } else if (throwException) {
         throw new NullPointerException();
      } else {
         return null;
      }
   }

   public static Object getStaticFieldVal(Class<?> cls, String fieldName, boolean throwException) throws IllegalArgumentException {
      if (cls != null && fieldName != null) {
         return getFieldVal(cls, (Object)null, fieldName, throwException);
      } else if (throwException) {
         throw new NullPointerException();
      } else {
         return null;
      }
   }

   private static List<Class<?>> getReverseMethodAttemptOrder(Class<?> cls) {
      List<Class<?>> reverseAttemptOrder = new ArrayList();

      for(Class<?> c = cls; c != null && c != Object.class; c = c.getSuperclass()) {
         reverseAttemptOrder.add(c);
      }

      Set<Class<?>> addedIfaces = new HashSet();
      LinkedList<Class<?>> ifaceQueue = new LinkedList();

      for(Class<?> c = cls; c != null; c = c.getSuperclass()) {
         if (c.isInterface() && addedIfaces.add(c)) {
            ifaceQueue.add(c);
         }

         for(Class<?> iface : c.getInterfaces()) {
            if (addedIfaces.add(iface)) {
               ifaceQueue.add(iface);
            }
         }
      }

      while(!ifaceQueue.isEmpty()) {
         Class<?> iface = (Class)ifaceQueue.remove();
         reverseAttemptOrder.add(iface);
         Class<?>[] superIfaces = iface.getInterfaces();
         if (superIfaces.length > 0) {
            for(Class<?> superIface : superIfaces) {
               if (addedIfaces.add(superIface)) {
                  ifaceQueue.add(superIface);
               }
            }
         }
      }

      return reverseAttemptOrder;
   }

   private static Object invokeMethod(Class<?> cls, Object obj, String methodName, boolean oneArg, Class<?> argType, Object param, boolean throwException) throws IllegalArgumentException {
      Method method = null;
      List<Class<?>> reverseAttemptOrder = getReverseMethodAttemptOrder(cls);

      for(int i = reverseAttemptOrder.size() - 1; i >= 0; --i) {
         Class<?> classOrInterface = (Class)reverseAttemptOrder.get(i);

         try {
            method = oneArg ? classOrInterface.getDeclaredMethod(methodName, argType) : classOrInterface.getDeclaredMethod(methodName);
            break;
         }
      }

      if (method == null) {
         if (throwException) {
            throw new IllegalArgumentException((obj == null ? "Static method " : "Method ") + "\"" + methodName + "\" not found or not accesible");
         }
      } else {
         try {
            method.setAccessible(true);
         } catch (RuntimeException var12) {
            ;
         }

         try {
            return oneArg ? method.invoke(obj, param) : method.invoke(obj);
         } catch (IllegalAccessException var13) {
            if (throwException) {
               throw new IllegalArgumentException("Can't call " + (obj == null ? "static " : "") + "method \"" + methodName + "\": " + var13);
            }
         } catch (InvocationTargetException var14) {
            if (throwException) {
               throw new IllegalArgumentException("Exception while invoking " + (obj == null ? "static " : "") + "method \"" + methodName + "\"", var14);
            }
         }
      }

      return null;
   }

   public static Object invokeMethod(Object obj, String methodName, boolean throwException) throws IllegalArgumentException {
      if (obj != null && methodName != null) {
         return invokeMethod(obj.getClass(), obj, methodName, false, (Class)null, (Object)null, throwException);
      } else if (throwException) {
         throw new NullPointerException();
      } else {
         return null;
      }
   }

   public static Object invokeMethod(Object obj, String methodName, Class<?> argType, Object param, boolean throwException) throws IllegalArgumentException {
      if (obj != null && methodName != null) {
         return invokeMethod(obj.getClass(), obj, methodName, true, argType, param, throwException);
      } else if (throwException) {
         throw new NullPointerException();
      } else {
         return null;
      }
   }

   public static Object invokeStaticMethod(Class<?> cls, String methodName, boolean throwException) throws IllegalArgumentException {
      if (cls != null && methodName != null) {
         return invokeMethod(cls, (Object)null, methodName, false, (Class)null, (Object)null, throwException);
      } else if (throwException) {
         throw new NullPointerException();
      } else {
         return null;
      }
   }

   public static Object invokeStaticMethod(Class<?> cls, String methodName, Class<?> argType, Object param, boolean throwException) throws IllegalArgumentException {
      if (cls != null && methodName != null) {
         return invokeMethod(cls, (Object)null, methodName, true, argType, param, throwException);
      } else if (throwException) {
         throw new NullPointerException();
      } else {
         return null;
      }
   }

   public static Class<?> classForNameOrNull(String className) {
      try {
         return Class.forName(className);
      } catch (LinkageError | ReflectiveOperationException var2) {
         return null;
      }
   }
}
