package me.lucko.spark.lib.xz;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;

public class XZInputStream extends InputStream {
   private final ArrayCache arrayCache;
   private final int memoryLimit;
   private InputStream in;
   private SingleXZInputStream xzIn;
   private final boolean verifyCheck;
   private boolean endReached;
   private IOException exception;
   private final byte[] tempBuf;

   public XZInputStream(InputStream var1) throws IOException {
      this(var1, -1);
   }

   public XZInputStream(InputStream var1, ArrayCache var2) throws IOException {
      this(var1, -1, var2);
   }

   public XZInputStream(InputStream var1, int var2) throws IOException {
      this(var1, var2, true);
   }

   public XZInputStream(InputStream var1, int var2, ArrayCache var3) throws IOException {
      this(var1, var2, true, var3);
   }

   public XZInputStream(InputStream var1, int var2, boolean var3) throws IOException {
      this(var1, var2, var3, ArrayCache.getDefaultCache());
   }

   public XZInputStream(InputStream var1, int var2, boolean var3, ArrayCache var4) throws IOException {
      this.endReached = false;
      this.exception = null;
      this.tempBuf = new byte[1];
      this.arrayCache = var4;
      this.in = var1;
      this.memoryLimit = var2;
      this.verifyCheck = var3;
      this.xzIn = new SingleXZInputStream(var1, var2, var3, var4);
   }

   public int read() throws IOException {
      return this.read(this.tempBuf, 0, 1) == -1 ? -1 : this.tempBuf[0] & 255;
   }

   public int read(byte[] var1, int var2, int var3) throws IOException {
      if (var2 >= 0 && var3 >= 0 && var2 + var3 >= 0 && var2 + var3 <= var1.length) {
         if (var3 == 0) {
            return 0;
         } else if (this.in == null) {
            throw new XZIOException("Stream closed");
         } else if (this.exception != null) {
            throw this.exception;
         } else if (this.endReached) {
            return -1;
         } else {
            int var4 = 0;

            try {
               while(var3 > 0) {
                  if (this.xzIn == null) {
                     this.prepareNextStream();
                     if (this.endReached) {
                        return var4 == 0 ? -1 : var4;
                     }
                  }

                  int var5 = this.xzIn.read(var1, var2, var3);
                  if (var5 > 0) {
                     var4 += var5;
                     var2 += var5;
                     var3 -= var5;
                  } else if (var5 == -1) {
                     this.xzIn = null;
                  }
               }
            } catch (IOException var6) {
               this.exception = var6;
               if (var4 == 0) {
                  throw var6;
               }
            }

            return var4;
         }
      } else {
         throw new IndexOutOfBoundsException();
      }
   }

   private void prepareNextStream() throws IOException {
      DataInputStream var1 = new DataInputStream(this.in);
      byte[] var2 = new byte[12];

      while(true) {
         int var3 = var1.read(var2, 0, 1);
         if (var3 == -1) {
            this.endReached = true;
            return;
         }

         var1.readFully(var2, 1, 3);
         if (var2[0] != 0 || var2[1] != 0 || var2[2] != 0 || var2[3] != 0) {
            break;
         }
      }

      var1.readFully(var2, 4, 8);

      try {
         this.xzIn = new SingleXZInputStream(this.in, this.memoryLimit, this.verifyCheck, var2, this.arrayCache);
      } catch (XZFormatException var4) {
         throw new CorruptedInputException("Garbage after a valid XZ Stream");
      }
   }

   public int available() throws IOException {
      if (this.in == null) {
         throw new XZIOException("Stream closed");
      } else if (this.exception != null) {
         throw this.exception;
      } else {
         return this.xzIn == null ? 0 : this.xzIn.available();
      }
   }

   public void close() throws IOException {
      this.close(true);
   }

   public void close(boolean var1) throws IOException {
      if (this.in != null) {
         if (this.xzIn != null) {
            this.xzIn.close(false);
            this.xzIn = null;
         }

         try {
            if (var1) {
               this.in.close();
            }
         } finally {
            this.in = null;
         }
      }

   }
}
