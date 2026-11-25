package me.lucko.spark.lib.xz.simple;

public final class SPARC implements SimpleFilter {
   private final boolean isEncoder;
   private int pos;

   public SPARC(boolean var1, int var2) {
      this.isEncoder = var1;
      this.pos = var2;
   }

   public int code(byte[] var1, int var2, int var3) {
      int var4 = var2 + var3 - 4;

      int var5;
      for(var5 = var2; var5 <= var4; var5 += 4) {
         if (var1[var5] == 64 && (var1[var5 + 1] & 192) == 0 || var1[var5] == 127 && (var1[var5 + 1] & 192) == 192) {
            int var6 = (var1[var5] & 255) << 24 | (var1[var5 + 1] & 255) << 16 | (var1[var5 + 2] & 255) << 8 | var1[var5 + 3] & 255;
            var6 = var6 << 2;
            int var7;
            if (this.isEncoder) {
               var7 = var6 + (this.pos + var5 - var2);
            } else {
               var7 = var6 - (this.pos + var5 - var2);
            }

            var7 = var7 >>> 2;
            var7 = 0 - (var7 >>> 22 & 1) << 22 & 1073741823 | var7 & 4194303 | 1073741824;
            var1[var5] = (byte)(var7 >>> 24);
            var1[var5 + 1] = (byte)(var7 >>> 16);
            var1[var5 + 2] = (byte)(var7 >>> 8);
            var1[var5 + 3] = (byte)var7;
         }
      }

      var5 = var5 - var2;
      this.pos += var5;
      return var5;
   }
}
