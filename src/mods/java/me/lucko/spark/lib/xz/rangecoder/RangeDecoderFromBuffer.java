package me.lucko.spark.lib.xz.rangecoder;

import java.io.DataInputStream;
import java.io.IOException;
import me.lucko.spark.lib.xz.ArrayCache;
import me.lucko.spark.lib.xz.CorruptedInputException;

public final class RangeDecoderFromBuffer extends RangeDecoder {
   private static final int INIT_SIZE = 5;
   private final byte[] buf;
   private int pos;

   public RangeDecoderFromBuffer(int var1, ArrayCache var2) {
      this.buf = var2.getByteArray(var1 - 5, false);
      this.pos = this.buf.length;
   }

   public void putArraysToCache(ArrayCache var1) {
      var1.putArray(this.buf);
   }

   public void prepareInputBuffer(DataInputStream var1, int var2) throws IOException {
      if (var2 < 5) {
         throw new CorruptedInputException();
      } else if (var1.readUnsignedByte() != 0) {
         throw new CorruptedInputException();
      } else {
         this.code = var1.readInt();
         this.range = -1;
         var2 = var2 - 5;
         this.pos = this.buf.length - var2;
         var1.readFully(this.buf, this.pos, var2);
      }
   }

   public boolean isFinished() {
      return this.pos == this.buf.length && this.code == 0;
   }

   public void normalize() throws IOException {
      if ((this.range & -16777216) == 0) {
         try {
            this.code = this.code << 8 | this.buf[this.pos++] & 255;
            this.range <<= 8;
         } catch (ArrayIndexOutOfBoundsException var2) {
            throw new CorruptedInputException();
         }
      }

   }
}
