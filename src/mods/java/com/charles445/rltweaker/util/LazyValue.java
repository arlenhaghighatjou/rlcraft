package com.charles445.rltweaker.util;

import java.util.function.Supplier;

public class LazyValue<T> {
   private Supplier<T> factory;
   private T value;

   public LazyValue(Supplier<T> factory) {
      this.factory = factory;
   }

   public T get() {
      if (this.factory != null) {
         this.value = (T)this.factory.get();
         this.factory = null;
      }

      return this.value;
   }
}
