package me.lucko.spark.lib.xz.lz;

import me.lucko.spark.lib.xz.ArrayCache;

final class BT4 extends LZEncoder {
   private final Hash234 hash;
   private final int[] tree;
   private final Matches matches;
   private final int depthLimit;
   private final int cyclicSize;
   private int cyclicPos = -1;
   private int lzPos;

   static int getMemoryUsage(int var0) {
      return Hash234.getMemoryUsage(var0) + var0 / 128 + 10;
   }

   BT4(int var1, int var2, int var3, int var4, int var5, int var6, ArrayCache var7) {
      super(var1, var2, var3, var4, var5, var7);
      this.cyclicSize = var1 + 1;
      this.lzPos = this.cyclicSize;
      this.hash = new Hash234(var1, var7);
      this.tree = var7.getIntArray(this.cyclicSize * 2, false);
      this.matches = new Matches(var4 - 1);
      this.depthLimit = var6 > 0 ? var6 : 16 + var4 / 2;
   }

   public void putArraysToCache(ArrayCache var1) {
      var1.putArray(this.tree);
      this.hash.putArraysToCache(var1);
      super.putArraysToCache(var1);
   }

   private int movePos() {
      int var1 = this.movePos(this.niceLen, 4);
      if (var1 != 0) {
         if (++this.lzPos == Integer.MAX_VALUE) {
            int var2 = Integer.MAX_VALUE - this.cyclicSize;
            this.hash.normalize(var2);
            normalize(this.tree, this.cyclicSize * 2, var2);
            this.lzPos -= var2;
         }

         if (++this.cyclicPos == this.cyclicSize) {
            this.cyclicPos = 0;
         }
      }

      return var1;
   }

   public Matches getMatches() {
      this.matches.count = 0;
      int var1 = this.matchLenMax;
      int var2 = this.niceLen;
      int var3 = this.movePos();
      if (var3 < var1) {
         if (var3 == 0) {
            return this.matches;
         }

         var1 = var3;
         if (var2 > var3) {
            var2 = var3;
         }
      }

      this.hash.calcHashes(this.buf, this.readPos);
      int var4 = this.lzPos - this.hash.getHash2Pos();
      int var5 = this.lzPos - this.hash.getHash3Pos();
      int var6 = this.hash.getHash4Pos();
      this.hash.updateTables(this.lzPos);
      int var7 = 0;
      if (var4 < this.cyclicSize && this.buf[this.readPos - var4] == this.buf[this.readPos]) {
         var7 = 2;
         this.matches.len[0] = 2;
         this.matches.dist[0] = var4 - 1;
         this.matches.count = 1;
      }

      if (var4 != var5 && var5 < this.cyclicSize && this.buf[this.readPos - var5] == this.buf[this.readPos]) {
         var7 = 3;
         this.matches.dist[this.matches.count++] = var5 - 1;
         var4 = var5;
      }

      if (this.matches.count > 0) {
         while(var7 < var1 && this.buf[this.readPos + var7 - var4] == this.buf[this.readPos + var7]) {
            ++var7;
         }

         this.matches.len[this.matches.count - 1] = var7;
         if (var7 >= var2) {
            this.skip(var2, var6);
            return this.matches;
         }
      }

      if (var7 < 3) {
         var7 = 3;
      }

      int var8 = this.depthLimit;
      int var9 = (this.cyclicPos << 1) + 1;
      int var10 = this.cyclicPos << 1;
      int var11 = 0;
      int var12 = 0;

      while(true) {
         int var13 = this.lzPos - var6;
         if (var8-- == 0 || var13 >= this.cyclicSize) {
            this.tree[var9] = 0;
            this.tree[var10] = 0;
            return this.matches;
         }

         int var14 = this.cyclicPos - var13 + (var13 > this.cyclicPos ? this.cyclicSize : 0) << 1;
         int var15 = Math.min(var11, var12);
         if (this.buf[this.readPos + var15 - var13] == this.buf[this.readPos + var15]) {
            while(true) {
               ++var15;
               if (var15 >= var1 || this.buf[this.readPos + var15 - var13] != this.buf[this.readPos + var15]) {
                  break;
               }
            }

            if (var15 > var7) {
               var7 = var15;
               this.matches.len[this.matches.count] = var15;
               this.matches.dist[this.matches.count] = var13 - 1;
               ++this.matches.count;
               if (var15 >= var2) {
                  this.tree[var10] = this.tree[var14];
                  this.tree[var9] = this.tree[var14 + 1];
                  return this.matches;
               }
            }
         }

         if ((this.buf[this.readPos + var15 - var13] & 255) < (this.buf[this.readPos + var15] & 255)) {
            this.tree[var10] = var6;
            var10 = var14 + 1;
            var6 = this.tree[var10];
            var12 = var15;
         } else {
            this.tree[var9] = var6;
            var9 = var14;
            var6 = this.tree[var14];
            var11 = var15;
         }
      }
   }

   private void skip(int var1, int var2) {
      int var3 = this.depthLimit;
      int var4 = (this.cyclicPos << 1) + 1;
      int var5 = this.cyclicPos << 1;
      int var6 = 0;
      int var7 = 0;

      while(true) {
         int var8 = this.lzPos - var2;
         if (var3-- == 0 || var8 >= this.cyclicSize) {
            this.tree[var4] = 0;
            this.tree[var5] = 0;
            return;
         }

         int var9 = this.cyclicPos - var8 + (var8 > this.cyclicPos ? this.cyclicSize : 0) << 1;
         int var10 = Math.min(var6, var7);
         if (this.buf[this.readPos + var10 - var8] == this.buf[this.readPos + var10]) {
            while(true) {
               ++var10;
               if (var10 == var1) {
                  this.tree[var5] = this.tree[var9];
                  this.tree[var4] = this.tree[var9 + 1];
                  return;
               }

               if (this.buf[this.readPos + var10 - var8] != this.buf[this.readPos + var10]) {
                  break;
               }
            }
         }

         if ((this.buf[this.readPos + var10 - var8] & 255) < (this.buf[this.readPos + var10] & 255)) {
            this.tree[var5] = var2;
            var5 = var9 + 1;
            var2 = this.tree[var5];
            var7 = var10;
         } else {
            this.tree[var4] = var2;
            var4 = var9;
            var2 = this.tree[var9];
            var6 = var10;
         }
      }
   }

   public void skip(int var1) {
      while(var1-- > 0) {
         int var2 = this.niceLen;
         int var3 = this.movePos();
         if (var3 < var2) {
            if (var3 == 0) {
               continue;
            }

            var2 = var3;
         }

         this.hash.calcHashes(this.buf, this.readPos);
         int var4 = this.hash.getHash4Pos();
         this.hash.updateTables(this.lzPos);
         this.skip(var2, var4);
      }

   }
}
