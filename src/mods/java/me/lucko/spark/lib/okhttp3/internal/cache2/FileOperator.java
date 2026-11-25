package me.lucko.spark.lib.okhttp3.internal.cache2;

import java.io.IOException;
import java.nio.channels.FileChannel;
import me.lucko.spark.lib.okio.Buffer;

final class FileOperator {
   private final FileChannel fileChannel;

   FileOperator(FileChannel fileChannel) {
      this.fileChannel = fileChannel;
   }

   public void write(long pos, Buffer source, long byteCount) throws IOException {
      if (byteCount >= 0L && byteCount <= source.size()) {
         while(byteCount > 0L) {
            long bytesWritten = this.fileChannel.transferFrom(source, pos, byteCount);
            pos += bytesWritten;
            byteCount -= bytesWritten;
         }

      } else {
         throw new IndexOutOfBoundsException();
      }
   }

   public void read(long pos, Buffer sink, long byteCount) throws IOException {
      if (byteCount < 0L) {
         throw new IndexOutOfBoundsException();
      } else {
         while(byteCount > 0L) {
            long bytesRead = this.fileChannel.transferTo(pos, byteCount, sink);
            pos += bytesRead;
            byteCount -= bytesRead;
         }

      }
   }
}
