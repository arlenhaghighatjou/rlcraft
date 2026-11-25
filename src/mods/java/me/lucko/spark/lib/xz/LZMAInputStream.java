package me.lucko.spark.lib.xz;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import me.lucko.spark.lib.xz.lz.LZDecoder;
import me.lucko.spark.lib.xz.lzma.LZMADecoder;
import me.lucko.spark.lib.xz.rangecoder.RangeDecoderFromStream;

public class LZMAInputStream extends InputStream {
   public static final int DICT_SIZE_MAX = 2147483632;
   private InputStream in;
   private ArrayCache arrayCache;
   private LZDecoder lz;
   private RangeDecoderFromStream rc;
   private LZMADecoder lzma;
   private boolean endReached;
   private final byte[] tempBuf;
   private long remainingSize;
   private IOException exception;

   public static int getMemoryUsage(int var0, byte var1) throws UnsupportedOptionsException, CorruptedInputException {
      if (var0 >= 0 && var0 <= 2147483632) {
         int var2 = var1 & 255;
         if (var2 > 224) {
            throw new CorruptedInputException("Invalid LZMA properties byte");
         } else {
            var2 = var2 % 45;
            int var3 = var2 / 9;
            int var4 = var2 - var3 * 9;
            return getMemoryUsage(var0, var4, var3);
         }
      } else {
         throw new UnsupportedOptionsException("LZMA dictionary is too big for this implementation");
      }
   }

   public static int getMemoryUsage(int var0, int var1, int var2) {
      if (var1 >= 0 && var1 <= 8 && var2 >= 0 && var2 <= 4) {
         return 10 + getDictSize(var0) / 1024 + (1536 << var1 + var2) / 1024;
      } else {
         throw new IllegalArgumentException("Invalid lc or lp");
      }
   }

   private static int getDictSize(int var0) {
      if (var0 >= 0 && var0 <= 2147483632) {
         if (var0 < 4096) {
            var0 = 4096;
         }

         return var0 + 15 & -16;
      } else {
         throw new IllegalArgumentException("LZMA dictionary is too big for this implementation");
      }
   }

   public LZMAInputStream(InputStream var1) throws IOException {
      this(var1, -1);
   }

   public LZMAInputStream(InputStream var1, ArrayCache var2) throws IOException {
      this(var1, -1, var2);
   }

   public LZMAInputStream(InputStream var1, int var2) throws IOException {
      this(var1, var2, ArrayCache.getDefaultCache());
   }

   public LZMAInputStream(InputStream var1, int var2, ArrayCache var3) throws IOException {
      this.endReached = false;
      this.tempBuf = new byte[1];
      this.exception = null;
      DataInputStream var4 = new DataInputStream(var1);
      byte var5 = var4.readByte();
      int var6 = 0;

      for(int var7 = 0; var7 < 4; ++var7) {
         var6 |= var4.readUnsignedByte() << 8 * var7;
      }

      long var10 = 0L;

      for(int var9 = 0; var9 < 8; ++var9) {
         var10 |= (long)var4.readUnsignedByte() << 8 * var9;
      }

      int var11 = getMemoryUsage(var6, var5);
      if (var2 != -1 && var11 > var2) {
         throw new MemoryLimitException(var11, var2);
      } else {
         this.initialize(var1, var10, var5, var6, (byte[])null, var3);
      }
   }

   public LZMAInputStream(InputStream var1, long var2, byte var4, int var5) throws IOException {
      this.endReached = false;
      this.tempBuf = new byte[1];
      this.exception = null;
      this.initialize(var1, var2, var4, var5, (byte[])null, ArrayCache.getDefaultCache());
   }

   public LZMAInputStream(InputStream var1, long var2, byte var4, int var5, byte[] var6) throws IOException {
      this.endReached = false;
      this.tempBuf = new byte[1];
      this.exception = null;
      this.initialize(var1, var2, var4, var5, var6, ArrayCache.getDefaultCache());
   }

   public LZMAInputStream(InputStream var1, long var2, byte var4, int var5, byte[] var6, ArrayCache var7) throws IOException {
      this.endReached = false;
      this.tempBuf = new byte[1];
      this.exception = null;
      this.initialize(var1, var2, var4, var5, var6, var7);
   }

