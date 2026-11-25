package ivorius.reccomplex.utils.optional;

import java.util.Optional;

public class IvOptional {
   public IvOptional() {
   }

   public static void ifAbsent(Optional<?> optional, Runnable run) {
      if (!optional.isPresent()) {
         run.run();
      }

   }

   public static <T> Optional<T> replace(Optional<T> optional, Optional<T> replace) {
      return replace.isPresent() ? replace : optional;
   }
}
