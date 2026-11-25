package betterquesting.api2.client.gui.controls.io;

import betterquesting.api2.client.gui.controls.IValueIO;
import java.util.concurrent.Callable;

public class ValueFuncIO<T> implements IValueIO<T> {
   private final Callable<T> v;

   public ValueFuncIO(Callable<T> value) {
      this.v = value;
   }

   public T readValue() {
      try {
         return (T)this.v.call();
      } catch (Exception var2) {
         var2.printStackTrace();
         return (T)null;
      }
   }

   public void writeValue(T value) {
   }

   public T readValueRaw() {
      return (T)this.readValue();
   }

   public void writeValueRaw(T value) {
   }
}
