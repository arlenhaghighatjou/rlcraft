package me.lucko.spark.lib.xz.index;

import me.lucko.spark.lib.xz.XZIOException;
import me.lucko.spark.lib.xz.common.Util;

abstract class IndexBase {
   private final XZIOException invalidIndexException;
   long blocksSum = 0L;
   long uncompressedSum = 0L;
   long indexListSize = 0L;
   long recordCount = 0L;

   IndexBase(XZIOException var1) {
      this.invalidIndexException = var1;
   }

   private long getUnpaddedIndexSize() {
      return (long)(1 + Util.getVLISize(this.recordCount)) + this.indexListSize + 4L;
   }

   public long getIndexSize() {
      return this.getUnpaddedIndexSize() + 3L & -4L;
   }

   public long getStreamSize() {
      return 12L + this.blocksSum + this.getIndexSize() + 12L;
   }

   int getIndexPaddingSize() {
      return (int)(4L - this.getUnpaddedIndexSize() & 3L);
   }

   void add(long var1, long var3) throws XZIOException {
      this.blocksSum += var1 + 3L & -4L;
      this.uncompressedSum += var3;
      this.indexListSize += (long)(Util.getVLISize(var1) + Util.getVLISize(var3));
      ++this.recordCount;
      if (this.blocksSum < 0L || this.uncompressedSum < 0L || this.getIndexSize() > 17179869184L || this.getStreamSize() < 0L) {
         throw this.invalidIndexException;
      }
   }
}
