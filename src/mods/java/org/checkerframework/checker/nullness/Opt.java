package org.checkerframework.checker.nullness;

import java.util.NoSuchElementException;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import org.checkerframework.checker.nullness.qual.EnsuresNonNullIf;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

public final class Opt {
   private Opt() {
      throw new AssertionError("shouldn't be instantiated");
   }

   @NonNull
   public static <T> T get(T primary) {
      if (primary == null) {
         throw new NoSuchElementException("No value present");
      } else {
         return primary;
      }
   }

   @EnsuresNonNullIf(
      expression = {"#1"},
      result = true
   )
   public static boolean isPresent(@Nullable Object primary) {
      return primary != null;
   }

   public static <T> void ifPresent(T primary, Consumer<? super T> consumer) {
      if (primary != null) {
         consumer.accept(primary);
      }

   }

   @Nullable
   public static <T> T filter(T primary, Predicate<? super T> predicate) {
      if (primary == null) {
         return (T)null;
      } else {
         return (T)(predicate.test(primary) ? primary : null);
      }
   }

   @Nullable
   public static <T, U> U map(T primary, Function<? super T, ? extends U> mapper) {
      return (U)(primary == null ? null : mapper.apply(primary));
   }

   @NonNull
   public static <T> T orElse(T primary, @NonNull T other) {
      return (T)(primary != null ? primary : other);
   }

   @NonNull
   public static <T> T orElseGet(T primary, Supplier<? extends T> other) {
      return (T)(primary != null ? primary : other.get());
   }

   @NonNull
   public static <T, X extends Throwable> T orElseThrow(T primary, Supplier<? extends X> exceptionSupplier) throws X {
      if (primary != null) {
         return primary;
      } else {
         throw (Throwable)exceptionSupplier.get();
      }
   }
}
