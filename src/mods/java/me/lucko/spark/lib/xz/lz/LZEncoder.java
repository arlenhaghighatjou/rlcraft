package me.lucko.spark.lib.xz.lz;

import java.io.IOException;
import java.io.OutputStream;
import me.lucko.spark.lib.xz.ArrayCache;

public abstract class LZEncoder {
   public static final int MF_HC4 = 4;
   public static final int MF_BT4 = 20;
   private final int keepSizeBefore;
   private final int keepSizeAfter;
   final int matchLenMax;
   final int niceLen;
   final byte[] buf;
   final int bufSize;
   int readPos = -1;
   private int readLimit = -1;
   private boolean finishing = false;
   private int writePos = 0;
   private int pendingSize = 0;

   static void normalize(int[] var0, int var1, int var2) {
      for(int var3 = 0; var3 < var1; ++var3) {
         if (var0[var3] <= var2) {
            var0[var3] = 0;
         } else {
            var0[var3] -= var2;
         }
      }

   }

   private static int getBufSize(int var0, int var1, int var2, int var3) {
      int var4 = var1 + var0;
      int var5 = var2 + var3;
      int var6 = Math.min(var0 / 2 + 262144, 536870912);
      return var4 + var5 + var6;
   }

   public static int getMemoryUsage(int var0, int var1, int var2, int var3, int var4) {
      int var5 = getBufSize(var0, var1, var2, var3) / 1024 + 10;
      switch(var4) {
      case 4:
         var5 = var5 + HC4.getMemoryUsage(var0);
         break;
      case 20:
         var5 = var5 + BT4.getMemoryUsage(var0);
         break;
      default:
         throw new IllegalArgumentException();
      }

      return var5;
   }

   public static LZEncoder getInstance(int var0, int var1, int var2, int var3, int var4, int var5, int var6, ArrayCache var7) {
      switch(var5) {
      case 4:
         return new HC4(var0, var1, var2, var3, var4, var6, var7);
      case 20:
         return new BT4(var0, var1, var2, var3, var4, var6, var7);
      default:
         throw new IllegalArgumentException();
      }
   }

   LZEncoder(int var1, int var2, int var3, int var4, int var5, ArrayCache var6) {
      this.bufSize = getBufSize(var1, var2, var3, var5);
      this.buf = var6.getByteArray(this.bufSize, false);
      this.keepSizeBefore = var2 + var1;
      this.keepSizeAfter = var3 + var5;
      this.matchLenMax = var5;
      this.niceLen = var4;
   }

   public void putArraysToCache(ArrayCache var1) {
      var1.putArray(this.buf);
   }

   public void setPresetDict(int var1, byte[] var2) {
      assert !this.isStarted();

      assert this.writePos == 0;

      if (var2 != null) {
         int var3 = Math.min(var2.length, var1);
         int var4 = var2.length - var3;
         System.arraycopy(var2, var4, this.buf, 0, var3);
         this.writePos += var3;
         this.skip(var3);
      }

   }

   private void moveWindow() {
      int var1 = this.readPos + 1 - this.keepSizeBefore & -16;
      int var2 = this.writePos - var1;
      System.arraycopy(this.buf, var1, this.buf, 0, var2);
      this.readPos -= var1;
      this.readLimit -= var1;
      this.writePos -= var1;
   }

   public int fillWindow(byte[] var1, int var2, int var3) {
      assert !this.finishing;

      if (this.readPos >= this.bufSize - this.keepSizeAfter) {
         this.moveWindow();
      }

      if (var3 > this.bufSize - this.writePos) {
         var3 = this.bufSize - this.writePos;
      }

      System.arraycopy(var1, var2, this.buf, this.writePos, var3);
      this.writePos += var3;
      if (this.writePos >= this.keepSizeAfter) {
         this.readLimit = this.writePos - this.keepSizeAfter;
      }

      this.processPendingBytes();
      return var3;
   }

   private void processPendingBytes() {
      if (this.pendingSize > 0 && this.readPos < this.readLimit) {
         this.readPos -= this.pendingSize;
         int var1 = this.pendingSize;
         this.pendingSize = 0;
         this.skip(var1);

         assert this.pendingSize < var1;
      }

   }

   public boolean isStarted() {
      return this.readPos != -1;
   }

   public void setFlushing() {
      this.readLimit = this.writePos - 1;
      this.processPendingBytes();
   }

   public void setFinishing() {
      this.readLimit = this.writePos - 1;
      this.finishing = true;
      this.processPendingBytes();
   }

   public boolean hasEnoughData(int var1) {
      return this.readPos - var1 < this.readLimit;
   }

   public void copyUncompressed(OutputStream var1, int var2, int var3) throws IOException {
      var1.write(this.buf, this.readPos + 1 - var2, var3);
   }

   public int getAvail() {
      assert this.isStarted();

      return this.writePos - this.readPos;
   }

   public int getPos() {
      return this.readPos;
   }

   public int getByte(int var1) {
      return this.buf[this.readPos - var1] & 255;
   }

   public int getByte(int var1, int var2) {
      return this.buf[this.readPos + var1 - var2] & 255;
   }

   public int getMatchLen(int var1, int var2) {
      int var3 = this.readPos - var1 - 1;

      int var4;
      for(var4 = 0; var4 < var2 && this.buf[this.readPos + var4] == this.buf[var3 + var4]; ++var4) {
         ;
      }

      return var4;
   }

   public int getMatchLen(int var1, int var2, int var3) {
      int var4 = this.readPos + var1;
      int var5 = var4 - var2 - 1;

      int var6;
      for(var6 = 0; var6 < var3 && this.buf[var4 + var6] == this.buf[var5 + var6]; ++var6) {
         ;
      }

      return var6;
   }

   public boolean verifyMatches(Matches var1) {
      int var2 = Math.min(this.getAvail(), this.matchLenMax);

      for(int var3 = 0; var3 < var1.count; ++var3) {
         if (this.getMatchLen(var1.dist[var3], var2) != var1.len[var3]) {
            return false;
         }
      }

      return true;
   }

   int movePos(int var1, int var2) {
      assert var1 >= var2;

      ++this.readPos;
      int var3 = this.writePos - this.readPos;
      if (var3 < var1 && (var3 < var2 || !this.finishing)) {
         ++this.pendingSize;
         var3 = 0;
      }

      return var3;
   }

   public abstract Matches getMatches();

   public abstract void skip(int var1);
}
