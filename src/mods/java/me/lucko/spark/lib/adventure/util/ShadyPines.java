package me.lucko.spark.lib.adventure.util;

import java.util.Set;
import org.jetbrains.annotations.NotNull;

public final class ShadyPines {
   private ShadyPines() {
   }

   /** @deprecated */
   @Deprecated
   @SafeVarargs
   @NotNull
   public static <E extends Enum<E>> Set<E> enumSet(Class<E> type, @NotNull E... constants) {
      return MonkeyBars.<E>enumSet(type, constants);
   }

   public static boolean equals(double a, double b) {
      return Double.doubleToLongBits(a) == Double.doubleToLongBits(b);
   }

   public static boolean equals(float a, float b) {
      return Float.floatToIntBits(a) == Float.floatToIntBits(b);
   }
}
