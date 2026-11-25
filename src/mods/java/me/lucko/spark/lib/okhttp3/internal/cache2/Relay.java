package me.lucko.spark.lib.okhttp3.internal.cache2;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import me.lucko.spark.lib.okhttp3.internal.Util;
import me.lucko.spark.lib.okio.Buffer;
import me.lucko.spark.lib.okio.ByteString;
import me.lucko.spark.lib.okio.Source;
import me.lucko.spark.lib.okio.Timeout;

final class Relay {
   private static final int SOURCE_UPSTREAM = 1;
   private static final int SOURCE_FILE = 2;
   static final ByteString PREFIX_CLEAN = ByteString.encodeUtf8("OkHttp cache v1\n");
   static final ByteString PREFIX_DIRTY = ByteString.encodeUtf8("OkHttp DIRTY :(\n");
   private static final long FILE_HEADER_SIZE = 32L;
   RandomAccessFile file;
   Thread upstreamReader;
   Source upstream;
   final Buffer upstreamBuffer = new Buffer();
   long upstreamPos;
   boolean complete;
   private final ByteString metadata;
   final Buffer buffer = new Buffer();
   final long bufferMaxSize;
   int sourceCount;

   private Relay(RandomAccessFile file, Source upstream, long upstreamPos, ByteString metadata, long bufferMaxSize) {
      this.file = file;
      this.upstream = upstream;
      this.complete = upstream == null;
      this.upstreamPos = upstreamPos;
      this.metadata = metadata;
      this.bufferMaxSize = bufferMaxSize;
   }

   public static Relay edit(File file, Source upstream, ByteString metadata, long bufferMaxSize) throws IOException {
      RandomAccessFile randomAccessFile = new RandomAccessFile(file, "rw");
      Relay result = new Relay(randomAccessFile, upstream, 0L, metadata, bufferMaxSize);
      randomAccessFile.setLength(0L);
      result.writeHeader(PREFIX_DIRTY, -1L, -1L);
      return result;
   }

   public static Relay read(File file) throws IOException {
      RandomAccessFile randomAccessFile = new RandomAccessFile(file, "rw");
      FileOperator fileOperator = new FileOperator(randomAccessFile.getChannel());
      Buffer header = new Buffer();
      fileOperator.read(0L, header, 32L);
      ByteString prefix = header.readByteString((long)PREFIX_CLEAN.size());
      if (!prefix.equals(PREFIX_CLEAN)) {
         throw new IOException("unreadable cache file");
      } else {
         long upstreamSize = header.readLong();
         long metadataSize = header.readLong();
         Buffer metadataBuffer = new Buffer();
         fileOperator.read(32L + upstreamSize, metadataBuffer, metadataSize);
         ByteString metadata = metadataBuffer.readByteString();
         return new Relay(randomAccessFile, (Source)null, upstreamSize, metadata, 0L);
      }
   }

   private void writeHeader(ByteString prefix, long upstreamSize, long metadataSize) throws IOException {
      Buffer header = new Buffer();
      header.write(prefix);
      header.writeLong(upstreamSize);
      header.writeLong(metadataSize);
      if (header.size() != 32L) {
         throw new IllegalArgumentException();
      } else {
         FileOperator fileOperator = new FileOperator(this.file.getChannel());
         fileOperator.write(0L, header, 32L);
      }
   }

   private void writeMetadata(long upstreamSize) throws IOException {
      Buffer metadataBuffer = new Buffer();
      metadataBuffer.write(this.metadata);
      FileOperator fileOperator = new FileOperator(this.file.getChannel());
      fileOperator.write(32L + upstreamSize, metadataBuffer, (long)this.metadata.size());
   }

   void commit(long upstreamSize) throws IOException {
      this.writeMetadata(upstreamSize);
      this.file.getChannel().force(false);
      this.writeHeader(PREFIX_CLEAN, upstreamSize, (long)this.metadata.size());
      this.file.getChannel().force(false);
      synchronized(this) {
         this.complete = true;
      }

      Util.closeQuietly(this.upstream);
      this.upstream = null;
   }

