package me.lucko.spark.lib.xz;

import java.io.InputStream;
import me.lucko.spark.lib.xz.simple.ARM;

public class ARMOptions extends BCJOptions {
   private static final int ALIGNMENT = 4;

   public ARMOptions() {
      super(4);
   }

   public FinishableOutputStream getOutputStream(FinishableOutputStream var1, ArrayCache var2) {
      return new SimpleOutputStream(var1, new ARM(true, this.startOffset));
   }

   public InputStream getInputStream(InputStream var1, ArrayCache var2) {
      return new SimpleInputStream(var1, new ARM(false, this.startOffset));
   }

   FilterEncoder getFilterEncoder() {
      return new BCJEncoder(this, 7L);
   }
}
