package me.lucko.spark.lib.xz.rangecoder;

import java.io.IOException;

public abstract class RangeEncoder extends RangeCoder {
   private static final int MOVE_REDUCING_BITS = 4;
   private static final int BIT_PRICE_SHIFT_BITS = 4;
   private static final int[] prices = new int[128];
   private long low;
   private int range;
   long cacheSize;
   private byte cache;

   public RangeEncoder() {
   }

   public void reset() {
      this.low = 0L;
      this.range = -1;
      this.cache = 0;
      this.cacheSize = 1L;
   }

   public int getPendingSize() {
      throw new Error();
   }

   public int finish() throws IOException {
      for(int var1 = 0; var1 < 5; ++var1) {
         this.shiftLow();
      }

      return -1;
   }

   abstract void writeByte(int var1) throws IOException;

   private void shiftLow() throws IOException {
      int var1 = (int)(this.low >>> 32);
      if (var1 != 0 || this.low < 4278190080L) {
         short var2 = this.cache;

         while(true) {
            this.writeByte(var2 + var1);
            var2 = 255;
            if (--this.cacheSize == 0L) {
               break;
            }
         }

         this.cache = (byte)((int)(this.low >>> 24));
      }

      ++this.cacheSize;
      this.low = (this.low & 16777215L) << 8;
   }

   public void encodeBit(short[] var1, int var2, int var3) throws IOException {
      short var4 = var1[var2];
      int var5 = (this.range >>> 11) * var4;
      if (var3 == 0) {
         this.range = var5;
         var1[var2] = (short)(var4 + (2048 - var4 >>> 5));
      } else {
         this.low += (long)var5 & 4294967295L;
         this.range -= var5;
         var1[var2] = (short)(var4 - (var4 >>> 5));
      }

      if ((this.range & -16777216) == 0) {
         this.range <<= 8;
         this.shiftLow();
      }

   }

   public static int getBitPrice(int var0, int var1) {
      assert var1 == 0 || var1 == 1;

      return prices[(var0 ^ -var1 & 2047) >>> 4];
   }

   public void encodeBitTree(short[] var1, int var2) throws IOException {
      int var3 = 1;
      int var4 = var1.length;

      while(true) {
         var4 >>>= 1;
         int var5 = var2 & var4;
         this.encodeBit(var1, var3, var5);
         var3 <<= 1;
         if (var5 != 0) {
            var3 |= 1;
         }

         if (var4 == 1) {
            break;
         }
      }

   }

   public static int getBitTreePrice(short[] var0, int var1) {
      int var2 = 0;
      var1 = var1 | var0.length;

      while(true) {
         int var3 = var1 & 1;
         var1 >>>= 1;
         var2 += getBitPrice(var0[var1], var3);
         if (var1 == 1) {
            break;
         }
      }

      return var2;
   }

   public void encodeReverseBitTree(short[] var1, int var2) throws IOException {
      int var3 = 1;
      var2 = var2 | var1.length;

      while(true) {
         int var4 = var2 & 1;
         var2 >>>= 1;
         this.encodeBit(var1, var3, var4);
         var3 = var3 << 1 | var4;
         if (var2 == 1) {
            break;
         }
      }

   }

   public static int getReverseBitTreePrice(short[] var0, int var1) {
      int var2 = 0;
      int var3 = 1;
      var1 = var1 | var0.length;

      while(true) {
         int var4 = var1 & 1;
         var1 >>>= 1;
         var2 += getBitPrice(var0[var3], var4);
         var3 = var3 << 1 | var4;
         if (var1 == 1) {
            break;
         }
      }

      return var2;
   }

   public void encodeDirectBits(int var1, int var2) throws IOException {
      while(true) {
         this.range >>>= 1;
         --var2;
         this.low += (long)(this.range & 0 - (var1 >>> var2 & 1));
         if ((this.range & -16777216) == 0) {
            this.range <<= 8;
            this.shiftLow();
         }

         if (var2 == 0) {
            break;
         }
      }

   }

   public static int getDirectBitsPrice(int var0) {
      return var0 << 4;
   }

   static {
      for(int var0 = 8; var0 < 2048; var0 += 16) {
         int var1 = var0;
         int var2 = 0;

         for(int var3 = 0; var3 < 4; ++var3) {
            var1 *= var1;

            for(var2 <<= 1; (var1 & -65536) != 0; ++var2) {
               var1 >>>= 1;
            }
         }

         prices[var0 >> 4] = 161 - var2;
      }

   }
}
