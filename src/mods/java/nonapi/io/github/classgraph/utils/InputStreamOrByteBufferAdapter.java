package nonapi.io.github.classgraph.utils;

import java.io.IOException;
import java.io.InputStream;
import java.nio.BufferUnderflowException;
import java.nio.ByteBuffer;
import java.util.Arrays;

public class InputStreamOrByteBufferAdapter implements AutoCloseable {
   private static final int INITIAL_BUFFER_CHUNK_SIZE = 16384;
   private static final int SUBSEQUENT_BUFFER_CHUNK_SIZE = 4096;
   private InputStream inputStream;
   private ByteBuffer byteBuffer;
   public byte[] buf;
   public int curr;
   private int used;

   public InputStreamOrByteBufferAdapter(InputStream inputStream) {
      this.inputStream = inputStream;
      this.buf = new byte[16384];
   }

   public InputStreamOrByteBufferAdapter(ByteBuffer byteBuffer) {
      if (byteBuffer.hasArray()) {
         this.buf = byteBuffer.array();
      } else {
         this.byteBuffer = byteBuffer;
         this.buf = new byte[16384];
      }

   }

   private int read(int off, int len) throws IOException {
      if (len == 0) {
         return 0;
      } else if (this.inputStream != null) {
         return this.inputStream.read(this.buf, off, len);
      } else {
         int bytesRemainingInBuf = this.byteBuffer != null ? this.byteBuffer.remaining() : this.buf.length - off;
         int bytesRead = Math.max(0, Math.min(len, bytesRemainingInBuf));
         if (bytesRead == 0) {
            return -1;
         } else if (this.byteBuffer != null) {
            int byteBufPositionBefore = this.byteBuffer.position();

            try {
               this.byteBuffer.get(this.buf, off, bytesRead);
            } catch (BufferUnderflowException var7) {
               throw new IOException("Buffer underflow", var7);
            }

            return this.byteBuffer.position() - byteBufPositionBefore;
         } else {
            return bytesRead;
         }
      }
   }

   private void readMore(int bytesRequired) throws IOException {
      if ((long)this.used + (long)bytesRequired > 2147483639L) {
         throw new IOException("File is larger than 2GB, cannot read it");
      } else {
         int targetReadSize = Math.max(bytesRequired, this.used == 0 ? 16384 : 4096);
         int maxNewUsed = (int)Math.min((long)this.used + (long)targetReadSize, 2147483639L);
         int bytesToRead = maxNewUsed - this.used;
         if (maxNewUsed > this.buf.length) {
            long newBufLen;
            for(newBufLen = (long)this.buf.length; newBufLen < (long)maxNewUsed; newBufLen <<= 1) {
               ;
            }

            this.buf = Arrays.copyOf(this.buf, (int)Math.min(newBufLen, 2147483639L));
         }

         int extraBytesStillNotRead = bytesToRead;

         int totBytesRead;
         int bytesRead;
         for(totBytesRead = 0; extraBytesStillNotRead > 0; extraBytesStillNotRead -= bytesRead) {
            bytesRead = this.read(this.used, extraBytesStillNotRead);
            if (bytesRead <= 0) {
               break;
            }

            this.used += bytesRead;
            totBytesRead += bytesRead;
         }

         if (totBytesRead < bytesRequired) {
            throw new IOException("Premature EOF while reading classfile");
         }
      }
   }

   public int readUnsignedByte() throws IOException {
      int val = this.readUnsignedByte(this.curr);
      ++this.curr;
      return val;
   }

   public int readUnsignedByte(int offset) throws IOException {
      int bytesToRead = Math.max(0, offset + 1 - this.used);
      if (bytesToRead > 0) {
         this.readMore(bytesToRead);
      }

      return this.buf[offset] & 255;
   }

   public int readUnsignedShort() throws IOException {
      int val = this.readUnsignedShort(this.curr);
      this.curr += 2;
      return val;
   }

   public int readUnsignedShort(int offset) throws IOException {
      int bytesToRead = Math.max(0, offset + 2 - this.used);
      if (bytesToRead > 0) {
         this.readMore(bytesToRead);
      }

      return (this.buf[offset] & 255) << 8 | this.buf[offset + 1] & 255;
   }

   public int readInt() throws IOException {
      int val = this.readInt(this.curr);
      this.curr += 4;
      return val;
   }

