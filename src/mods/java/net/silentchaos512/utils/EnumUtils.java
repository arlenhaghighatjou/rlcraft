package net.silentchaos512.utils;

import java.util.function.Function;
import javax.annotation.Nullable;

public final class EnumUtils {
   private EnumUtils() {
      throw new IllegalAccessError("Utility class");
   }

   public static <E extends Enum<E>> E byIndex(int value, E defaultValue, Function<E, Integer> getter) {
      for(E e : (Enum[])defaultValue.getDeclaringClass().getEnumConstants()) {
         if (((Integer)getter.apply(e)).intValue() == value) {
            return e;
         }
      }

      return defaultValue;
   }

   public static <E extends Enum<E>> E byName(String name, E defaultValue) {
      for(E e : (Enum[])defaultValue.getDeclaringClass().getEnumConstants()) {
         if (e.name().equalsIgnoreCase(name)) {
            return e;
         }
      }

      return defaultValue;
   }

   public static <E extends Enum<E>> E byOrdinal(int ordinal, E defaultValue) {
      E[] enumConstants = defaultValue.getDeclaringClass().getEnumConstants();
      return (E)(ordinal >= 0 && ordinal < enumConstants.length ? enumConstants[ordinal] : defaultValue);
   }

   public static <E extends Enum<E>> boolean validate(@Nullable Object obj, Class<E> enumClass) {
      if (obj != null) {
         for(E e : (Enum[])enumClass.getEnumConstants()) {
            if (e.name().equalsIgnoreCase(obj.toString())) {
               return true;
            }
         }
      }

      return false;
   }
}
