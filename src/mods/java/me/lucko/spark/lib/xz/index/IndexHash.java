package me.lucko.spark.lib.xz.index;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.zip.CheckedInputStream;
import me.lucko.spark.lib.xz.CorruptedInputException;
import me.lucko.spark.lib.xz.XZIOException;
import me.lucko.spark.lib.xz.check.CRC32;
import me.lucko.spark.lib.xz.check.Check;
import me.lucko.spark.lib.xz.check.SHA256;
import me.lucko.spark.lib.xz.common.DecoderUtil;

public class IndexHash extends IndexBase {
   private Check hash;

   public IndexHash() {
      super(new CorruptedInputException());

      try {
         this.hash = new SHA256();
      } catch (NoSuchAlgorithmException var2) {
         this.hash = new CRC32();
      }

   }

   public void add(long var1, long var3) throws XZIOException {
      super.add(var1, var3);
      ByteBuffer var5 = ByteBuffer.allocate(16);
      var5.putLong(var1);
      var5.putLong(var3);
      this.hash.update(var5.array());
   }

   public void validate(InputStream var1) throws IOException {
      java.util.zip.CRC32 var2 = new java.util.zip.CRC32();
      var2.update(0);
      CheckedInputStream var3 = new CheckedInputStream(var1, var2);
      long var4 = DecoderUtil.decodeVLI(var3);
      if (var4 != this.recordCount) {
         throw new CorruptedInputException("XZ Block Header or the start of XZ Index is corrupt");
      } else {
         IndexHash var6 = new IndexHash();
         long var7 = 0L;

         while(true) {
            if (var7 < this.recordCount) {
               long var9 = DecoderUtil.decodeVLI(var3);
               long var11 = DecoderUtil.decodeVLI(var3);

               try {
                  var6.add(var9, var11);
               } catch (XZIOException var14) {
                  throw new CorruptedInputException("XZ Index is corrupt");
               }

               if (var6.blocksSum <= this.blocksSum && var6.uncompressedSum <= this.uncompressedSum && var6.indexListSize <= this.indexListSize) {
                  ++var7;
                  continue;
               }

               throw new CorruptedInputException("XZ Index is corrupt");
            }

            if (var6.blocksSum == this.blocksSum && var6.uncompressedSum == this.uncompressedSum && var6.indexListSize == this.indexListSize && Arrays.equals(var6.hash.finish(), this.hash.finish())) {
               DataInputStream var15 = new DataInputStream(var3);

               for(int var8 = this.getIndexPaddingSize(); var8 > 0; --var8) {
                  if (var15.readUnsignedByte() != 0) {
                     throw new CorruptedInputException("XZ Index is corrupt");
                  }
               }

               long var16 = var2.getValue();

               for(int var10 = 0; var10 < 4; ++var10) {
                  if ((var16 >>> var10 * 8 & 255L) != (long)var15.readUnsignedByte()) {
                     throw new CorruptedInputException("XZ Index is corrupt");
                  }
               }

               return;
            }

            throw new CorruptedInputException("XZ Index is corrupt");
         }
      }
   }
}
