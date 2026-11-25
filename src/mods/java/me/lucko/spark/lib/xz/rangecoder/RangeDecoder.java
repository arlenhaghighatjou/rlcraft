package me.lucko.spark.lib.xz.rangecoder;

import java.io.IOException;

public abstract class RangeDecoder extends RangeCoder {
   int range = 0;
   int code = 0;

   public RangeDecoder() {
   }

   public abstract void normalize() throws IOException;

   public int decodeBit(short[] var1, int var2) throws IOException {
      this.normalize();
      short var3 = var1[var2];
      int var4 = (this.range >>> 11) * var3;
      byte var5;
      if ((this.code ^ Integer.MIN_VALUE) < (var4 ^ Integer.MIN_VALUE)) {
         this.range = var4;
         var1[var2] = (short)(var3 + (2048 - var3 >>> 5));
         var5 = 0;
      } else {
         this.range -= var4;
         this.code -= var4;
         var1[var2] = (short)(var3 - (var3 >>> 5));
         var5 = 1;
      }

      return var5;
   }

   public int decodeBitTree(short[] var1) throws IOException {
      int var2 = 1;

      while(true) {
         var2 = var2 << 1 | this.decodeBit(var1, var2);
         if (var2 >= var1.length) {
            break;
         }
      }

      return var2 - var1.length;
   }

   public int decodeReverseBitTree(short[] var1) throws IOException {
      int var2 = 1;
      int var3 = 0;
      int var4 = 0;

      while(true) {
         int var5 = this.decodeBit(var1, var2);
         var2 = var2 << 1 | var5;
         var4 |= var5 << var3++;
         if (var2 >= var1.length) {
            break;
         }
      }

      return var4;
   }

   public int decodeDirectBits(int var1) throws IOException {
      int var2 = 0;

      while(true) {
         this.normalize();
         this.range >>>= 1;
         int var3 = this.code - this.range >>> 31;
         this.code -= this.range & var3 - 1;
         var2 = var2 << 1 | 1 - var3;
         --var1;
         if (var1 == 0) {
            break;
         }
      }

      return var2;
   }
}