   public int readInt(int offset) throws IOException {
      int bytesToRead = Math.max(0, offset + 4 - this.used);
      if (bytesToRead > 0) {
         this.readMore(bytesToRead);
      }

      return (this.buf[offset] & 255) << 24 | (this.buf[offset + 1] & 255) << 16 | (this.buf[offset + 2] & 255) << 8 | this.buf[offset + 3] & 255;
   }

   public long readLong() throws IOException {
      long val = this.readLong(this.curr);
      this.curr += 8;
      return val;
   }

   public long readLong(int offset) throws IOException {
      int bytesToRead = Math.max(0, offset + 8 - this.used);
      if (bytesToRead > 0) {
         this.readMore(bytesToRead);
      }

      return ((long)this.buf[offset] & 255L) << 56 | ((long)this.buf[offset + 1] & 255L) << 48 | ((long)this.buf[offset + 2] & 255L) << 40 | ((long)this.buf[offset + 3] & 255L) << 32 | ((long)this.buf[offset + 4] & 255L) << 24 | ((long)this.buf[offset + 5] & 255L) << 16 | ((long)this.buf[offset + 6] & 255L) << 8 | (long)this.buf[offset + 7] & 255L;
   }

   public void skip(int bytesToSkip) throws IOException {
      int bytesToRead = Math.max(0, this.curr + bytesToSkip - this.used);
      if (bytesToRead > 0) {
         this.readMore(bytesToRead);
      }

      this.curr += bytesToSkip;
   }

   public String readString(int strStart, boolean replaceSlashWithDot, boolean stripLSemicolon) throws IOException {
      int utfLen = this.readUnsignedShort(strStart);
      int utfStart = strStart + 2;
      int bufferUnderrunBytes = Math.max(0, utfStart + utfLen - this.used);
      if (bufferUnderrunBytes > 0) {
         this.readMore(bufferUnderrunBytes);
      }

      char[] chars = new char[utfLen];
      int byteIdx = 0;

      int charIdx;
      for(charIdx = 0; byteIdx < utfLen; ++byteIdx) {
         int c = this.buf[utfStart + byteIdx] & 255;
         if (c > 127) {
            break;
         }

         chars[charIdx++] = (char)(replaceSlashWithDot && c == 47 ? 46 : c);
      }

      while(byteIdx < utfLen) {
         int c = this.buf[utfStart + byteIdx] & 255;
         switch(c >> 4) {
         case 0:
         case 1:
         case 2:
         case 3:
         case 4:
         case 5:
         case 6:
         case 7:
            ++byteIdx;
            chars[charIdx++] = (char)(replaceSlashWithDot && c == 47 ? 46 : c);
            break;
         case 8:
         case 9:
         case 10:
         case 11:
         default:
            throw new IOException("Bad modified UTF8");
         case 12:
         case 13:
            byteIdx += 2;
            if (byteIdx > utfLen) {
               throw new IOException("Bad modified UTF8");
            }

            int c2 = this.buf[utfStart + byteIdx - 1];
            if ((c2 & 192) != 128) {
               throw new IOException("Bad modified UTF8");
            }

            int c3 = (c & 31) << 6 | c2 & 63;
            chars[charIdx++] = (char)(replaceSlashWithDot && c3 == 47 ? 46 : c3);
            break;
         case 14:
            byteIdx += 3;
            if (byteIdx > utfLen) {
               throw new IOException("Bad modified UTF8");
            }

            int c2 = this.buf[utfStart + byteIdx - 2];
            int c3 = this.buf[utfStart + byteIdx - 1];
            if ((c2 & 192) != 128 || (c3 & 192) != 128) {
               throw new IOException("Bad modified UTF8");
            }

            int c4 = (c & 15) << 12 | (c2 & 63) << 6 | c3 & 63;
            chars[charIdx++] = (char)(replaceSlashWithDot && c4 == 47 ? 46 : c4);
         }
      }

      if (charIdx == utfLen && !stripLSemicolon) {
         return new String(chars);
      } else if (stripLSemicolon) {
         if (charIdx >= 2 && chars[0] == 'L' && chars[charIdx - 1] == ';') {
            return new String(chars, 1, charIdx - 2);
         } else {
            throw new IOException("Expected string to start with 'L' and end with ';', got \"" + new String(chars) + "\"");
         }
      } else {
         return new String(chars, 0, charIdx);
      }
   }

   public void close() {
      if (this.inputStream != null) {
         try {
            this.inputStream.close();
         } catch (IOException var2) {
            ;
         }

         this.inputStream = null;
      }

      this.byteBuffer = null;
      this.buf = null;
   }
}
