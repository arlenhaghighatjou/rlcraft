package me.lucko.spark.lib.xz.index;

import java.io.EOFException;
import java.io.IOException;
import java.util.zip.CRC32;
import java.util.zip.CheckedInputStream;
import me.lucko.spark.lib.xz.CorruptedInputException;
import me.lucko.spark.lib.xz.MemoryLimitException;
import me.lucko.spark.lib.xz.SeekableInputStream;
import me.lucko.spark.lib.xz.UnsupportedOptionsException;
import me.lucko.spark.lib.xz.common.DecoderUtil;
import me.lucko.spark.lib.xz.common.StreamFlags;

public class IndexDecoder extends IndexBase {
   private final StreamFlags streamFlags;
   private final long streamPadding;
   private final int memoryUsage;
   private final long[] unpadded;
   private final long[] uncompressed;
   private long largestBlockSize = 0L;
   private int recordOffset = 0;
   private long compressedOffset = 0L;
   private long uncompressedOffset = 0L;

   public IndexDecoder(SeekableInputStream var1, StreamFlags var2, long var3, int var5) throws IOException {
      super(new CorruptedInputException("XZ Index is corrupt"));
      this.streamFlags = var2;
      this.streamPadding = var3;
      long var6 = var1.position() + var2.backwardSize - 4L;
      CRC32 var8 = new CRC32();
      CheckedInputStream var9 = new CheckedInputStream(var1, var8);
      if (var9.read() != 0) {
         throw new CorruptedInputException("XZ Index is corrupt");
      } else {
         try {
            long var10 = DecoderUtil.decodeVLI(var9);
            if (var10 >= var2.backwardSize / 2L) {
               throw new CorruptedInputException("XZ Index is corrupt");
            }

            if (var10 > 2147483647L) {
               throw new UnsupportedOptionsException("XZ Index has over 2147483647 Records");
            }

            this.memoryUsage = 1 + (int)((16L * var10 + 1023L) / 1024L);
            if (var5 >= 0 && this.memoryUsage > var5) {
               throw new MemoryLimitException(this.memoryUsage, var5);
            }

            this.unpadded = new long[(int)var10];
            this.uncompressed = new long[(int)var10];
            int var12 = 0;

            for(int var13 = (int)var10; var13 > 0; --var13) {
               long var14 = DecoderUtil.decodeVLI(var9);
               long var16 = DecoderUtil.decodeVLI(var9);
               if (var1.position() > var6) {
                  throw new CorruptedInputException("XZ Index is corrupt");
               }

               this.unpadded[var12] = this.blocksSum + var14;
               this.uncompressed[var12] = this.uncompressedSum + var16;
               ++var12;
               super.add(var14, var16);

               assert (long)var12 == this.recordCount;

               if (this.largestBlockSize < var16) {
                  this.largestBlockSize = var16;
               }
            }
         } catch (EOFException var18) {
            throw new CorruptedInputException("XZ Index is corrupt");
         }

         int var19 = this.getIndexPaddingSize();
         if (var1.position() + (long)var19 != var6) {
            throw new CorruptedInputException("XZ Index is corrupt");
         } else {
            while(var19-- > 0) {
               if (var9.read() != 0) {
                  throw new CorruptedInputException("XZ Index is corrupt");
               }
            }

            long var11 = var8.getValue();

            for(int var20 = 0; var20 < 4; ++var20) {
               if ((var11 >>> var20 * 8 & 255L) != (long)var1.read()) {
                  throw new CorruptedInputException("XZ Index is corrupt");
               }
            }

         }
      }
   }

   public void setOffsets(IndexDecoder var1) {
      this.recordOffset = var1.recordOffset + (int)var1.recordCount;
      this.compressedOffset = var1.compressedOffset + var1.getStreamSize() + var1.streamPadding;

      assert (this.compressedOffset & 3L) == 0L;

      this.uncompressedOffset = var1.uncompressedOffset + var1.uncompressedSum;
   }

   public int getMemoryUsage() {
      return this.memoryUsage;
   }

   public StreamFlags getStreamFlags() {
      return this.streamFlags;
   }

   public int getRecordCount() {
      return (int)this.recordCount;
   }

   public long getUncompressedSize() {
      return this.uncompressedSum;
   }

   public long getLargestBlockSize() {
      return this.largestBlockSize;
   }

   public boolean hasUncompressedOffset(long var1) {
      return var1 >= this.uncompressedOffset && var1 < this.uncompressedOffset + this.uncompressedSum;
   }

   public boolean hasRecord(int var1) {
      return var1 >= this.recordOffset && (long)var1 < (long)this.recordOffset + this.recordCount;
   }

   public void locateBlock(BlockInfo var1, long var2) {
      assert var2 >= this.uncompressedOffset;

      var2 = var2 - this.uncompressedOffset;

      assert var2 < this.uncompressedSum;

      int var4 = 0;
      int var5 = this.unpadded.length - 1;

      while(var4 < var5) {
         int var6 = var4 + (var5 - var4) / 2;
         if (this.uncompressed[var6] <= var2) {
            var4 = var6 + 1;
         } else {
            var5 = var6;
         }
      }

      this.setBlockInfo(var1, this.recordOffset + var4);
   }

   public void setBlockInfo(BlockInfo var1, int var2) {
      assert var2 >= this.recordOffset;

      assert (long)(var2 - this.recordOffset) < this.recordCount;

      var1.index = this;
      var1.blockNumber = var2;
      int var3 = var2 - this.recordOffset;
      if (var3 == 0) {
         var1.compressedOffset = 0L;
         var1.uncompressedOffset = 0L;
      } else {
         var1.compressedOffset = this.unpadded[var3 - 1] + 3L & -4L;
         var1.uncompressedOffset = this.uncompressed[var3 - 1];
      }

      var1.unpaddedSize = this.unpadded[var3] - var1.compressedOffset;
      var1.uncompressedSize = this.uncompressed[var3] - var1.uncompressedOffset;
      var1.compressedOffset += this.compressedOffset + 12L;
      var1.uncompressedOffset += this.uncompressedOffset;
   }
}
