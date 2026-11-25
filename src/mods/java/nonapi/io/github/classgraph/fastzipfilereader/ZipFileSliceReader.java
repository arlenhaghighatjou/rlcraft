package nonapi.io.github.classgraph.fastzipfilereader;

import java.io.EOFException;
import java.io.IOException;
import java.nio.BufferUnderflowException;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

class ZipFileSliceReader implements AutoCloseable {
   private final ZipFileSlice zipFileSlice;
   private final ByteBuffer[] chunkCache;
   private final byte[] scratch = new byte[256];

   public ZipFileSliceReader(ZipFileSlice zipFileSlice) {
      this.zipFileSlice = zipFileSlice;
      this.chunkCache = new ByteBuffer[zipFileSlice.physicalZipFile.numMappedByteBuffers];
   }

   private ByteBuffer getChunk(int chunkIdx) throws IOException, InterruptedException {
      ByteBuffer chunk = this.chunkCache[chunkIdx];
      if (chunk == null) {
         ByteBuffer byteBufferDup = this.zipFileSlice.physicalZipFile.getByteBuffer(chunkIdx).duplicate();
         chunk = this.chunkCache[chunkIdx] = byteBufferDup;
      }

      return chunk;
   }

   int read(long off, byte[] buf, int bufStart, int numBytesToRead) throws IOException, InterruptedException {
      if (off >= 0L && bufStart >= 0 && bufStart + numBytesToRead <= buf.length) {
         int currBufStart = bufStart;
         int remainingBytesToRead = numBytesToRead;
         int totBytesRead = 0;

         int numBytesRead;
         for(long currOff = off; remainingBytesToRead > 0; remainingBytesToRead -= numBytesRead) {
            long currOffAbsolute = this.zipFileSlice.startOffsetWithinPhysicalZipFile + currOff;
            int chunkIdx = (int)(currOffAbsolute / 2147483639L);
            ByteBuffer chunk = this.getChunk(chunkIdx);
            long chunkStartAbsolute = (long)chunkIdx * 2147483639L;
            int startReadPos = (int)(currOffAbsolute - chunkStartAbsolute);
            chunk.mark();
            chunk.position(startReadPos);
            numBytesRead = Math.min(chunk.remaining(), remainingBytesToRead);

            try {
               chunk.get(buf, currBufStart, numBytesRead);
            } catch (BufferUnderflowException var20) {
               throw new EOFException("Unexpected EOF");
            }

            chunk.reset();
            currOff += (long)numBytesRead;
            currBufStart += numBytesRead;
            totBytesRead += numBytesRead;
         }

         return totBytesRead == 0 && numBytesToRead > 0 ? -1 : totBytesRead;
      } else {
         throw new IndexOutOfBoundsException();
      }
   }

   static byte[] getBytes(byte[] arr, long off, int len) throws IndexOutOfBoundsException {
      if (off > 4294967295L) {
         throw new IndexOutOfBoundsException();
      } else {
         int ioff = (int)off;
         if (ioff >= 0 && ioff <= arr.length - len) {
            return Arrays.copyOfRange(arr, ioff, ioff + len);
         } else {
            throw new IndexOutOfBoundsException();
         }
      }
   }

   byte[] getBytes(long off, int len) throws IOException, InterruptedException {
      if (off >= 0L && off <= this.zipFileSlice.len - (long)len) {
         byte[] bytes = new byte[len];
         if (this.read(off, bytes, 0, len) < len) {
            throw new EOFException("Unexpected EOF");
         } else {
            return bytes;
         }
      } else {
         throw new IndexOutOfBoundsException();
      }
   }

   static byte getByte(byte[] arr, long off) throws IndexOutOfBoundsException {
      if (off > 4294967295L) {
         throw new IndexOutOfBoundsException();
      } else {
         int ioff = (int)off;
         if (ioff >= 0 && ioff <= arr.length - 1) {
            return (byte)(arr[ioff] & 255);
         } else {
            throw new IndexOutOfBoundsException();
         }
      }
   }

   byte getByte(long off) throws IOException, InterruptedException {
      if (off >= 0L && off <= this.zipFileSlice.len - 1L) {
         if (this.read(off, this.scratch, 0, 1) < 1) {
            throw new EOFException("Unexpected EOF");
         } else {
            return (byte)(this.scratch[0] & 255);
         }
      } else {
         throw new IndexOutOfBoundsException();
      }
   }

