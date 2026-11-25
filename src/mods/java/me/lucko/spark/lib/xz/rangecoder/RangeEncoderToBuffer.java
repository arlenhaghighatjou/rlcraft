package me.lucko.spark.lib.xz.rangecoder;

import java.io.IOException;
import java.io.OutputStream;
import me.lucko.spark.lib.xz.ArrayCache;

public final class RangeEncoderToBuffer extends RangeEncoder {
   private final byte[] buf;
   private int bufPos;

   public RangeEncoderToBuffer(int var1, ArrayCache var2) {
      this.buf = var2.getByteArray(var1, false);
      this.reset();
   }

   public void putArraysToCache(ArrayCache var1) {
      var1.putArray(this.buf);
   }

   public void reset() {
      super.reset();
      this.bufPos = 0;
   }

   public int getPendingSize() {
      return this.bufPos + (int)this.cacheSize + 5 - 1;
   }

   public int finish() {
      try {
         super.finish();
      } catch (IOException var2) {
         throw new Error();
      }

      return this.bufPos;
   }

   public void write(OutputStream var1) throws IOException {
      var1.write(this.buf, 0, this.bufPos);
   }

   void writeByte(int var1) {
      this.buf[this.bufPos++] = (byte)var1;
   }
}
