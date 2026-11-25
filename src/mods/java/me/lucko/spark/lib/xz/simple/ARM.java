package me.lucko.spark.lib.xz.simple;

public final class ARM implements SimpleFilter {
   private final boolean isEncoder;
   private int pos;

   public ARM(boolean var1, int var2) {
      this.isEncoder = var1;
      this.pos = var2 + 8;
   }

   public int code(byte[] var1, int var2, int var3) {
      int var4 = var2 + var3 - 4;

      int var5;
      for(var5 = var2; var5 <= var4; var5 += 4) {
         if ((var1[var5 + 3] & 255) == 235) {
            int var6 = (var1[var5 + 2] & 255) << 16 | (var1[var5 + 1] & 255) << 8 | var1[var5] & 255;
            var6 = var6 << 2;
            int var7;
            if (this.isEncoder) {
               var7 = var6 + (this.pos + var5 - var2);
            } else {
               var7 = var6 - (this.pos + var5 - var2);
            }

            var7 = var7 >>> 2;
            var1[var5 + 2] = (byte)(var7 >>> 16);
            var1[var5 + 1] = (byte)(var7 >>> 8);
            var1[var5] = (byte)var7;
         }
      }

      var5 = var5 - var2;
      this.pos += var5;
      return var5;
   }
}
