package com.charles445.rltweaker.util;

public class StackTraceUtil {
   public StackTraceUtil() {
   }

   public static boolean stackTraceHasClass(String clazz) {
      for(StackTraceElement ste : Thread.currentThread().getStackTrace()) {
         if (ste.getClassName().equals(clazz)) {
            return true;
         }
      }

      return false;
   }

   public static boolean stackTraceHasClassOfMethod(String clazz, String... methods) {
      for(StackTraceElement ste : Thread.currentThread().getStackTrace()) {
         if (ste.getClassName().equals(clazz)) {
            for(String m : methods) {
               if (ste.getMethodName().equals(m)) {
                  return true;
               }
            }
         }
      }

      return false;
   }
}
