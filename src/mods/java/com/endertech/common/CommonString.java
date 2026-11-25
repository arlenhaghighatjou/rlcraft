package com.endertech.common;

import java.util.Locale;
import java.util.StringJoiner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public abstract class CommonString {
   public static final String EMPTY = "";

   public CommonString() {
   }

   public static String capitalizeFirstChar(String string) {
      string = String.valueOf(string);
      if (string != null && string.length() > 0) {
         String firstChar = string.substring(0, 1).toUpperCase(Locale.ROOT);
         return firstChar + string.substring(1);
      } else {
         return string;
      }
   }

   public static String decapitalizeFirstChar(String string) {
      string = String.valueOf(string);
      if (string != null && string.length() > 0) {
         String firstChar = string.substring(0, 1).toLowerCase(Locale.ROOT);
         return firstChar + string.substring(1);
      } else {
         return string;
      }
   }

   public static String replaceFirst(String string, String target, String replacement) {
      return Pattern.compile(target, 16).matcher(string).replaceFirst(Matcher.quoteReplacement(replacement));
   }

   public static String quoted(String string) {
      return "'" + string + "'";
   }

   public static String doubleQuoted(String string) {
      return "\"" + string + "\"";
   }

   public static boolean isNullOrEmpty(String string) {
      return string == null || string.isEmpty();
   }

   public static boolean allCharsAreDigits(String string) {
      if (isNullOrEmpty(string)) {
         return false;
      } else {
         for(int i = 0; i < string.length(); ++i) {
            if (!Character.isDigit(string.charAt(i))) {
               return false;
            }
         }

         return true;
      }
   }

   public static class Joiner {
      protected final String delimiter;

      public Joiner(String delimiter) {
         this.delimiter = delimiter;
      }

      public static CommonString.Joiner with(String delimiter) {
         return new CommonString.Joiner(delimiter);
      }

      public String join(String... strings) {
         StringJoiner joiner = new StringJoiner(this.delimiter);

         for(String str : strings) {
            if (!str.isEmpty()) {
               joiner.add(str);
            }
         }

         return joiner.toString();
      }

      public String getDelimiter() {
         return this.delimiter;
      }
   }
}
