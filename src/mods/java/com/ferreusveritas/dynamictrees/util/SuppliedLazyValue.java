package com.ferreusveritas.dynamictrees.util;

import java.util.function.Supplier;

public final class SuppliedLazyValue<T> implements LazyValue<T> {
   private T object;
   private final Supplier<T> supplier;

   SuppliedLazyValue(Supplier<T> supplier) {
      this.supplier = supplier;
   }

   public T get() {
      if (this.object == null) {
         this.object = (T)this.supplier.get();
      }

      return this.object;
   }
}
