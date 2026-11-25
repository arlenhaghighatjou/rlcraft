package me.lucko.spark.lib.xz;

import java.io.IOException;
import java.io.OutputStream;
import me.lucko.spark.lib.xz.lz.LZEncoder;
import me.lucko.spark.lib.xz.lzma.LZMAEncoder;
import me.lucko.spark.lib.xz.rangecoder.RangeEncoderToStream;

public class LZMAOutputStream extends FinishableOutputStream {
   private OutputStream out;
   private final ArrayCache arrayCache;
   private LZEncoder lz;
   private final RangeEncoderToStream rc;
   private LZMAEncoder lzma;
   private final int props;
   private final boolean useEndMarker;
   private final long expectedUncompressedSize;
   private long currentUncompressedSize;
   private boolean finished;
   private IOException exception;
   private final byte[] tempBuf;

   private LZMAOutputStream(OutputStream var1, LZMA2Options var2, boolean var3, boolean var4, long var5, ArrayCache var7) throws IOException {
      this.currentUncompressedSize = 0L;
      this.finished = false;
      this.exception = null;
      this.tempBuf = new byte[1];
      if (var1 == null) {
         throw new NullPointerException();
      } else if (var5 < -1L) {
         throw new IllegalArgumentException("Invalid expected input size (less than -1)");
      } else {
         this.useEndMarker = var4;
         this.expectedUncompressedSize = var5;
         this.arrayCache = var7;
         this.out = var1;
         this.rc = new RangeEncoderToStream(var1);
         int var8 = var2.getDictSize();
         this.lzma = LZMAEncoder.getInstance(this.rc, var2.getLc(), var2.getLp(), var2.getPb(), var2.getMode(), var8, 0, var2.getNiceLen(), var2.getMatchFinder(), var2.getDepthLimit(), var7);
         this.lz = this.lzma.getLZEncoder();
         byte[] var9 = var2.getPresetDict();
         if (var9 != null && var9.length > 0) {
            if (var3) {
               throw new UnsupportedOptionsException("Preset dictionary cannot be used in .lzma files (try a raw LZMA stream instead)");
            }

            this.lz.setPresetDict(var8, var9);
         }

         this.props = (var2.getPb() * 5 + var2.getLp()) * 9 + var2.getLc();
         if (var3) {
            var1.write(this.props);

            for(int var10 = 0; var10 < 4; ++var10) {
               var1.write(var8 & 255);
               var8 >>>= 8;
            }

            for(int var11 = 0; var11 < 8; ++var11) {
               var1.write((int)(var5 >>> 8 * var11) & 255);
            }
         }

      }
   }

   public LZMAOutputStream(OutputStream var1, LZMA2Options var2, long var3) throws IOException {
      this(var1, var2, var3, ArrayCache.getDefaultCache());
   }

   public LZMAOutputStream(OutputStream var1, LZMA2Options var2, long var3, ArrayCache var5) throws IOException {
      this(var1, var2, true, var3 == -1L, var3, var5);
   }

   public LZMAOutputStream(OutputStream var1, LZMA2Options var2, boolean var3) throws IOException {
      this(var1, var2, var3, ArrayCache.getDefaultCache());
   }

   public LZMAOutputStream(OutputStream var1, LZMA2Options var2, boolean var3, ArrayCache var4) throws IOException {
      this(var1, var2, false, var3, -1L, var4);
   }

   public int getProps() {
      return this.props;
   }

   public long getUncompressedSize() {
      return this.currentUncompressedSize;
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
         } else if (this.expectedUncompressedSize != -1L && this.expectedUncompressedSize - this.currentUncompressedSize < (long)var3) {
            throw new XZIOException("Expected uncompressed input size (" + this.expectedUncompressedSize + " bytes) was exceeded");
         } else {
            this.currentUncompressedSize += (long)var3;

            try {
               while(var3 > 0) {
                  int var4 = this.lz.fillWindow(var1, var2, var3);
                  var2 += var4;
                  var3 -= var4;
                  this.lzma.encodeForLZMA1();
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

   public void flush() throws IOException {
      throw new XZIOException("LZMAOutputStream does not support flushing");
   }

   public void finish() throws IOException {
      if (!this.finished) {
         if (this.exception != null) {
            throw this.exception;
         }

         try {
            if (this.expectedUncompressedSize != -1L && this.expectedUncompressedSize != this.currentUncompressedSize) {
               throw new XZIOException("Expected uncompressed size (" + this.expectedUncompressedSize + ") doesn't equal " + "the number of bytes written to the stream (" + this.currentUncompressedSize + ")");
            }

            this.lz.setFinishing();
            this.lzma.encodeForLZMA1();
            if (this.useEndMarker) {
               this.lzma.encodeLZMA1EndMarker();
            }

            this.rc.finish();
         } catch (IOException var2) {
            this.exception = var2;
            throw var2;
         }

         this.finished = true;
         this.lzma.putArraysToCache(this.arrayCache);
         this.lzma = null;
         this.lz = null;
      }

   }

   public void close() throws IOException {
      if (this.out != null) {
         try {
            this.finish();
         } catch (IOException var2) {
            ;
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
