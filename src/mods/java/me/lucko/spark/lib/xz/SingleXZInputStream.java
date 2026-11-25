package me.lucko.spark.lib.xz;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import me.lucko.spark.lib.xz.check.Check;
import me.lucko.spark.lib.xz.common.DecoderUtil;
import me.lucko.spark.lib.xz.common.StreamFlags;
import me.lucko.spark.lib.xz.index.IndexHash;

public class SingleXZInputStream extends InputStream {
   private InputStream in;
   private final ArrayCache arrayCache;
   private final int memoryLimit;
   private final StreamFlags streamHeaderFlags;
   private final Check check;
   private final boolean verifyCheck;
   private BlockInputStream blockDecoder;
   private final IndexHash indexHash;
   private boolean endReached;
   private IOException exception;
   private final byte[] tempBuf;

   private static byte[] readStreamHeader(InputStream var0) throws IOException {
      byte[] var1 = new byte[12];
      (new DataInputStream(var0)).readFully(var1);
      return var1;
   }

   public SingleXZInputStream(InputStream var1) throws IOException {
      this(var1, -1);
   }

   public SingleXZInputStream(InputStream var1, ArrayCache var2) throws IOException {
      this(var1, -1, var2);
   }

   public SingleXZInputStream(InputStream var1, int var2) throws IOException {
      this(var1, var2, true);
   }

   public SingleXZInputStream(InputStream var1, int var2, ArrayCache var3) throws IOException {
      this(var1, var2, true, var3);
   }

   public SingleXZInputStream(InputStream var1, int var2, boolean var3) throws IOException {
      this(var1, var2, var3, ArrayCache.getDefaultCache());
   }

   public SingleXZInputStream(InputStream var1, int var2, boolean var3, ArrayCache var4) throws IOException {
      this(var1, var2, var3, readStreamHeader(var1), var4);
   }

   SingleXZInputStream(InputStream var1, int var2, boolean var3, byte[] var4, ArrayCache var5) throws IOException {
      this.blockDecoder = null;
      this.indexHash = new IndexHash();
      this.endReached = false;
      this.exception = null;
      this.tempBuf = new byte[1];
      this.arrayCache = var5;
      this.in = var1;
      this.memoryLimit = var2;
      this.verifyCheck = var3;
      this.streamHeaderFlags = DecoderUtil.decodeStreamHeader(var4);
      this.check = Check.getInstance(this.streamHeaderFlags.checkType);
   }

   public int getCheckType() {
      return this.streamHeaderFlags.checkType;
   }

   public String getCheckName() {
      return this.check.getName();
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
                  if (this.blockDecoder == null) {
                     try {
                        this.blockDecoder = new BlockInputStream(this.in, this.check, this.verifyCheck, this.memoryLimit, -1L, -1L, this.arrayCache);
                     } catch (IndexIndicatorException var6) {
                        this.indexHash.validate(this.in);
                        this.validateStreamFooter();
                        this.endReached = true;
                        return var4 > 0 ? var4 : -1;
                     }
                  }

                  int var5 = this.blockDecoder.read(var1, var2, var3);
                  if (var5 > 0) {
                     var4 += var5;
                     var2 += var5;
                     var3 -= var5;
                  } else if (var5 == -1) {
                     this.indexHash.add(this.blockDecoder.getUnpaddedSize(), this.blockDecoder.getUncompressedSize());
                     this.blockDecoder = null;
                  }
               }
            } catch (IOException var7) {
               this.exception = var7;
               if (var4 == 0) {
                  throw var7;
               }
            }

            return var4;
         }
      } else {
         throw new IndexOutOfBoundsException();
      }
   }

   private void validateStreamFooter() throws IOException {
      byte[] var1 = new byte[12];
      (new DataInputStream(this.in)).readFully(var1);
      StreamFlags var2 = DecoderUtil.decodeStreamFooter(var1);
      if (!DecoderUtil.areStreamFlagsEqual(this.streamHeaderFlags, var2) || this.indexHash.getIndexSize() != var2.backwardSize) {
         throw new CorruptedInputException("XZ Stream Footer does not match Stream Header");
      }
   }

   public int available() throws IOException {
      if (this.in == null) {
         throw new XZIOException("Stream closed");
      } else if (this.exception != null) {
         throw this.exception;
      } else {
         return this.blockDecoder == null ? 0 : this.blockDecoder.available();
      }
   }

   public void close() throws IOException {
      this.close(true);
   }

   public void close(boolean var1) throws IOException {
      if (this.in != null) {
         if (this.blockDecoder != null) {
            this.blockDecoder.close();
            this.blockDecoder = null;
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
