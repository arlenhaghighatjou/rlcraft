package me.lucko.spark.lib.xz;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import me.lucko.spark.lib.xz.check.Check;
import me.lucko.spark.lib.xz.common.DecoderUtil;

class BlockInputStream extends InputStream {
   private final DataInputStream inData;
   private final CountingInputStream inCounted;
   private InputStream filterChain;
   private final Check check;
   private final boolean verifyCheck;
   private long uncompressedSizeInHeader = -1L;
   private long compressedSizeInHeader = -1L;
   private long compressedSizeLimit;
   private final int headerSize;
   private long uncompressedSize = 0L;
   private boolean endReached = false;
   private final byte[] tempBuf = new byte[1];

   public BlockInputStream(InputStream var1, Check var2, boolean var3, int var4, long var5, long var7, ArrayCache var9) throws IOException, IndexIndicatorException {
      this.check = var2;
      this.verifyCheck = var3;
      this.inData = new DataInputStream(var1);
      byte[] var10 = new byte[1024];
      this.inData.readFully(var10, 0, 1);
      if (var10[0] == 0) {
         throw new IndexIndicatorException();
      } else {
         this.headerSize = 4 * ((var10[0] & 255) + 1);
         this.inData.readFully(var10, 1, this.headerSize - 1);
         if (!DecoderUtil.isCRC32Valid(var10, 0, this.headerSize - 4, this.headerSize - 4)) {
            throw new CorruptedInputException("XZ Block Header is corrupt");
         } else if ((var10[1] & 60) != 0) {
            throw new UnsupportedOptionsException("Unsupported options in XZ Block Header");
         } else {
            int var11 = (var10[1] & 3) + 1;
            long[] var12 = new long[var11];
            byte[][] var13 = new byte[var11][];
            ByteArrayInputStream var14 = new ByteArrayInputStream(var10, 2, this.headerSize - 6);

            try {
               this.compressedSizeLimit = 9223372036854775804L - (long)this.headerSize - (long)var2.getSize();
               if ((var10[1] & 64) != 0) {
                  this.compressedSizeInHeader = DecoderUtil.decodeVLI(var14);
                  if (this.compressedSizeInHeader == 0L || this.compressedSizeInHeader > this.compressedSizeLimit) {
                     throw new CorruptedInputException();
                  }

                  this.compressedSizeLimit = this.compressedSizeInHeader;
               }

               if ((var10[1] & 128) != 0) {
                  this.uncompressedSizeInHeader = DecoderUtil.decodeVLI(var14);
               }

               for(int var15 = 0; var15 < var11; ++var15) {
                  var12[var15] = DecoderUtil.decodeVLI(var14);
                  long var16 = DecoderUtil.decodeVLI(var14);
                  if (var16 > (long)var14.available()) {
                     throw new CorruptedInputException();
                  }

                  var13[var15] = new byte[(int)var16];
                  var14.read(var13[var15]);
               }
            } catch (IOException var18) {
               throw new CorruptedInputException("XZ Block Header is corrupt");
            }

            for(int var19 = var14.available(); var19 > 0; --var19) {
               if (var14.read() != 0) {
                  throw new UnsupportedOptionsException("Unsupported options in XZ Block Header");
               }
            }

            if (var5 != -1L) {
               int var20 = this.headerSize + var2.getSize();
               if ((long)var20 >= var5) {
                  throw new CorruptedInputException("XZ Index does not match a Block Header");
               }

               long var22 = var5 - (long)var20;
               if (var22 > this.compressedSizeLimit || this.compressedSizeInHeader != -1L && this.compressedSizeInHeader != var22) {
                  throw new CorruptedInputException("XZ Index does not match a Block Header");
               }

               if (this.uncompressedSizeInHeader != -1L && this.uncompressedSizeInHeader != var7) {
                  throw new CorruptedInputException("XZ Index does not match a Block Header");
               }

               this.compressedSizeLimit = var22;
               this.compressedSizeInHeader = var22;
               this.uncompressedSizeInHeader = var7;
            }

            FilterDecoder[] var21 = new FilterDecoder[var12.length];

            for(int var23 = 0; var23 < var21.length; ++var23) {
               if (var12[var23] == 33L) {
                  var21[var23] = new LZMA2Decoder(var13[var23]);
               } else if (var12[var23] == 3L) {
                  var21[var23] = new DeltaDecoder(var13[var23]);
               } else {
                  if (!BCJDecoder.isBCJFilterID(var12[var23])) {
                     throw new UnsupportedOptionsException("Unknown Filter ID " + var12[var23]);
                  }

                  var21[var23] = new BCJDecoder(var12[var23], var13[var23]);
               }
            }

            RawCoder.validate(var21);
            if (var4 >= 0) {
               int var24 = 0;

               for(int var17 = 0; var17 < var21.length; ++var17) {
                  var24 += var21[var17].getMemoryUsage();
               }

               if (var24 > var4) {
                  throw new MemoryLimitException(var24, var4);
               }
            }

            this.inCounted = new CountingInputStream(var1);
            this.filterChain = this.inCounted;

            for(int var25 = var21.length - 1; var25 >= 0; --var25) {
               this.filterChain = var21[var25].getInputStream(this.filterChain, var9);
            }

         }
      }
   }

   public int read() throws IOException {
      return this.read(this.tempBuf, 0, 1) == -1 ? -1 : this.tempBuf[0] & 255;
   }

   public int read(byte[] var1, int var2, int var3) throws IOException {
      if (this.endReached) {
         return -1;
      } else {
         int var4 = this.filterChain.read(var1, var2, var3);
         if (var4 > 0) {
            if (this.verifyCheck) {
               this.check.update(var1, var2, var4);
            }

            this.uncompressedSize += (long)var4;
            long var5 = this.inCounted.getSize();
            if (var5 < 0L || var5 > this.compressedSizeLimit || this.uncompressedSize < 0L || this.uncompressedSizeInHeader != -1L && this.uncompressedSize > this.uncompressedSizeInHeader) {
               throw new CorruptedInputException();
            }

            if (var4 < var3 || this.uncompressedSize == this.uncompressedSizeInHeader) {
               if (this.filterChain.read() != -1) {
                  throw new CorruptedInputException();
               }

               this.validate();
               this.endReached = true;
            }
         } else if (var4 == -1) {
            this.validate();
            this.endReached = true;
         }

         return var4;
      }
   }

   private void validate() throws IOException {
      long var1 = this.inCounted.getSize();
      if ((this.compressedSizeInHeader == -1L || this.compressedSizeInHeader == var1) && (this.uncompressedSizeInHeader == -1L || this.uncompressedSizeInHeader == this.uncompressedSize)) {
         while((var1++ & 3L) != 0L) {
            if (this.inData.readUnsignedByte() != 0) {
               throw new CorruptedInputException();
            }
         }

         byte[] var3 = new byte[this.check.getSize()];
         this.inData.readFully(var3);
         if (this.verifyCheck && !Arrays.equals(this.check.finish(), var3)) {
            throw new CorruptedInputException("Integrity check (" + this.check.getName() + ") does not match");
         }
      } else {
         throw new CorruptedInputException();
      }
   }

   public int available() throws IOException {
      return this.filterChain.available();
   }

   public void close() {
      try {
         this.filterChain.close();
      } catch (IOException var2) {
         assert false;
      }

      this.filterChain = null;
   }

   public long getUnpaddedSize() {
      return (long)this.headerSize + this.inCounted.getSize() + (long)this.check.getSize();
   }

   public long getUncompressedSize() {
      return this.uncompressedSize;
   }
}
