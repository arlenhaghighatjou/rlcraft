package org.orecruncher.lib.streams;

import java.util.Iterator;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;
import javax.annotation.Nonnull;

public final class StreamUtil {
   private StreamUtil() {
   }

   @Nonnull
   public static <T> Stream<T> asStream(@Nonnull Iterator<T> iterator) {
      return asStream(iterator, false);
   }

   @Nonnull
   public static <T> Stream<T> asStream(@Nonnull Iterator<T> iterator, boolean parallell) {
      Iterable<T> iterable = () -> {
         return iterator;
      };
      return StreamSupport.stream(iterable.spliterator(), parallell);
   }
}