   static int getShort(byte[] arr, long off) throws IndexOutOfBoundsException {
      if (off > 4294967295L) {
         throw new IndexOutOfBoundsException();
      } else {
         int ioff = (int)off;
         if (ioff >= 0 && ioff <= arr.length - 2) {
            return (arr[ioff + 1] & 255) << 8 | arr[ioff] & 255;
         } else {
            throw new IndexOutOfBoundsException();
         }
      }
   }

   int getShort(long off) throws IOException, InterruptedException {
      if (off >= 0L && off <= this.zipFileSlice.len - 2L) {
         if (this.read(off, this.scratch, 0, 2) < 2) {
            throw new EOFException("Unexpected EOF");
         } else {
            return (this.scratch[1] & 255) << 8 | this.scratch[0] & 255;
         }
      } else {
         throw new IndexOutOfBoundsException();
      }
   }

   static int getInt(byte[] arr, long off) throws IOException {
      if (off > 4294967295L) {
         throw new IndexOutOfBoundsException();
      } else {
         int ioff = (int)off;
         if (ioff >= 0 && ioff <= arr.length - 4) {
            return (arr[ioff + 3] & 255) << 24 | (arr[ioff + 2] & 255) << 16 | (arr[ioff + 1] & 255) << 8 | arr[ioff] & 255;
         } else {
            throw new IndexOutOfBoundsException();
         }
      }
   }

   int getInt(long off) throws IOException, InterruptedException {
      if (off >= 0L && off <= this.zipFileSlice.len - 4L) {
         if (this.read(off, this.scratch, 0, 4) < 4) {
            throw new EOFException("Unexpected EOF");
         } else {
            return (this.scratch[3] & 255) << 24 | (this.scratch[2] & 255) << 16 | (this.scratch[1] & 255) << 8 | this.scratch[0] & 255;
         }
      } else {
         throw new IndexOutOfBoundsException();
      }
   }

   static long getLong(byte[] arr, long off) throws IOException {
      if (off > 4294967295L) {
         throw new IndexOutOfBoundsException();
      } else {
         int ioff = (int)off;
         if (ioff >= 0 && ioff <= arr.length - 8) {
            return ((long)arr[ioff + 7] & 255L) << 56 | ((long)arr[ioff + 6] & 255L) << 48 | ((long)arr[ioff + 5] & 255L) << 40 | ((long)arr[ioff + 4] & 255L) << 32 | ((long)arr[ioff + 3] & 255L) << 24 | ((long)arr[ioff + 2] & 255L) << 16 | ((long)arr[ioff + 1] & 255L) << 8 | (long)arr[ioff] & 255L;
         } else {
            throw new IndexOutOfBoundsException();
         }
      }
   }

   long getLong(long off) throws IOException, InterruptedException {
      if (off >= 0L && off <= this.zipFileSlice.len - 8L) {
         if (this.read(off, this.scratch, 0, 8) < 8) {
            throw new EOFException("Unexpected EOF");
         } else {
            return ((long)this.scratch[7] & 255L) << 56 | ((long)this.scratch[6] & 255L) << 48 | ((long)this.scratch[5] & 255L) << 40 | ((long)this.scratch[4] & 255L) << 32 | ((long)this.scratch[3] & 255L) << 24 | ((long)this.scratch[2] & 255L) << 16 | ((long)this.scratch[1] & 255L) << 8 | (long)this.scratch[0] & 255L;
         }
      } else {
         throw new IndexOutOfBoundsException();
      }
   }

   static String getString(byte[] arr, long off, int lenBytes) throws IOException {
      if (off > 4294967295L) {
         throw new IndexOutOfBoundsException();
      } else {
         int ioff = (int)off;
         if (ioff >= 0 && ioff <= arr.length - lenBytes) {
            return new String(arr, ioff, lenBytes, StandardCharsets.UTF_8);
         } else {
            throw new IndexOutOfBoundsException();
         }
      }
   }

   String getString(long off, int lenBytes) throws IOException, InterruptedException {
      if (off >= 0L && off <= this.zipFileSlice.len - (long)lenBytes) {
         byte[] scratchToUse = lenBytes <= this.scratch.length ? this.scratch : new byte[lenBytes];
         if (this.read(off, scratchToUse, 0, lenBytes) < lenBytes) {
            throw new EOFException("Unexpected EOF");
         } else {
            return new String(scratchToUse, 0, lenBytes, StandardCharsets.UTF_8);
         }
      } else {
         throw new IndexOutOfBoundsException();
      }
   }

   public void close() {
      Arrays.fill(this.chunkCache, (Object)null);
   }
}