   boolean isClosed() {
      return this.file == null;
   }

   public ByteString metadata() {
      return this.metadata;
   }

   public Source newSource() {
      synchronized(this) {
         if (this.file == null) {
            return null;
         }

         ++this.sourceCount;
      }

      return new Relay.RelaySource();
   }

   class RelaySource implements Source {
      private final Timeout timeout = new Timeout();
      private FileOperator fileOperator;
      private long sourcePos;

      RelaySource() {
         this.fileOperator = new FileOperator(Relay.this.file.getChannel());
      }

      public long read(Buffer sink, long byteCount) throws IOException {
         if (this.fileOperator == null) {
            throw new IllegalStateException("closed");
         } else {
            long upstreamPos;
            int source;
            synchronized(Relay.this) {
               while(true) {
                  upstreamPos = Relay.this.upstreamPos;
                  if (this.sourcePos == Relay.this.upstreamPos) {
                     if (Relay.this.complete) {
                        return -1L;
                     }

                     if (Relay.this.upstreamReader != null) {
                        this.timeout.waitUntilNotified(Relay.this);
                        continue;
                     }

                     Relay.this.upstreamReader = Thread.currentThread();
                     source = 1;
                     break;
                  }

                  long bufferPos = upstreamPos - Relay.this.buffer.size();
                  if (this.sourcePos >= bufferPos) {
                     long bytesToRead = Math.min(byteCount, upstreamPos - this.sourcePos);
                     Relay.this.buffer.copyTo(sink, this.sourcePos - bufferPos, bytesToRead);
                     this.sourcePos += bytesToRead;
                     return bytesToRead;
                  }

                  source = 2;
                  break;
               }
            }

            if (source == 2) {
               long bytesToRead = Math.min(byteCount, upstreamPos - this.sourcePos);
               this.fileOperator.read(32L + this.sourcePos, sink, bytesToRead);
               this.sourcePos += bytesToRead;
               return bytesToRead;
            } else {
               boolean var25 = false;

               long var34;
               label192: {
                  long var35;
                  try {
                     var25 = true;
                     long var32 = Relay.this.upstream.read(Relay.this.upstreamBuffer, Relay.this.bufferMaxSize);
                     if (var32 == -1L) {
                        Relay.this.commit(upstreamPos);
                        var34 = -1L;
                        var25 = false;
                        break label192;
                     }

                     bytesRead = Math.min(var32, byteCount);
                     Relay.this.upstreamBuffer.copyTo(sink, 0L, var34);
                     this.sourcePos += var34;
                     this.fileOperator.write(32L + upstreamPos, Relay.this.upstreamBuffer.clone(), var32);
                     synchronized(Relay.this) {
                        Relay.this.buffer.write(Relay.this.upstreamBuffer, var32);
                        if (Relay.this.buffer.size() > Relay.this.bufferMaxSize) {
                           Relay.this.buffer.skip(Relay.this.buffer.size() - Relay.this.bufferMaxSize);
                        }

                        Relay.this.upstreamPos += var32;
                     }

                     var35 = var34;
                     var25 = false;
                  } finally {
                     if (var25) {
                        synchronized(Relay.this) {
                           Relay.this.upstreamReader = null;
                           Relay.this.notifyAll();
                        }
                     }
                  }

                  synchronized(Relay.this) {
                     Relay.this.upstreamReader = null;
                     Relay.this.notifyAll();
                     return var35;
                  }
               }

               synchronized(Relay.this) {
                  Relay.this.upstreamReader = null;
                  Relay.this.notifyAll();
                  return var34;
               }
            }
         }
      }

      public Timeout timeout() {
         return this.timeout;
      }

      public void close() throws IOException {
         if (this.fileOperator != null) {
            this.fileOperator = null;
            RandomAccessFile fileToClose = null;
            synchronized(Relay.this) {
               --Relay.this.sourceCount;
               if (Relay.this.sourceCount == 0) {
                  fileToClose = Relay.this.file;
                  Relay.this.file = null;
               }
            }

            if (fileToClose != null) {
               Util.closeQuietly(fileToClose);
            }

         }
      }
   }
}
