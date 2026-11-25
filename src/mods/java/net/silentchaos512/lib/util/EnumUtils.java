package net.silentchaos512.lib.util;

import java.util.Optional;
import java.util.function.Function;

public final class EnumUtils {
   private EnumUtils() {
      throw new IllegalAccessError("Utility class");
   }

   public static <T extends Enum<T>> Optional<T> fromString(Class<T> enumClass, String value) {
      return fromString(enumClass, value, true);
   }

   public static <T extends Enum<T>> Optional<T> fromString(Class<T> enumClass, String value, boolean ignoreCase) {
      for(T t : (Enum[])enumClass.getEnumConstants()) {
         if (ignoreCase && t.name().equalsIgnoreCase(value) || t.name().equals(value)) {
            return Optional.of(t);
         }
      }

      return Optional.empty();
   }

   public static <T extends Enum<T>> Optional<T> fromIndex(Class<T> enumClass, int value, Function<T, Integer> getter) {
      for(T t : (Enum[])enumClass.getEnumConstants()) {
         if (((Integer)getter.apply(t)).intValue() == value) {
            return Optional.of(t);
         }
      }

      return Optional.empty();
   }

   public static <T extends Enum<T>> Optional<T> fromOrdinal(Class<T> enumClass, int value) {
      return value >= 0 && value < ((Enum[])enumClass.getEnumConstants()).length ? Optional.of(((Enum[])enumClass.getEnumConstants())[value]) : Optional.empty();
   }
}
