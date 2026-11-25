package me.lucko.spark.lib.xz;

import java.io.IOException;
import java.io.InputStream;

public abstract class SeekableInputStream extends InputStream {
   public SeekableInputStream() {
   }

   public long skip(long var1) throws IOException {
      if (var1 <= 0L) {
         return 0L;
      } else {
         long var3 = this.length();
         long var5 = this.position();
         if (var5 >= var3) {
            return 0L;
         } else {
            if (var3 - var5 < var1) {
               var1 = var3 - var5;
            }

            this.seek(var5 + var1);
            return var1;
         }
      }
   }

   public abstract long length() throws IOException;

   public abstract long position() throws IOException;

   public abstract void seek(long var1) throws IOException;
}
