package me.lucko.spark.lib.xz.lz;

import java.io.DataInputStream;
import java.io.IOException;
import me.lucko.spark.lib.xz.ArrayCache;
import me.lucko.spark.lib.xz.CorruptedInputException;

public final class LZDecoder {
   private final byte[] buf;
   private final int bufSize;
   private int start = 0;
   private int pos = 0;
   private int full = 0;
   private int limit = 0;
   private int pendingLen = 0;
   private int pendingDist = 0;

   public LZDecoder(int var1, byte[] var2, ArrayCache var3) {
      this.bufSize = var1;
      this.buf = var3.getByteArray(this.bufSize, false);
      if (var2 != null) {
         this.pos = Math.min(var2.length, var1);
         this.full = this.pos;
         this.start = this.pos;
         System.arraycopy(var2, var2.length - this.pos, this.buf, 0, this.pos);
      }

   }

   public void putArraysToCache(ArrayCache var1) {
      var1.putArray(this.buf);
   }

   public void reset() {
      this.start = 0;
      this.pos = 0;
      this.full = 0;
      this.limit = 0;
      this.buf[this.bufSize - 1] = 0;
   }

   public void setLimit(int var1) {
      if (this.bufSize - this.pos <= var1) {
         this.limit = this.bufSize;
      } else {
         this.limit = this.pos + var1;
      }

   }

   public boolean hasSpace() {
      return this.pos < this.limit;
   }

   public boolean hasPending() {
      return this.pendingLen > 0;
   }

   public int getPos() {
      return this.pos;
   }

   public int getByte(int var1) {
      int var2 = this.pos - var1 - 1;
      if (var1 >= this.pos) {
         var2 += this.bufSize;
      }

      return this.buf[var2] & 255;
   }

   public void putByte(byte var1) {
      this.buf[this.pos++] = var1;
      if (this.full < this.pos) {
         this.full = this.pos;
      }

   }

   public void repeat(int var1, int var2) throws IOException {
      if (var1 >= 0 && var1 < this.full) {
         int var3 = Math.min(this.limit - this.pos, var2);
         this.pendingLen = var2 - var3;
         this.pendingDist = var1;
         int var4 = this.pos - var1 - 1;
         if (var1 >= this.pos) {
            var4 += this.bufSize;
         }

         while(true) {
            this.buf[this.pos++] = this.buf[var4++];
            if (var4 == this.bufSize) {
               var4 = 0;
            }

            --var3;
            if (var3 <= 0) {
               break;
            }
         }

         if (this.full < this.pos) {
            this.full = this.pos;
         }

      } else {
         throw new CorruptedInputException();
      }
   }

   public void repeatPending() throws IOException {
      if (this.pendingLen > 0) {
         this.repeat(this.pendingDist, this.pendingLen);
      }

   }

   public void copyUncompressed(DataInputStream var1, int var2) throws IOException {
      int var3 = Math.min(this.bufSize - this.pos, var2);
      var1.readFully(this.buf, this.pos, var3);
      this.pos += var3;
      if (this.full < this.pos) {
         this.full = this.pos;
      }

   }

   public int flush(byte[] var1, int var2) {
      int var3 = this.pos - this.start;
      if (this.pos == this.bufSize) {
         this.pos = 0;
      }

      System.arraycopy(this.buf, this.start, var1, var2, var3);
      this.start = this.pos;
      return var3;
   }
}
