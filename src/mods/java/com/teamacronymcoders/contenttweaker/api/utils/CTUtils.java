package com.teamacronymcoders.contenttweaker.api.utils;

import java.util.Locale;

public class CTUtils {
   public CTUtils() {
   }

   public static <T extends Enum<T>> T getEnum(String string, Class<T> enumType) {
      return (T)Enum.valueOf(enumType, string.toUpperCase(Locale.US));
   }
}
