package me.lucko.spark.lib.xz.common;

import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.util.zip.CRC32;
import me.lucko.spark.lib.xz.CorruptedInputException;
import me.lucko.spark.lib.xz.UnsupportedOptionsException;
import me.lucko.spark.lib.xz.XZ;
import me.lucko.spark.lib.xz.XZFormatException;

public class DecoderUtil extends Util {
   public DecoderUtil() {
   }

   public static boolean isCRC32Valid(byte[] var0, int var1, int var2, int var3) {
      CRC32 var4 = new CRC32();
      var4.update(var0, var1, var2);
      long var5 = var4.getValue();

      for(int var7 = 0; var7 < 4; ++var7) {
         if ((byte)((int)(var5 >>> var7 * 8)) != var0[var3 + var7]) {
            return false;
         }
      }

      return true;
   }

   public static StreamFlags decodeStreamHeader(byte[] var0) throws IOException {
      for(int var1 = 0; var1 < XZ.HEADER_MAGIC.length; ++var1) {
         if (var0[var1] != XZ.HEADER_MAGIC[var1]) {
            throw new XZFormatException();
         }
      }

      if (!isCRC32Valid(var0, XZ.HEADER_MAGIC.length, 2, XZ.HEADER_MAGIC.length + 2)) {
         throw new CorruptedInputException("XZ Stream Header is corrupt");
      } else {
         try {
            return decodeStreamFlags(var0, XZ.HEADER_MAGIC.length);
         } catch (UnsupportedOptionsException var2) {
            throw new UnsupportedOptionsException("Unsupported options in XZ Stream Header");
         }
      }
   }

   public static StreamFlags decodeStreamFooter(byte[] var0) throws IOException {
      if (var0[10] == XZ.FOOTER_MAGIC[0] && var0[11] == XZ.FOOTER_MAGIC[1]) {
         if (!isCRC32Valid(var0, 4, 6, 0)) {
            throw new CorruptedInputException("XZ Stream Footer is corrupt");
         } else {
            StreamFlags var1;
            try {
               var1 = decodeStreamFlags(var0, 8);
            } catch (UnsupportedOptionsException var3) {
               throw new UnsupportedOptionsException("Unsupported options in XZ Stream Footer");
            }

            var1.backwardSize = 0L;

            for(int var2 = 0; var2 < 4; ++var2) {
               var1.backwardSize |= (long)((var0[var2 + 4] & 255) << var2 * 8);
            }

            var1.backwardSize = (var1.backwardSize + 1L) * 4L;
            return var1;
         }
      } else {
         throw new CorruptedInputException("XZ Stream Footer is corrupt");
      }
   }

   private static StreamFlags decodeStreamFlags(byte[] var0, int var1) throws UnsupportedOptionsException {
      if (var0[var1] == 0 && (var0[var1 + 1] & 255) < 16) {
         StreamFlags var2 = new StreamFlags();
         var2.checkType = var0[var1 + 1];
         return var2;
      } else {
         throw new UnsupportedOptionsException();
      }
   }

   public static boolean areStreamFlagsEqual(StreamFlags var0, StreamFlags var1) {
      return var0.checkType == var1.checkType;
   }

   public static long decodeVLI(InputStream var0) throws IOException {
      int var1 = var0.read();
      if (var1 == -1) {
         throw new EOFException();
      } else {
         long var2 = (long)(var1 & 127);

         for(int var4 = 0; (var1 & 128) != 0; var2 |= (long)(var1 & 127) << var4 * 7) {
            ++var4;
            if (var4 >= 9) {
               throw new CorruptedInputException();
            }

            var1 = var0.read();
            if (var1 == -1) {
               throw new EOFException();
            }

            if (var1 == 0) {
               throw new CorruptedInputException();
            }
         }

         return var2;
      }
   }
}
