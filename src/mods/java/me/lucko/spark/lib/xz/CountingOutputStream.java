package me.lucko.spark.lib.xz;

import java.io.IOException;
import java.io.OutputStream;

class CountingOutputStream extends FinishableOutputStream {
   private final OutputStream out;
   private long size = 0L;

   public CountingOutputStream(OutputStream var1) {
      this.out = var1;
   }

   public void write(int var1) throws IOException {
      this.out.write(var1);
      if (this.size >= 0L) {
         ++this.size;
      }

   }

   public void write(byte[] var1, int var2, int var3) throws IOException {
      this.out.write(var1, var2, var3);
      if (this.size >= 0L) {
         this.size += (long)var3;
      }

   }

   public void flush() throws IOException {
      this.out.flush();
   }

   public void close() throws IOException {
      this.out.close();
   }

   public long getSize() {
      return this.size;
   }
}
