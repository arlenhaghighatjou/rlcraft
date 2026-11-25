package ivorius.reccomplex.utils.algebra;

import java.util.function.Supplier;

public class SupplierCache<T> {
   private Supplier<? extends T> supplier;
   private T t;

   private SupplierCache(Supplier<? extends T> supplier) {
      this.supplier = supplier;
   }

   public static <T> SupplierCache<T> of(Supplier<? extends T> supplier) {
      return new SupplierCache<T>(supplier);
   }

   public static <T> SupplierCache<T> direct(T t) {
      return new SupplierCache<T>(() -> {
         return (SupplierCache<T>)t;
      });
   }

   public T get() {
      return (T)(this.t != null ? this.t : (this.t = (T)this.supplier.get()));
   }
}
