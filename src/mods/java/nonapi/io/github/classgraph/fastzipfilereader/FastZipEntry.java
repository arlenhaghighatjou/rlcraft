package nonapi.io.github.classgraph.fastzipfilereader;

import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.BufferUnderflowException;
import java.nio.ByteBuffer;
import java.nio.file.attribute.PosixFilePermission;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.zip.DataFormatException;
import java.util.zip.Inflater;
import java.util.zip.ZipException;
import nonapi.io.github.classgraph.recycler.RecycleOnClose;
import nonapi.io.github.classgraph.utils.FileUtils;
import nonapi.io.github.classgraph.utils.VersionFinder;

public class FastZipEntry implements Comparable<FastZipEntry> {
   final LogicalZipFile parentLogicalZipFile;
   private final long locHeaderPos;
   private long entryDataStartOffsetWithinPhysicalZipFile = -1L;
   public final String entryName;
   final boolean isDeflated;
   public final long compressedSize;
   public final long uncompressedSize;
   public final long lastModified;
   public final Set<PosixFilePermission> posixFilePermissions;
   final int version;
   public final String entryNameUnversioned;
   private final NestedJarHandler nestedJarHandler;
   private RecyclableInflater recyclableInflaterInstance;

   FastZipEntry(LogicalZipFile parentLogicalZipFile, long locHeaderPos, String entryName, boolean isDeflated, long compressedSize, long uncompressedSize, NestedJarHandler nestedJarHandler, long lastModified, Set<PosixFilePermission> posixFilePermissions) {
      this.parentLogicalZipFile = parentLogicalZipFile;
      this.locHeaderPos = locHeaderPos;
      this.entryName = entryName;
      this.isDeflated = isDeflated;
      this.compressedSize = compressedSize;
      this.uncompressedSize = !isDeflated && uncompressedSize < 0L ? compressedSize : uncompressedSize;
      this.nestedJarHandler = nestedJarHandler;
      this.lastModified = lastModified;
      this.posixFilePermissions = posixFilePermissions;
      int entryVersion = 8;
      String entryNameWithoutVersionPrefix = entryName;
      if (entryName.startsWith("META-INF/versions/") && entryName.length() > "META-INF/versions/".length() + 1) {
         int nextSlashIdx = entryName.indexOf(47, "META-INF/versions/".length());
         if (nextSlashIdx > 0) {
            String versionStr = entryName.substring("META-INF/versions/".length(), nextSlashIdx);
            int versionInt = 0;
            if (versionStr.length() < 6 && !versionStr.isEmpty()) {
               for(int i = 0; i < versionStr.length(); ++i) {
                  char c = versionStr.charAt(i);
                  if (c < '0' || c > '9') {
                     versionInt = 0;
                     break;
                  }

                  if (versionInt == 0) {
                     versionInt = c - 48;
                  } else {
                     versionInt = versionInt * 10 + c - 48;
                  }
               }
            }

            if (versionInt != 0) {
               entryVersion = versionInt;
            }

            if (entryVersion < 9 || entryVersion > VersionFinder.JAVA_MAJOR_VERSION) {
               entryVersion = 8;
            }

            if (entryVersion > 8) {
               entryNameWithoutVersionPrefix = entryName.substring(nextSlashIdx + 1);
               if (entryNameWithoutVersionPrefix.startsWith("META-INF/")) {
                  entryVersion = 8;
                  entryNameWithoutVersionPrefix = entryName;
               }
            }
         }
      }

      this.version = entryVersion;
      this.entryNameUnversioned = entryNameWithoutVersionPrefix;
   }

