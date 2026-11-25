package me.lucko.spark.lib.xz.simple;

public final class ARMThumb implements SimpleFilter {
   private final boolean isEncoder;
   private int pos;

   public ARMThumb(boolean var1, int var2) {
      this.isEncoder = var1;
      this.pos = var2 + 4;
   }

   public int code(byte[] var1, int var2, int var3) {
      int var4 = var2 + var3 - 4;

      int var5;
      for(var5 = var2; var5 <= var4; var5 += 2) {
         if ((var1[var5 + 1] & 248) == 240 && (var1[var5 + 3] & 248) == 248) {
            int var6 = (var1[var5 + 1] & 7) << 19 | (var1[var5] & 255) << 11 | (var1[var5 + 3] & 7) << 8 | var1[var5 + 2] & 255;
            var6 = var6 << 1;
            int var7;
            if (this.isEncoder) {
               var7 = var6 + (this.pos + var5 - var2);
            } else {
               var7 = var6 - (this.pos + var5 - var2);
            }

            var7 = var7 >>> 1;
            var1[var5 + 1] = (byte)(240 | var7 >>> 19 & 7);
            var1[var5] = (byte)(var7 >>> 11);
            var1[var5 + 3] = (byte)(248 | var7 >>> 8 & 7);
            var1[var5 + 2] = (byte)var7;
            var5 += 2;
         }
      }

      var5 = var5 - var2;
      this.pos += var5;
      return var5;
   }
}
