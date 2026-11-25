package net.silentchaos512.utils;

import java.util.function.Supplier;

public class Lazy<T> {
   private T value;
   private Supplier<T> supplier;

   public Lazy(Supplier<T> supplier) {
      this.supplier = supplier;
   }

   public static <T> Lazy<T> of(Supplier<T> supplier) {
      return new Lazy<T>(supplier);
   }

   public T get() {
      if (this.supplier != null) {
         this.value = (T)this.supplier.get();
         this.supplier = null;
      }

      return this.value;
   }
}