   public LZMAInputStream(InputStream var1, long var2, int var4, int var5, int var6, int var7, byte[] var8) throws IOException {
      this.endReached = false;
      this.tempBuf = new byte[1];
      this.exception = null;
      this.initialize(var1, var2, var4, var5, var6, var7, var8, ArrayCache.getDefaultCache());
   }

   public LZMAInputStream(InputStream var1, long var2, int var4, int var5, int var6, int var7, byte[] var8, ArrayCache var9) throws IOException {
      this.endReached = false;
      this.tempBuf = new byte[1];
      this.exception = null;
      this.initialize(var1, var2, var4, var5, var6, var7, var8, var9);
   }

   private void initialize(InputStream var1, long var2, byte var4, int var5, byte[] var6, ArrayCache var7) throws IOException {
      if (var2 < -1L) {
         throw new UnsupportedOptionsException("Uncompressed size is too big");
      } else {
         int var8 = var4 & 255;
         if (var8 > 224) {
            throw new CorruptedInputException("Invalid LZMA properties byte");
         } else {
            int var9 = var8 / 45;
            var8 = var8 - var9 * 9 * 5;
            int var10 = var8 / 9;
            int var11 = var8 - var10 * 9;
            if (var5 >= 0 && var5 <= 2147483632) {
               this.initialize(var1, var2, var11, var10, var9, var5, var6, var7);
            } else {
               throw new UnsupportedOptionsException("LZMA dictionary is too big for this implementation");
            }
         }
      }
   }

   private void initialize(InputStream var1, long var2, int var4, int var5, int var6, int var7, byte[] var8, ArrayCache var9) throws IOException {
      if (var2 >= -1L && var4 >= 0 && var4 <= 8 && var5 >= 0 && var5 <= 4 && var6 >= 0 && var6 <= 4) {
         this.in = var1;
         this.arrayCache = var9;
         var7 = getDictSize(var7);
         if (var2 >= 0L && (long)var7 > var2) {
            var7 = getDictSize((int)var2);
         }

         this.lz = new LZDecoder(getDictSize(var7), var8, var9);
         this.rc = new RangeDecoderFromStream(var1);
         this.lzma = new LZMADecoder(this.lz, this.rc, var4, var5, var6);
         this.remainingSize = var2;
      } else {
         throw new IllegalArgumentException();
      }
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
            try {
               int var4 = 0;

               while(var3 > 0) {
                  int var5 = var3;
                  if (this.remainingSize >= 0L && this.remainingSize < (long)var3) {
                     var5 = (int)this.remainingSize;
                  }

                  this.lz.setLimit(var5);

                  try {
                     this.lzma.decode();
                  } catch (CorruptedInputException var7) {
                     if (this.remainingSize != -1L || !this.lzma.endMarkerDetected()) {
                        throw var7;
                     }

                     this.endReached = true;
                     this.rc.normalize();
                  }

                  int var6 = this.lz.flush(var1, var2);
                  var2 += var6;
                  var3 -= var6;
                  var4 += var6;
                  if (this.remainingSize >= 0L) {
                     this.remainingSize -= (long)var6;

                     assert this.remainingSize >= 0L;

                     if (this.remainingSize == 0L) {
                        this.endReached = true;
                     }
                  }

                  if (this.endReached) {
                     if (this.rc.isFinished() && !this.lz.hasPending()) {
                        this.putArraysToCache();
                        return var4 == 0 ? -1 : var4;
                     }

                     throw new CorruptedInputException();
                  }
               }

               return var4;
            } catch (IOException var8) {
               this.exception = var8;
               throw var8;
            }
         }
      } else {
         throw new IndexOutOfBoundsException();
      }
   }

   private void putArraysToCache() {
      if (this.lz != null) {
         this.lz.putArraysToCache(this.arrayCache);
         this.lz = null;
      }

   }

   public void close() throws IOException {
      if (this.in != null) {
         this.putArraysToCache();

         try {
            this.in.close();
         } finally {
            this.in = null;
         }
      }

   }
}
