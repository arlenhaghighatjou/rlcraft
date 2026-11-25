package com.endertech.common;

public class Console {
   public static boolean debugMode = true;
   public static final String STR_LINE_END = "\n";

   public static void infoMsg(String string) {
      System.out.println(string);
   }

   public static void infoMsg(String format, Object... args) {
      System.out.println(String.format(format, args));
   }

   public static void errorMsg(String string) {
      System.err.println(string);
   }

   public static void errorMsg(String format, Object... args) {
      System.err.println(String.format(format, args));
   }

   public static void debugMsg(String string) {
      if (debugMode) {
         infoMsg(string);
      }

   }

   public static void debugMsg(String format, Object... args) {
      if (debugMode) {
         infoMsg(format, args);
      }

   }

   private Console() {
   }
}
