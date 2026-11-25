package me.lucko.spark.lib.xz;

import java.io.InputStream;

public class DeltaOptions extends FilterOptions {
   public static final int DISTANCE_MIN = 1;
   public static final int DISTANCE_MAX = 256;
   private int distance = 1;

   public DeltaOptions() {
   }

   public DeltaOptions(int var1) throws UnsupportedOptionsException {
      this.setDistance(var1);
   }

   public void setDistance(int var1) throws UnsupportedOptionsException {
      if (var1 >= 1 && var1 <= 256) {
         this.distance = var1;
      } else {
         throw new UnsupportedOptionsException("Delta distance must be in the range [1, 256]: " + var1);
      }
   }

   public int getDistance() {
      return this.distance;
   }

   public int getEncoderMemoryUsage() {
      return DeltaOutputStream.getMemoryUsage();
   }

   public FinishableOutputStream getOutputStream(FinishableOutputStream var1, ArrayCache var2) {
      return new DeltaOutputStream(var1, this);
   }

   public int getDecoderMemoryUsage() {
      return 1;
   }

   public InputStream getInputStream(InputStream var1, ArrayCache var2) {
      return new DeltaInputStream(var1, this.distance);
   }

   FilterEncoder getFilterEncoder() {
      return new DeltaEncoder(this);
   }

   public Object clone() {
      try {
         return super.clone();
      } catch (CloneNotSupportedException var2) {
         assert false;

         throw new RuntimeException();
      }
   }
}
