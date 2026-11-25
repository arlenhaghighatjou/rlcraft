package me.lucko.spark.lib.xz;

import java.io.IOException;
import java.io.InputStream;

public abstract class FilterOptions implements Cloneable {
   public static int getEncoderMemoryUsage(FilterOptions[] var0) {
      int var1 = 0;

      for(int var2 = 0; var2 < var0.length; ++var2) {
         var1 += var0[var2].getEncoderMemoryUsage();
      }

      return var1;
   }

   public static int getDecoderMemoryUsage(FilterOptions[] var0) {
      int var1 = 0;

      for(int var2 = 0; var2 < var0.length; ++var2) {
         var1 += var0[var2].getDecoderMemoryUsage();
      }

      return var1;
   }

   public abstract int getEncoderMemoryUsage();

   public FinishableOutputStream getOutputStream(FinishableOutputStream var1) {
      return this.getOutputStream(var1, ArrayCache.getDefaultCache());
   }

   public abstract FinishableOutputStream getOutputStream(FinishableOutputStream var1, ArrayCache var2);

   public abstract int getDecoderMemoryUsage();

   public InputStream getInputStream(InputStream var1) throws IOException {
      return this.getInputStream(var1, ArrayCache.getDefaultCache());
   }

   public abstract InputStream getInputStream(InputStream var1, ArrayCache var2) throws IOException;

   abstract FilterEncoder getFilterEncoder();

   FilterOptions() {
   }
}
