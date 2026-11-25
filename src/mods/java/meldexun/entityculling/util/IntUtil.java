package meldexun.entityculling.util;

public class IntUtil {
   public IntUtil() {
   }

   public static boolean anyMatch(int x1, int x2, int y1, int y2, IntUtil.BiIntPredicate predicate) {
      for(int x = x1; x <= x2; ++x) {
         for(int y = y1; y <= y2; ++y) {
            if (predicate.test(x, y)) {
               return true;
            }
         }
      }

      return false;
   }

   public static void forEach(int x1, int x2, int y1, int y2, IntUtil.BiIntConsumer consumer) {
      for(int x = x1; x <= x2; ++x) {
         for(int y = y1; y <= y2; ++y) {
            consumer.accept(x, y);
         }
      }

   }

   @FunctionalInterface
   public interface BiIntConsumer {
      void accept(int var1, int var2);
   }

   @FunctionalInterface
   public interface BiIntPredicate {
      boolean test(int var1, int var2);
   }
}
