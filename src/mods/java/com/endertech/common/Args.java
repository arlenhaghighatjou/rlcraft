package com.endertech.common;

import java.util.ArrayList;

public final class Args {
   public static final String DELIMITER = ", ";

   public Args() {
   }

   public static String joinDelim(String delimiter, Object... args) {
      ArrayList<String> list = new ArrayList();

      for(Object object : args) {
         list.add(String.valueOf(object));
      }

      return String.join(delimiter, list);
   }

   public static String join(Object... args) {
      return joinDelim(", ", args);
   }

   public static String group(Object... args) {
      return "{ " + join(args) + " }";
   }

   public static String extend(Object obj) {
      return " extends " + String.valueOf(obj);
   }

   public static String get(String name, Object obj) {
      return name + " = " + obj;
   }

   public static String[] split(String string) {
      String[] params = string.trim().replace("(", "").replace(")", "").replace("[", "").replace("]", "").replace("<", "").replace(">", "").replace("{", "").replace("}", "").replace(" ", "").split(",");

      for(int i = 0; i < params.length; ++i) {
         params[i] = params[i].trim();
      }

      return params;
   }
}
