package me.lucko.spark.lib.xz;

import java.io.InputStream;
import me.lucko.spark.lib.xz.simple.PowerPC;

public class PowerPCOptions extends BCJOptions {
   private static final int ALIGNMENT = 4;

   public PowerPCOptions() {
      super(4);
   }

   public FinishableOutputStream getOutputStream(FinishableOutputStream var1, ArrayCache var2) {
      return new SimpleOutputStream(var1, new PowerPC(true, this.startOffset));
   }

   public InputStream getInputStream(InputStream var1, ArrayCache var2) {
      return new SimpleInputStream(var1, new PowerPC(false, this.startOffset));
   }

   FilterEncoder getFilterEncoder() {
      return new BCJEncoder(this, 5L);
   }
}
