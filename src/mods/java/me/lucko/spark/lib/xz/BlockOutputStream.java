package me.lucko.spark.lib.xz;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import me.lucko.spark.lib.xz.check.Check;
import me.lucko.spark.lib.xz.common.EncoderUtil;

class BlockOutputStream extends FinishableOutputStream {
   private final OutputStream out;
   private final CountingOutputStream outCounted;
   private FinishableOutputStream filterChain;
   private final Check check;
   private final int headerSize;
   private final long compressedSizeLimit;
   private long uncompressedSize = 0L;
   private final byte[] tempBuf = new byte[1];

   public BlockOutputStream(OutputStream var1, FilterEncoder[] var2, Check var3, ArrayCache var4) throws IOException {
      this.out = var1;
      this.check = var3;
      this.outCounted = new CountingOutputStream(var1);
      this.filterChain = this.outCounted;

      for(int var5 = var2.length - 1; var5 >= 0; --var5) {
         this.filterChain = var2[var5].getOutputStream(this.filterChain, var4);
      }

      ByteArrayOutputStream var8 = new ByteArrayOutputStream();
      var8.write(0);
      var8.write(var2.length - 1);

      for(int var6 = 0; var6 < var2.length; ++var6) {
         EncoderUtil.encodeVLI(var8, var2[var6].getFilterID());
         byte[] var7 = var2[var6].getFilterProps();
         EncoderUtil.encodeVLI(var8, (long)var7.length);
         var8.write(var7);
      }

      while((var8.size() & 3) != 0) {
         var8.write(0);
      }

      byte[] var9 = var8.toByteArray();
      this.headerSize = var9.length + 4;
      if (this.headerSize > 1024) {
         throw new UnsupportedOptionsException();
      } else {
         var9[0] = (byte)(var9.length / 4);
         var1.write(var9);
         EncoderUtil.writeCRC32(var1, var9);
         this.compressedSizeLimit = 9223372036854775804L - (long)this.headerSize - (long)var3.getSize();
      }
   }

   public void write(int var1) throws IOException {
      this.tempBuf[0] = (byte)var1;
      this.write(this.tempBuf, 0, 1);
   }

   public void write(byte[] var1, int var2, int var3) throws IOException {
      this.filterChain.write(var1, var2, var3);
      this.check.update(var1, var2, var3);
      this.uncompressedSize += (long)var3;
      this.validate();
   }

   public void flush() throws IOException {
      this.filterChain.flush();
      this.validate();
   }

   public void finish() throws IOException {
      this.filterChain.finish();
      this.validate();

      for(long var1 = this.outCounted.getSize(); (var1 & 3L) != 0L; ++var1) {
         this.out.write(0);
      }

      this.out.write(this.check.finish());
   }

   private void validate() throws IOException {
      long var1 = this.outCounted.getSize();
      if (var1 < 0L || var1 > this.compressedSizeLimit || this.uncompressedSize < 0L) {
         throw new XZIOException("XZ Stream has grown too big");
      }
   }

   public long getUnpaddedSize() {
      return (long)this.headerSize + this.outCounted.getSize() + (long)this.check.getSize();
   }

   public long getUncompressedSize() {
      return this.uncompressedSize;
   }
}
