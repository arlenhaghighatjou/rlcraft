package com.ferreusveritas.dynamictrees.util;

import java.util.function.Supplier;

@FunctionalInterface
public interface LazyValue<T> {
   T get();

   static <T> LazyValue<T> of(T value) {
      return () -> {
         return (LazyValue<T>)value;
      };
   }

   static <T> LazyValue<T> supplied(Supplier<T> supplier) {
      return new SuppliedLazyValue<T>(supplier);
   }
}
