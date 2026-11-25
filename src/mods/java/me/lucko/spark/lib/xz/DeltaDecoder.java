package me.lucko.spark.lib.xz;

import java.io.InputStream;

class DeltaDecoder extends DeltaCoder implements FilterDecoder {
   private final int distance;

   DeltaDecoder(byte[] var1) throws UnsupportedOptionsException {
      if (var1.length != 1) {
         throw new UnsupportedOptionsException("Unsupported Delta filter properties");
      } else {
         this.distance = (var1[0] & 255) + 1;
      }
   }

   public int getMemoryUsage() {
      return 1;
   }

   public InputStream getInputStream(InputStream var1, ArrayCache var2) {
      return new DeltaInputStream(var1, this.distance);
   }
}
