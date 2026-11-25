package org.spongepowered.asm.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public abstract class JavaVersion {
   public static final double JAVA_6 = 1.6D;
   public static final double JAVA_7 = 1.7D;
   public static final double JAVA_8 = 1.8D;
   public static final double JAVA_9 = 9.0D;
   public static final double JAVA_10 = 10.0D;
   public static final double JAVA_11 = 11.0D;
   public static final double JAVA_12 = 12.0D;
   public static final double JAVA_13 = 13.0D;
   public static final double JAVA_14 = 14.0D;
   public static final double JAVA_15 = 15.0D;
   public static final double JAVA_16 = 16.0D;
   public static final double JAVA_17 = 17.0D;
   public static final double JAVA_18 = 18.0D;
   private static double current = 0.0D;

   private JavaVersion() {
   }

   public static double current() {
      if (current == 0.0D) {
         current = resolveCurrentVersion();
      }

      return current;
   }

   private static double resolveCurrentVersion() {
      String version = System.getProperty("java.version");
      Matcher decimalMatcher = Pattern.compile("[0-9]+\\.[0-9]+").matcher(version);
      if (decimalMatcher.find()) {
         return Double.parseDouble(decimalMatcher.group());
      } else {
         Matcher numberMatcher = Pattern.compile("[0-9]+").matcher(version);
         return numberMatcher.find() ? Double.parseDouble(numberMatcher.group()) : 1.6D;
      }
   }
}