   long getEntryDataStartOffsetWithinPhysicalZipFile() throws IOException, InterruptedException {
      if (this.entryDataStartOffsetWithinPhysicalZipFile == -1L) {
         RecycleOnClose<ZipFileSliceReader, RuntimeException> zipFileSliceReaderRecycleOnClose = this.parentLogicalZipFile.zipFileSliceReaderRecycler.acquireRecycleOnClose();

         try {
            ZipFileSliceReader headerReader = zipFileSliceReaderRecycleOnClose.get();
            if (headerReader.getInt(this.locHeaderPos) != 67324752) {
               throw new IOException("Zip entry has bad LOC header: " + this.entryName);
            }

            long dataStartPos = this.locHeaderPos + 30L + (long)headerReader.getShort(this.locHeaderPos + 26L) + (long)headerReader.getShort(this.locHeaderPos + 28L);
            if (dataStartPos > this.parentLogicalZipFile.len) {
               throw new IOException("Unexpected EOF when trying to read zip entry data: " + this.entryName);
            }

            this.entryDataStartOffsetWithinPhysicalZipFile = this.parentLogicalZipFile.startOffsetWithinPhysicalZipFile + dataStartPos;
         } catch (Throwable var6) {
            if (zipFileSliceReaderRecycleOnClose != null) {
               try {
                  zipFileSliceReaderRecycleOnClose.close();
               } catch (Throwable var5) {
                  var6.addSuppressed(var5);
               }
            }

            throw var6;
         }

         if (zipFileSliceReaderRecycleOnClose != null) {
            zipFileSliceReaderRecycleOnClose.close();
         }
      }

      return this.entryDataStartOffsetWithinPhysicalZipFile;
   }

   public boolean canGetAsSlice() throws IOException, InterruptedException {
      long dataStartOffsetWithinPhysicalZipFile = this.getEntryDataStartOffsetWithinPhysicalZipFile();
      return !this.isDeflated && dataStartOffsetWithinPhysicalZipFile / 2147483639L == (dataStartOffsetWithinPhysicalZipFile + this.uncompressedSize) / 2147483639L;
   }

   public ByteBuffer getAsSlice() throws IOException, InterruptedException {
      if (!this.canGetAsSlice()) {
         throw new IllegalArgumentException("Cannot open zip entry as a slice");
      } else {
         long dataStartOffsetWithinPhysicalZipFile = this.getEntryDataStartOffsetWithinPhysicalZipFile();
         int chunkIdx = (int)(dataStartOffsetWithinPhysicalZipFile / 2147483639L);
         long chunkStart = (long)chunkIdx * 2147483639L;
         ByteBuffer dupdBuf = this.parentLogicalZipFile.physicalZipFile.getByteBuffer(chunkIdx).duplicate();
         dupdBuf.position((int)(dataStartOffsetWithinPhysicalZipFile - chunkStart));
         dupdBuf.limit((int)(dataStartOffsetWithinPhysicalZipFile + this.uncompressedSize - chunkStart));
         return dupdBuf.slice();
      }
   }

