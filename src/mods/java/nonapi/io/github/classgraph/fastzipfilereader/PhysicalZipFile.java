package nonapi.io.github.classgraph.fastzipfilereader;

import java.io.Closeable;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.FileChannel.MapMode;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;
import nonapi.io.github.classgraph.concurrency.SingletonMap;
import nonapi.io.github.classgraph.utils.FastPathResolver;
import nonapi.io.github.classgraph.utils.FileUtils;
import nonapi.io.github.classgraph.utils.LogNode;

class PhysicalZipFile implements Closeable {
   private final File file;
   private final String path;
   private RandomAccessFile raf;
   private FileChannel fc;
   final long fileLen;
   final int numMappedByteBuffers;
   private ByteBuffer[] mappedByteBuffersCached;
   private SingletonMap<Integer, ByteBuffer, IOException> chunkIdxToByteBuffer;
   NestedJarHandler nestedJarHandler;
   boolean isDeflatedToRam;
   private final AtomicBoolean closed = new AtomicBoolean(false);

   PhysicalZipFile(File file, NestedJarHandler nestedJarHandler) throws IOException {
      FileUtils.checkCanReadAndIsFile(file);
      this.file = file;
      this.nestedJarHandler = nestedJarHandler;
      this.path = FastPathResolver.resolve(FileUtils.CURR_DIR_PATH, file.getPath());

      try {
         this.raf = new RandomAccessFile(file, "r");
         this.fileLen = this.raf.length();
         if (this.fileLen == 0L) {
            throw new IOException("Zipfile is empty: " + file);
         }

         this.fc = this.raf.getChannel();
      } catch (SecurityException | IOException var4) {
         if (this.raf != null) {
            this.raf.close();
            this.raf = null;
         }

         if (this.fc != null) {
            this.fc.close();
            this.fc = null;
         }

         throw var4;
      }

      this.numMappedByteBuffers = (int)((this.fileLen + 2147483639L) / 2147483639L);
      this.mappedByteBuffersCached = new MappedByteBuffer[this.numMappedByteBuffers];
      this.chunkIdxToByteBuffer = new SingletonMap<Integer, ByteBuffer, IOException>() {
         public ByteBuffer newInstance(Integer chunkIdxI, LogNode log) throws IOException {
            long pos = chunkIdxI.longValue() * 2147483639L;
            long chunkSize = Math.min(2147483639L, PhysicalZipFile.this.fileLen - pos);
            MappedByteBuffer buffer = null;

            try {
               buffer = PhysicalZipFile.this.fc.map(MapMode.READ_ONLY, pos, chunkSize);
            } catch (FileNotFoundException var9) {
               throw var9;
            } catch (OutOfMemoryError | IOException var10) {
               System.gc();
               System.runFinalization();
               buffer = PhysicalZipFile.this.fc.map(MapMode.READ_ONLY, pos, chunkSize);
            }

            return buffer;
         }
      };
   }

   PhysicalZipFile(ByteBuffer byteBuffer, File outermostFile, String path, NestedJarHandler nestedJarHandler) throws IOException {
      this.file = outermostFile;
      this.path = path;
      this.nestedJarHandler = nestedJarHandler;
      this.isDeflatedToRam = true;
      this.fileLen = (long)byteBuffer.remaining();
      if (this.fileLen == 0L) {
         throw new IOException("Zipfile is empty: " + path);
      } else {
         this.numMappedByteBuffers = 1;
         this.mappedByteBuffersCached = new ByteBuffer[this.numMappedByteBuffers];
         this.mappedByteBuffersCached[0] = byteBuffer;
      }
   }

   ByteBuffer getByteBuffer(int chunkIdx) throws IOException, InterruptedException {
      if (this.closed.get()) {
         throw new IOException(this.getClass().getSimpleName() + " already closed");
      } else if (chunkIdx >= 0 && chunkIdx < this.mappedByteBuffersCached.length) {
         if (this.mappedByteBuffersCached[chunkIdx] == null) {
            try {
               this.mappedByteBuffersCached[chunkIdx] = this.chunkIdxToByteBuffer.get(Integer.valueOf(chunkIdx), (LogNode)null);
            } catch (SingletonMap.NullSingletonException var3) {
               throw new IOException("Cannot get ByteBuffer chunk " + chunkIdx + " : " + var3);
            }
         }

         return this.mappedByteBuffersCached[chunkIdx];
      } else {
         throw new IOException("Chunk index out of range");
      }
   }

   public File getFile() {
      return this.file;
   }

   public String getPath() {
      return this.path;
   }

   public int hashCode() {
      return this.file == null ? 0 : this.file.hashCode();
   }

   public boolean equals(Object obj) {
      if (obj == this) {
         return true;
      } else {
         return !(obj instanceof PhysicalZipFile) ? false : Objects.equals(this.file, ((PhysicalZipFile)obj).file);
      }
   }

   public void close() {
      if (!this.closed.getAndSet(true)) {
         if (this.fc != null) {
            try {
               this.fc.close();
               this.fc = null;
            } catch (IOException var3) {
               ;
            }
         }

         if (this.raf != null) {
            try {
               this.raf.close();
               this.raf = null;
            } catch (IOException var2) {
               ;
            }
         }

         if (this.chunkIdxToByteBuffer != null) {
            this.chunkIdxToByteBuffer.clear();
            this.chunkIdxToByteBuffer = null;
         }

         if (this.mappedByteBuffersCached != null) {
            for(int i = 0; i < this.mappedByteBuffersCached.length; ++i) {
               if (this.mappedByteBuffersCached[i] != null) {
                  FileUtils.closeDirectByteBuffer(this.mappedByteBuffersCached[i], (LogNode)null);
                  this.mappedByteBuffersCached[i] = null;
               }
            }

            this.mappedByteBuffersCached = null;
         }

         this.nestedJarHandler = null;
      }

   }

   public String toString() {
      return this.path;
   }
}
