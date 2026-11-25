package me.lucko.spark.lib.xz;

import java.io.IOException;

class DeltaOutputStream extends FinishableOutputStream {
   private static final int FILTER_BUF_SIZE = 4096;
   private FinishableOutputStream out;
   private final me.lucko.spark.lib.xz.delta.DeltaEncoder delta;
   private final byte[] filterBuf = new byte[4096];
   private boolean finished = false;
   private IOException exception = null;
   private final byte[] tempBuf = new byte[1];

   static int getMemoryUsage() {
      return 5;
   }

   DeltaOutputStream(FinishableOutputStream var1, DeltaOptions var2) {
      this.out = var1;
      this.delta = new me.lucko.spark.lib.xz.delta.DeltaEncoder(var2.getDistance());
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
            throw new XZIOException("Stream finished");
         } else {
            try {
               while(var3 > 4096) {
                  this.delta.encode(var1, var2, 4096, this.filterBuf);
                  this.out.write(this.filterBuf);
                  var2 += 4096;
                  var3 -= 4096;
               }

               this.delta.encode(var1, var2, var3, this.filterBuf);
               this.out.write(this.filterBuf, 0, var3);
            } catch (IOException var5) {
               this.exception = var5;
               throw var5;
            }
         }
      } else {
         throw new IndexOutOfBoundsException();
      }
   }

   public void flush() throws IOException {
      if (this.exception != null) {
         throw this.exception;
      } else if (this.finished) {
         throw new XZIOException("Stream finished or closed");
      } else {
         try {
            this.out.flush();
         } catch (IOException var2) {
            this.exception = var2;
            throw var2;
         }
      }
   }

   public void finish() throws IOException {
      if (!this.finished) {
         if (this.exception != null) {
            throw this.exception;
         }

         try {
            this.out.finish();
         } catch (IOException var2) {
            this.exception = var2;
            throw var2;
         }

         this.finished = true;
      }

   }

   public void close() throws IOException {
      if (this.out != null) {
         try {
            this.out.close();
         } catch (IOException var2) {
            if (this.exception == null) {
               this.exception = var2;
            }
         }

         this.out = null;
      }

      if (this.exception != null) {
         throw this.exception;
      }
   }
}