   public InputStream open() throws IOException, InterruptedException {
      if (this.recyclableInflaterInstance != null) {
         throw new IOException("Zip entry already open");
      } else {
         if (this.isDeflated) {
            this.recyclableInflaterInstance = this.nestedJarHandler.inflaterRecycler.acquire();
         }

         return new InputStream() {
            private final long dataStartOffsetWithinPhysicalZipFile = FastZipEntry.this.getEntryDataStartOffsetWithinPhysicalZipFile();
            private final byte[] scratch = new byte[8192];
            private ByteBuffer currChunkByteBuf;
            private boolean isLastChunk;
            private int currChunkIdx;
            private boolean eof;
            private final Inflater inflater;
            private final AtomicBoolean closed;
            private static final int INFLATE_BUF_SIZE = 1024;

            {
               this.inflater = FastZipEntry.this.isDeflated ? FastZipEntry.this.recyclableInflaterInstance.getInflater() : null;
               this.closed = new AtomicBoolean(false);
               this.currChunkIdx = (int)(this.dataStartOffsetWithinPhysicalZipFile / 2147483639L);
               this.currChunkByteBuf = FastZipEntry.this.parentLogicalZipFile.physicalZipFile.getByteBuffer(this.currChunkIdx).duplicate();
               int chunkPos = (int)(this.dataStartOffsetWithinPhysicalZipFile - (long)this.currChunkIdx * 2147483639L);
               this.currChunkByteBuf.position(chunkPos);
               long endPos = (long)chunkPos + FastZipEntry.this.compressedSize;
               this.currChunkByteBuf.limit((int)Math.min(2147483639L, endPos));
               this.isLastChunk = endPos <= 2147483639L;
            }

            private boolean readNextChunk() throws IOException, InterruptedException {
               ++this.currChunkIdx;
               if (this.currChunkIdx >= FastZipEntry.this.parentLogicalZipFile.physicalZipFile.numMappedByteBuffers) {
                  return false;
               } else {
                  long chunkStartOff = (long)this.currChunkIdx * 2147483639L;
                  long priorBytes = chunkStartOff - this.dataStartOffsetWithinPhysicalZipFile;
                  long remainingBytes = FastZipEntry.this.compressedSize - priorBytes;
                  if (remainingBytes <= 0L) {
                     return false;
                  } else {
                     this.currChunkByteBuf = FastZipEntry.this.parentLogicalZipFile.physicalZipFile.getByteBuffer(this.currChunkIdx).duplicate();
                     this.currChunkByteBuf.position(0);
                     this.currChunkByteBuf.limit((int)Math.min(2147483639L, remainingBytes));
                     this.isLastChunk = remainingBytes <= 2147483639L;
                     return true;
                  }
               }
            }

            private int readDeflated(byte[] buf, int off, int len) throws IOException, InterruptedException {
               try {
                  byte[] inflateBuf = new byte[1024];

                  int numInflatedBytes;
                  while((numInflatedBytes = this.inflater.inflate(buf, off, len)) == 0) {
                     if (!this.inflater.finished() && !this.inflater.needsDictionary()) {
                        if (!this.inflater.needsInput()) {
                           continue;
                        }

                        if (this.currChunkByteBuf.hasRemaining() || this.readNextChunk() && this.currChunkByteBuf.hasRemaining()) {
                           try {
                              int remaining = this.currChunkByteBuf.remaining();
                              if (this.isLastChunk && remaining < inflateBuf.length) {
                                 this.currChunkByteBuf.get(inflateBuf, 0, remaining);
                                 inflateBuf[remaining] = 0;
                                 this.inflater.setInput(inflateBuf, 0, remaining + 1);
                                 continue;
                              }

                              if (this.isLastChunk && remaining == inflateBuf.length) {
                                 this.currChunkByteBuf.get(inflateBuf, 0, remaining - 1);
                                 this.inflater.setInput(inflateBuf, 0, remaining - 1);
                                 continue;
                              }

                              int bytesToRead = Math.min(inflateBuf.length, remaining);
                              this.currChunkByteBuf.get(inflateBuf, 0, bytesToRead);
                              this.inflater.setInput(inflateBuf, 0, bytesToRead);
                              continue;
                           } catch (BufferUnderflowException var8) {
                              throw new IOException("Unexpected EOF in deflated data");
                           }
                        }

                        throw new IOException("Unexpected EOF in deflated data");
                     }

                     this.eof = true;
                     return -1;
                  }

                  return numInflatedBytes;
               } catch (DataFormatException var9) {
                  throw new ZipException(var9.getMessage() != null ? var9.getMessage() : "Invalid deflated zip entry data");
               }
            }

            private int readStored(byte[] buf, int off, int len) throws IOException, InterruptedException {
               int read;
               int numBytesRead;
               for(read = 0; read < len; read += numBytesRead) {
                  if (!this.currChunkByteBuf.hasRemaining() && !this.readNextChunk()) {
                     return read == 0 ? -1 : read;
                  }

                  int remainingToRead = len - read;
                  int remainingInBuf = this.currChunkByteBuf.remaining();
                  numBytesRead = Math.min(remainingToRead, remainingInBuf);

                  try {
                     this.currChunkByteBuf.get(buf, off + read, numBytesRead);
                  } catch (BufferUnderflowException var9) {
                     throw new EOFException("Unexpected EOF in stored (non-deflated) zip entry data");
                  }
               }

               return read;
            }

            public int read(byte[] buf, int off, int len) throws IOException {
               if (this.closed.get()) {
                  throw new IOException("Stream closed");
               } else if (buf == null) {
                  throw new NullPointerException();
               } else if (off >= 0 && len >= 0 && len <= buf.length - off) {
                  if (len == 0) {
                     return 0;
                  } else if (FastZipEntry.this.parentLogicalZipFile.physicalZipFile.fileLen == 0L) {
                     return -1;
                  } else {
                     try {
                        return FastZipEntry.this.isDeflated ? this.readDeflated(buf, off, len) : this.readStored(buf, off, len);
                     } catch (InterruptedException var5) {
                        FastZipEntry.this.nestedJarHandler.interruptionChecker.interrupt();
                        throw new IOException("Thread was interrupted");
                     }
                  }
               } else {
                  throw new IndexOutOfBoundsException();
               }
            }

            public int read() throws IOException {
               if (this.closed.get()) {
                  throw new IOException("Stream closed");
               } else {
                  return this.read(this.scratch, 0, 1) == -1 ? -1 : this.scratch[0] & 255;
               }
            }

            public int available() throws IOException {
               if (this.closed.get()) {
                  throw new IOException("Stream closed");
               } else {
                  if (this.inflater.finished()) {
                     this.eof = true;
                  }

                  return this.eof ? 0 : 1;
               }
            }

            public long skip(long n) throws IOException {
               if (this.closed.get()) {
                  throw new IOException("Stream closed");
               } else if (n < 0L) {
                  throw new IllegalArgumentException("Invalid skip value");
               } else {
                  long total;
                  int numSkipped;
                  for(total = 0L; total < n; total += (long)numSkipped) {
                     numSkipped = this.read(this.scratch, 0, (int)Math.min(n - total, (long)this.scratch.length));
                     if (numSkipped == -1) {
                        this.eof = true;
                        break;
                     }
                  }

                  return total;
               }
            }

            public boolean markSupported() {
               return false;
            }

            public synchronized void mark(int readlimit) {
               throw new IllegalArgumentException("Not supported");
            }

            public synchronized void reset() throws IOException {
               throw new IllegalArgumentException("Not supported");
            }

            public void close() throws IOException {
               if (!this.closed.getAndSet(true)) {
                  this.currChunkByteBuf = null;
                  if (FastZipEntry.this.recyclableInflaterInstance != null) {
                     FastZipEntry.this.nestedJarHandler.inflaterRecycler.recycle(FastZipEntry.this.recyclableInflaterInstance);
                     FastZipEntry.this.recyclableInflaterInstance = null;
                  }
               }

            }
         };
      }
   }

