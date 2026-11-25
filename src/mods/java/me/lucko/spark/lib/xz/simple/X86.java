package me.lucko.spark.lib.xz.simple;

public final class X86 implements SimpleFilter {
   private static final boolean[] MASK_TO_ALLOWED_STATUS = new boolean[]{true, true, true, false, true, false, false, false};
   private static final int[] MASK_TO_BIT_NUMBER = new int[]{0, 1, 2, 2, 3, 3, 3, 3};
   private final boolean isEncoder;
   private int pos;
   private int prevMask = 0;

   private static boolean test86MSByte(byte var0) {
      int var1 = var0 & 255;
      return var1 == 0 || var1 == 255;
   }

   public X86(boolean var1, int var2) {
      this.isEncoder = var1;
      this.pos = var2 + 5;
   }

   public int code(byte[] var1, int var2, int var3) {
      int var4 = var2 - 1;
      int var5 = var2 + var3 - 5;

      int var6;
      for(var6 = var2; var6 <= var5; ++var6) {
         if ((var1[var6] & 254) == 232) {
            var4 = var6 - var4;
            if ((var4 & -4) != 0) {
               this.prevMask = 0;
            } else {
               this.prevMask = this.prevMask << var4 - 1 & 7;
               if (this.prevMask != 0 && (!MASK_TO_ALLOWED_STATUS[this.prevMask] || test86MSByte(var1[var6 + 4 - MASK_TO_BIT_NUMBER[this.prevMask]]))) {
                  var4 = var6;
                  this.prevMask = this.prevMask << 1 | 1;
                  continue;
               }
            }

            var4 = var6;
            if (!test86MSByte(var1[var6 + 4])) {
               this.prevMask = this.prevMask << 1 | 1;
            } else {
               int var7 = var1[var6 + 1] & 255 | (var1[var6 + 2] & 255) << 8 | (var1[var6 + 3] & 255) << 16 | (var1[var6 + 4] & 255) << 24;

               int var8;
               while(true) {
                  if (this.isEncoder) {
                     var8 = var7 + (this.pos + var6 - var2);
                  } else {
                     var8 = var7 - (this.pos + var6 - var2);
                  }

                  if (this.prevMask == 0) {
                     break;
                  }

                  int var9 = MASK_TO_BIT_NUMBER[this.prevMask] * 8;
                  if (!test86MSByte((byte)(var8 >>> 24 - var9))) {
                     break;
                  }

                  var7 = var8 ^ (1 << 32 - var9) - 1;
               }

               var1[var6 + 1] = (byte)var8;
               var1[var6 + 2] = (byte)(var8 >>> 8);
               var1[var6 + 3] = (byte)(var8 >>> 16);
               var1[var6 + 4] = (byte)(~((var8 >>> 24 & 1) - 1));
               var6 += 4;
            }
         }
      }

      var4 = var6 - var4;
      this.prevMask = (var4 & -4) != 0 ? 0 : this.prevMask << var4 - 1;
      var6 = var6 - var2;
      this.pos += var6;
      return var6;
   }
}
