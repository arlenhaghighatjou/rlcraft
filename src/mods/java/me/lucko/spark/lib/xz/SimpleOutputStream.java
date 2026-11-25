package me.lucko.spark.lib.xz;

import java.io.IOException;
import me.lucko.spark.lib.xz.simple.SimpleFilter;

class SimpleOutputStream extends FinishableOutputStream {
   private static final int FILTER_BUF_SIZE = 4096;
   private FinishableOutputStream out;
   private final SimpleFilter simpleFilter;
   private final byte[] filterBuf = new byte[4096];
   private int pos = 0;
   private int unfiltered = 0;
   private IOException exception = null;
   private boolean finished = false;
   private final byte[] tempBuf = new byte[1];

   static int getMemoryUsage() {
      return 5;
   }

   SimpleOutputStream(FinishableOutputStream var1, SimpleFilter var2) {
      if (var1 == null) {
         throw new NullPointerException();
      } else {
         this.out = var1;
         this.simpleFilter = var2;
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
            while(var3 > 0) {
               int var4 = Math.min(var3, 4096 - (this.pos + this.unfiltered));
               System.arraycopy(var1, var2, this.filterBuf, this.pos + this.unfiltered, var4);
               var2 += var4;
               var3 -= var4;
               this.unfiltered += var4;
               int var5 = this.simpleFilter.code(this.filterBuf, this.pos, this.unfiltered);

               assert var5 <= this.unfiltered;

               this.unfiltered -= var5;

               try {
                  this.out.write(this.filterBuf, this.pos, var5);
               } catch (IOException var7) {
                  this.exception = var7;
                  throw var7;
               }

               this.pos += var5;
               if (this.pos + this.unfiltered == 4096) {
                  System.arraycopy(this.filterBuf, this.pos, this.filterBuf, 0, this.unfiltered);
                  this.pos = 0;
               }
            }

         }
      } else {
         throw new IndexOutOfBoundsException();
      }
   }

   private void writePending() throws IOException {
      assert !this.finished;

      if (this.exception != null) {
         throw this.exception;
      } else {
         try {
            this.out.write(this.filterBuf, this.pos, this.unfiltered);
         } catch (IOException var2) {
            this.exception = var2;
            throw var2;
         }

         this.finished = true;
      }
   }

   public void flush() throws IOException {
      throw new UnsupportedOptionsException("Flushing is not supported");
   }

   public void finish() throws IOException {
      if (!this.finished) {
         this.writePending();

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
               this.writePending();
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
