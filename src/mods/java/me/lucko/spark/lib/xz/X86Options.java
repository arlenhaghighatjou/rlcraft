package me.lucko.spark.lib.xz;

import java.io.InputStream;
import me.lucko.spark.lib.xz.simple.X86;

public class X86Options extends BCJOptions {
   private static final int ALIGNMENT = 1;

   public X86Options() {
      super(1);
   }

   public FinishableOutputStream getOutputStream(FinishableOutputStream var1, ArrayCache var2) {
      return new SimpleOutputStream(var1, new X86(true, this.startOffset));
   }

   public InputStream getInputStream(InputStream var1, ArrayCache var2) {
      return new SimpleInputStream(var1, new X86(false, this.startOffset));
   }

   FilterEncoder getFilterEncoder() {
      return new BCJEncoder(this, 4L);
   }
}