   public byte[] load() throws IOException, InterruptedException {
      InputStream is = this.open();

      byte[] var2;
      try {
         var2 = FileUtils.readAllBytesAsArray(is, this.uncompressedSize);
      } catch (Throwable var5) {
         if (is != null) {
            try {
               is.close();
            } catch (Throwable var4) {
               var5.addSuppressed(var4);
            }
         }

         throw var5;
      }

      if (is != null) {
         is.close();
      }

      return var2;
   }

   public String loadAsString() throws IOException, InterruptedException {
      InputStream is = this.open();

      String var2;
      try {
         var2 = FileUtils.readAllBytesAsString(is, this.uncompressedSize);
      } catch (Throwable var5) {
         if (is != null) {
            try {
               is.close();
            } catch (Throwable var4) {
               var5.addSuppressed(var4);
            }
         }

         throw var5;
      }

      if (is != null) {
         is.close();
      }

      return var2;
   }

   public String getPath() {
      return this.parentLogicalZipFile.getPath() + "!/" + this.entryName;
   }

   public String toString() {
      return "jar:file:" + this.getPath();
   }

   public int compareTo(FastZipEntry o) {
      int diff0 = o.version - this.version;
      if (diff0 != 0) {
         return diff0;
      } else {
         int diff1 = this.entryNameUnversioned.compareTo(o.entryNameUnversioned);
         if (diff1 != 0) {
            return diff1;
         } else {
            int diff2 = this.entryName.compareTo(o.entryName);
            if (diff2 != 0) {
               return diff2;
            } else {
               long diff3 = this.locHeaderPos - o.locHeaderPos;
               return diff3 < 0L ? -1 : (diff3 > 0L ? 1 : 0);
            }
         }
      }
   }

   public boolean equals(Object obj) {
      if (this == obj) {
         return true;
      } else if (!(obj instanceof FastZipEntry)) {
         return false;
      } else {
         FastZipEntry other = (FastZipEntry)obj;
         return this.parentLogicalZipFile.equals(other.parentLogicalZipFile) && this.compareTo(other) == 0;
      }
   }

   public int hashCode() {
      return this.parentLogicalZipFile.hashCode() ^ this.version ^ this.entryName.hashCode() ^ (int)this.locHeaderPos;
   }
}
