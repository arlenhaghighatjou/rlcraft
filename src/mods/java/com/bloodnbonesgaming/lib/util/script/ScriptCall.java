package com.bloodnbonesgaming.lib.util.script;

import java.math.BigDecimal;

public class ScriptCall {
   public ScriptCall() {
   }

   public static boolean isNumber(Object arg) {
      return arg != null && arg.getClass() == BigDecimal.class;
   }

   public static boolean isString(Object arg) {
      return arg == null || arg instanceof String;
   }

   public static boolean isBoolean(Object arg) {
      return arg != null && arg.getClass() == Boolean.class;
   }

   public static int toInt(Object arg) {
      return ((BigDecimal)arg).intValueExact();
   }

   public static long toLong(Object arg) {
      return ((BigDecimal)arg).longValueExact();
   }

   public static float toFloat(Object arg) {
      return ((BigDecimal)arg).floatValue();
   }

   public static double toDouble(Object arg) {
      return ((BigDecimal)arg).doubleValue();
   }
}
