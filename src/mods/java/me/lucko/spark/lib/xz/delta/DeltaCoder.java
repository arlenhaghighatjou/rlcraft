package me.lucko.spark.lib.xz.delta;

abstract class DeltaCoder {
   static final int DISTANCE_MIN = 1;
   static final int DISTANCE_MAX = 256;
   static final int DISTANCE_MASK = 255;
   final int distance;
   final byte[] history = new byte[256];
   int pos = 0;

   DeltaCoder(int var1) {
      if (var1 >= 1 && var1 <= 256) {
         this.distance = var1;
      } else {
         throw new IllegalArgumentException();
      }
   }
}
