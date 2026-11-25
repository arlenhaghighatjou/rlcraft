package me.lucko.spark.lib.xz;

import java.io.IOException;
import java.io.InputStream;

class CountingInputStream extends CloseIgnoringInputStream {
   private long size = 0L;

   public CountingInputStream(InputStream var1) {
      super(var1);
   }

   public int read() throws IOException {
      int var1 = this.in.read();
      if (var1 != -1 && this.size >= 0L) {
         ++this.size;
      }

      return var1;
   }

   public int read(byte[] var1, int var2, int var3) throws IOException {
      int var4 = this.in.read(var1, var2, var3);
      if (var4 > 0 && this.size >= 0L) {
         this.size += (long)var4;
      }

      return var4;
   }

   public long getSize() {
      return this.size;
   }
}
