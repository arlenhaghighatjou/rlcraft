package me.lucko.spark.lib.xz.common;

import java.io.IOException;
import java.io.OutputStream;
import java.util.zip.CRC32;

public class EncoderUtil extends Util {
   public EncoderUtil() {
   }

   public static void writeCRC32(OutputStream var0, byte[] var1) throws IOException {
      CRC32 var2 = new CRC32();
      var2.update(var1);
      long var3 = var2.getValue();

      for(int var5 = 0; var5 < 4; ++var5) {
         var0.write((byte)((int)(var3 >>> var5 * 8)));
      }

   }

   public static void encodeVLI(OutputStream var0, long var1) throws IOException {
      while(var1 >= 128L) {
         var0.write((byte)((int)(var1 | 128L)));
         var1 >>>= 7;
      }

      var0.write((byte)((int)var1));
   }
}
