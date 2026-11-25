package com.teamacronymcoders.base.util;

import java.util.Arrays;
import java.util.function.Function;
import java.util.function.IntFunction;

public class EnumUtils {
   private EnumUtils() {
   }

   public static String[] getNames(Class<? extends Enum<?>> enumClass) {
      String[] enumNames = (String[])Arrays.stream(enumClass.getEnumConstants()).map(Enum::name).toArray((x$0) -> {
         return new String[x$0];
      });

      for(int i = 0; i < enumNames.length; ++i) {
         enumNames[i] = enumNames[i].toLowerCase();
      }

      return enumNames;
   }
}
