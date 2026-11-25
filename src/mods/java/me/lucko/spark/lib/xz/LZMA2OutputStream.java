package me.lucko.spark.lib.xz;

import java.io.DataOutputStream;
import java.io.IOException;
import me.lucko.spark.lib.xz.lz.LZEncoder;
import me.lucko.spark.lib.xz.lzma.LZMAEncoder;
import me.lucko.spark.lib.xz.rangecoder.RangeEncoderToBuffer;

class LZMA2OutputStream extends FinishableOutputStream {
   static final int COMPRESSED_SIZE_MAX = 65536;
   private final ArrayCache arrayCache;
   private FinishableOutputStream out;
   private final DataOutputStream outData;
   private LZEncoder lz;
   private RangeEncoderToBuffer rc;
   private LZMAEncoder lzma;
   private final int props;
   private boolean dictResetNeeded = true;
   private boolean stateResetNeeded = true;
   private boolean propsNeeded = true;
   private int pendingSize = 0;
   private boolean finished = false;
   private IOException exception = null;
   private final byte[] tempBuf = new byte[1];

   private static int getExtraSizeBefore(int var0) {
      return 65536 > var0 ? 65536 - var0 : 0;
   }

   static int getMemoryUsage(LZMA2Options var0) {
      int var1 = var0.getDictSize();
      int var2 = getExtraSizeBefore(var1);
      return 70 + LZMAEncoder.getMemoryUsage(var0.getMode(), var1, var2, var0.getMatchFinder());
   }

   LZMA2OutputStream(FinishableOutputStream var1, LZMA2Options var2, ArrayCache var3) {
      if (var1 == null) {
         throw new NullPointerException();
      } else {
         this.arrayCache = var3;
         this.out = var1;
         this.outData = new DataOutputStream(var1);
         this.rc = new RangeEncoderToBuffer(65536, var3);
         int var4 = var2.getDictSize();
         int var5 = getExtraSizeBefore(var4);
         this.lzma = LZMAEncoder.getInstance(this.rc, var2.getLc(), var2.getLp(), var2.getPb(), var2.getMode(), var4, var5, var2.getNiceLen(), var2.getMatchFinder(), var2.getDepthLimit(), this.arrayCache);
         this.lz = this.lzma.getLZEncoder();
         byte[] var6 = var2.getPresetDict();
         if (var6 != null && var6.length > 0) {
            this.lz.setPresetDict(var4, var6);
            this.dictResetNeeded = false;
         }

         this.props = (var2.getPb() * 5 + var2.getLp()) * 9 + var2.getLc();
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
                  int var4 = this.lz.fillWindow(var1, var2, var3);
                  var2 += var4;
                  var3 -= var4;
                  this.pendingSize += var4;
                  if (this.lzma.encodeForLZMA2()) {
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
      int var1 = this.rc.finish();
      int var2 = this.lzma.getUncompressedSize();

      assert var1 > 0 : var1;

      assert var2 > 0 : var2;

      if (var1 + 2 < var2) {
         this.writeLZMA(var2, var1);
      } else {
         this.lzma.reset();
         var2 = this.lzma.getUncompressedSize();

         assert var2 > 0 : var2;

         this.writeUncompressed(var2);
      }

      this.pendingSize -= var2;
      this.lzma.resetUncompressedSize();
      this.rc.reset();
   }

   private void writeLZMA(int var1, int var2) throws IOException {
      short var3;
      if (this.propsNeeded) {
         if (this.dictResetNeeded) {
            var3 = 224;
         } else {
            var3 = 192;
         }
      } else if (this.stateResetNeeded) {
         var3 = 160;
      } else {
         var3 = 128;
      }

      var3 = var3 | var1 - 1 >>> 16;
      this.outData.writeByte(var3);
      this.outData.writeShort(var1 - 1);
      this.outData.writeShort(var2 - 1);
      if (this.propsNeeded) {
         this.outData.writeByte(this.props);
      }

      this.rc.write(this.out);
      this.propsNeeded = false;
      this.stateResetNeeded = false;
      this.dictResetNeeded = false;
   }

   private void writeUncompressed(int var1) throws IOException {
      while(var1 > 0) {
         int var2 = Math.min(var1, 65536);
         this.outData.writeByte(this.dictResetNeeded ? 1 : 2);
         this.outData.writeShort(var2 - 1);
         this.lz.copyUncompressed(this.out, var1, var2);
         var1 -= var2;
         this.dictResetNeeded = false;
      }

      this.stateResetNeeded = true;
   }

   private void writeEndMarker() throws IOException {
      assert !this.finished;

      if (this.exception != null) {
         throw this.exception;
      } else {
         this.lz.setFinishing();

         try {
            while(this.pendingSize > 0) {
               this.lzma.encodeForLZMA2();
               this.writeChunk();
            }

            this.out.write(0);
         } catch (IOException var2) {
            this.exception = var2;
            throw var2;
         }

         this.finished = true;
         this.lzma.putArraysToCache(this.arrayCache);
         this.lzma = null;
         this.lz = null;
         this.rc.putArraysToCache(this.arrayCache);
         this.rc = null;
      }
   }

   public void flush() throws IOException {
      if (this.exception != null) {
         throw this.exception;
      } else if (this.finished) {
         throw new XZIOException("Stream finished or closed");
      } else {
         try {
            this.lz.setFlushing();

            while(this.pendingSize > 0) {
               this.lzma.encodeForLZMA2();
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
