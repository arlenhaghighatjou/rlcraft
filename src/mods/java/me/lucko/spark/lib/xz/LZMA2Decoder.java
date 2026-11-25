package me.lucko.spark.lib.xz;

import java.io.InputStream;

class LZMA2Decoder extends LZMA2Coder implements FilterDecoder {
   private int dictSize;

   LZMA2Decoder(byte[] var1) throws UnsupportedOptionsException {
      if (var1.length == 1 && (var1[0] & 255) <= 37) {
         this.dictSize = 2 | var1[0] & 1;
         this.dictSize <<= (var1[0] >>> 1) + 11;
      } else {
         throw new UnsupportedOptionsException("Unsupported LZMA2 properties");
      }
   }

   public int getMemoryUsage() {
      return LZMA2InputStream.getMemoryUsage(this.dictSize);
   }

   public InputStream getInputStream(InputStream var1, ArrayCache var2) {
      return new LZMA2InputStream(var1, this.dictSize, (byte[])null, var2);
   }
}
