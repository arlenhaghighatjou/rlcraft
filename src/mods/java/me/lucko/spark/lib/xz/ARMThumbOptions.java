package me.lucko.spark.lib.xz;

import java.io.InputStream;
import me.lucko.spark.lib.xz.simple.ARMThumb;

public class ARMThumbOptions extends BCJOptions {
   private static final int ALIGNMENT = 2;

   public ARMThumbOptions() {
      super(2);
   }

   public FinishableOutputStream getOutputStream(FinishableOutputStream var1, ArrayCache var2) {
      return new SimpleOutputStream(var1, new ARMThumb(true, this.startOffset));
   }

   public InputStream getInputStream(InputStream var1, ArrayCache var2) {
      return new SimpleInputStream(var1, new ARMThumb(false, this.startOffset));
   }

   FilterEncoder getFilterEncoder() {
      return new BCJEncoder(this, 8L);
   }
}
