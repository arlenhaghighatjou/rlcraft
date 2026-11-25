package me.lucko.spark.lib.xz;

import java.io.InputStream;
import me.lucko.spark.lib.xz.simple.IA64;

public class IA64Options extends BCJOptions {
   private static final int ALIGNMENT = 16;

   public IA64Options() {
      super(16);
   }

   public FinishableOutputStream getOutputStream(FinishableOutputStream var1, ArrayCache var2) {
      return new SimpleOutputStream(var1, new IA64(true, this.startOffset));
   }

   public InputStream getInputStream(InputStream var1, ArrayCache var2) {
      return new SimpleInputStream(var1, new IA64(false, this.startOffset));
   }

   FilterEncoder getFilterEncoder() {
      return new BCJEncoder(this, 6L);
   }
}
