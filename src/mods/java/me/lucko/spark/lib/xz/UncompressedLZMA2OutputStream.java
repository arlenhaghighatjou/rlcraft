package me.lucko.spark.lib.xz;

import java.io.DataOutputStream;
import java.io.IOException;

class UncompressedLZMA2OutputStream extends FinishableOutputStream {
   private final ArrayCache arrayCache;
   private FinishableOutputStream out;
   private final DataOutputStream outData;
   private final byte[] uncompBuf;
   private int uncompPos = 0;
   private boolean dictResetNeeded = true;
   private boolean finished = false;
   private IOException exception = null;
   private final byte[] tempBuf = new byte[1];

   static int getMemoryUsage() {
      return 70;
   }

   UncompressedLZMA2OutputStream(FinishableOutputStream var1, ArrayCache var2) {
      if (var1 == null) {
         throw new NullPointerException();
      } else {
         this.out = var1;
         this.outData = new DataOutputStream(var1);
         this.arrayCache = var2;
         this.uncompBuf = var2.getByteArray(65536, false);
      }
   }

   public void write(int var1) throws IOException {
      this.tempBuf[0] = (byte)var1;
      this.write(this.tempBuf, 0, 1);
   }

   public void write(byte[] var1, int var2, int var3) throws IOException {
      if (var2 >= 0 && var3 >= 0 && var2 + var3 >= 0 && var2 + var3 <= var1.length) {
         if (this.exception != null) {
            throw this.exception;
         } else if (this.finished) {
            throw new XZIOException("Stream finished or closed");
         } else {
            try {
               while(var3 > 0) {
                  int var4 = Math.min(65536 - this.uncompPos, var3);
                  System.arraycopy(var1, var2, this.uncompBuf, this.uncompPos, var4);
                  var3 -= var4;
                  this.uncompPos += var4;
                  if (this.uncompPos == 65536) {
                     this.writeChunk();
                  }
               }

            } catch (IOException var5) {
               this.exception = var5;
               throw var5;
            }
         }
      } else {
         throw new IndexOutOfBoundsException();
      }
   }

   private void writeChunk() throws IOException {
      this.outData.writeByte(this.dictResetNeeded ? 1 : 2);
      this.outData.writeShort(this.uncompPos - 1);
      this.outData.write(this.uncompBuf, 0, this.uncompPos);
      this.uncompPos = 0;
      this.dictResetNeeded = false;
   }

   private void writeEndMarker() throws IOException {
      if (this.exception != null) {
         throw this.exception;
      } else if (this.finished) {
         throw new XZIOException("Stream finished or closed");
      } else {
         try {
            if (this.uncompPos > 0) {
               this.writeChunk();
            }

            this.out.write(0);
         } catch (IOException var2) {
            this.exception = var2;
            throw var2;
         }

         this.finished = true;
         this.arrayCache.putArray(this.uncompBuf);
      }
   }

   public void flush() throws IOException {
      if (this.exception != null) {
         throw this.exception;
      } else if (this.finished) {
         throw new XZIOException("Stream finished or closed");
      } else {
         try {
            if (this.uncompPos > 0) {
               this.writeChunk();
            }

            this.out.flush();
         } catch (IOException var2) {
            this.exception = var2;
            throw var2;
         }
      }
   }

   public void finish() throws IOException {
      if (!this.finished) {
         this.writeEndMarker();

         try {
            this.out.finish();
         } catch (IOException var2) {
            this.exception = var2;
            throw var2;
         }
      }

   }

   public void close() throws IOException {
      if (this.out != null) {
         if (!this.finished) {
            try {
               this.writeEndMarker();
            } catch (IOException var2) {
               ;
            }
         }

         try {
            this.out.close();
         } catch (IOException var3) {
            if (this.exception == null) {
               this.exception = var3;
            }
         }

         this.out = null;
      }

      if (this.exception != null) {
         throw this.exception;
      }
   }
}
