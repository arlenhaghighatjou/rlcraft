package com.bloodnbonesgaming.lib.util;

public class ReflectionHelper {
   public ReflectionHelper() {
   }

   public static StackTraceElement retrieveCallingStackTraceElement() {
      return retrieveCallingStackTraceElement(3);
   }

   public static StackTraceElement retrieveCallingStackTraceElement(int depth) {
      return (new Throwable()).getStackTrace()[depth];
   }
}
