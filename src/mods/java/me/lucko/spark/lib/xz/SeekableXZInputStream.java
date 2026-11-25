package me.lucko.spark.lib.xz;

import java.io.DataInputStream;
import java.io.EOFException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import me.lucko.spark.lib.xz.check.Check;
import me.lucko.spark.lib.xz.common.DecoderUtil;
import me.lucko.spark.lib.xz.common.StreamFlags;
import me.lucko.spark.lib.xz.index.BlockInfo;
import me.lucko.spark.lib.xz.index.IndexDecoder;

public class SeekableXZInputStream extends SeekableInputStream {
   private final ArrayCache arrayCache;
   private SeekableInputStream in;
   private final int memoryLimit;
   private int indexMemoryUsage;
   private final ArrayList<IndexDecoder> streams;
   private int checkTypes;
   private long uncompressedSize;
   private long largestBlockSize;
   private int blockCount;
   private final BlockInfo curBlockInfo;
   private final BlockInfo queriedBlockInfo;
   private Check check;
   private final boolean verifyCheck;
   private BlockInputStream blockDecoder;
   private long curPos;
   private long seekPos;
   private boolean seekNeeded;
   private boolean endReached;
   private IOException exception;
   private final byte[] tempBuf;

   public SeekableXZInputStream(SeekableInputStream var1) throws IOException {
      this(var1, -1);
   }

   public SeekableXZInputStream(SeekableInputStream var1, ArrayCache var2) throws IOException {
      this(var1, -1, var2);
   }

   public SeekableXZInputStream(SeekableInputStream var1, int var2) throws IOException {
      this(var1, var2, true);
   }

   public SeekableXZInputStream(SeekableInputStream var1, int var2, ArrayCache var3) throws IOException {
      this(var1, var2, true, var3);
   }

   public SeekableXZInputStream(SeekableInputStream var1, int var2, boolean var3) throws IOException {
      this(var1, var2, var3, ArrayCache.getDefaultCache());
   }

   public SeekableXZInputStream(SeekableInputStream var1, int var2, boolean var3, ArrayCache var4) throws IOException {
      this.indexMemoryUsage = 0;
      this.streams = new ArrayList();
      this.checkTypes = 0;
      this.uncompressedSize = 0L;
      this.largestBlockSize = 0L;
      this.blockCount = 0;
      this.blockDecoder = null;
      this.curPos = 0L;
      this.seekNeeded = false;
      this.endReached = false;
      this.exception = null;
      this.tempBuf = new byte[1];
      this.arrayCache = var4;
      this.verifyCheck = var3;
      this.in = var1;
      DataInputStream var5 = new DataInputStream(var1);
      var1.seek(0L);
      byte[] var6 = new byte[XZ.HEADER_MAGIC.length];
      var5.readFully(var6);
      if (!Arrays.equals(var6, XZ.HEADER_MAGIC)) {
         throw new XZFormatException();
      } else {
         long var17 = var1.length();
         if ((var17 & 3L) != 0L) {
            throw new CorruptedInputException("XZ file size is not a multiple of 4 bytes");
         } else {
            byte[] var8 = new byte[12];
            long var9 = 0L;

            while(var17 > 0L) {
               if (var17 < 12L) {
                  throw new CorruptedInputException();
               }

               var1.seek(var17 - 12L);
               var5.readFully(var8);
               if (var8[8] == 0 && var8[9] == 0 && var8[10] == 0 && var8[11] == 0) {
                  var9 += 4L;
                  var17 -= 4L;
               } else {
                  var17 = var17 - 12L;
                  StreamFlags var11 = DecoderUtil.decodeStreamFooter(var8);
                  if (var11.backwardSize >= var17) {
                     throw new CorruptedInputException("Backward Size in XZ Stream Footer is too big");
                  }

                  this.check = Check.getInstance(var11.checkType);
                  this.checkTypes |= 1 << var11.checkType;
                  var1.seek(var17 - var11.backwardSize);

                  IndexDecoder var12;
                  try {
                     var12 = new IndexDecoder(var1, var11, var9, var2);
                  } catch (MemoryLimitException var16) {
                     assert var2 >= 0;

                     throw new MemoryLimitException(var16.getMemoryNeeded() + this.indexMemoryUsage, var2 + this.indexMemoryUsage);
                  }

                  this.indexMemoryUsage += var12.getMemoryUsage();
                  if (var2 >= 0) {
                     var2 -= var12.getMemoryUsage();

                     assert var2 >= 0;
                  }

                  if (this.largestBlockSize < var12.getLargestBlockSize()) {
                     this.largestBlockSize = var12.getLargestBlockSize();
                  }

                  long var13 = var12.getStreamSize() - 12L;
                  if (var17 < var13) {
                     throw new CorruptedInputException("XZ Index indicates too big compressed size for the XZ Stream");
                  }

                  var17 = var17 - var13;
                  var1.seek(var17);
                  var5.readFully(var8);
                  StreamFlags var15 = DecoderUtil.decodeStreamHeader(var8);
                  if (!DecoderUtil.areStreamFlagsEqual(var15, var11)) {
                     throw new CorruptedInputException("XZ Stream Footer does not match Stream Header");
                  }

                  this.uncompressedSize += var12.getUncompressedSize();
                  if (this.uncompressedSize < 0L) {
                     throw new UnsupportedOptionsException("XZ file is too big");
                  }

                  this.blockCount += var12.getRecordCount();
                  if (this.blockCount < 0) {
                     throw new UnsupportedOptionsException("XZ file has over 2147483647 Blocks");
                  }

                  this.streams.add(var12);
                  var9 = 0L;
               }
            }

            assert var17 == 0L;

            this.memoryLimit = var2;
            IndexDecoder var19 = (IndexDecoder)this.streams.get(this.streams.size() - 1);

            for(int var20 = this.streams.size() - 2; var20 >= 0; --var20) {
               IndexDecoder var22 = (IndexDecoder)this.streams.get(var20);
               var22.setOffsets(var19);
               var19 = var22;
            }

            IndexDecoder var21 = (IndexDecoder)this.streams.get(this.streams.size() - 1);
            this.curBlockInfo = new BlockInfo(var21);
            this.queriedBlockInfo = new BlockInfo(var21);
         }
      }
   }

   public int getCheckTypes() {
      return this.checkTypes;
   }

   public int getIndexMemoryUsage() {
      return this.indexMemoryUsage;
   }

   public long getLargestBlockSize() {
      return this.largestBlockSize;
   }

   public int getStreamCount() {
      return this.streams.size();
   }

   public int getBlockCount() {
      return this.blockCount;
   }

   public long getBlockPos(int var1) {
      this.locateBlockByNumber(this.queriedBlockInfo, var1);
      return this.queriedBlockInfo.uncompressedOffset;
   }

   public long getBlockSize(int var1) {
      this.locateBlockByNumber(this.queriedBlockInfo, var1);
      return this.queriedBlockInfo.uncompressedSize;
   }

   public long getBlockCompPos(int var1) {
      this.locateBlockByNumber(this.queriedBlockInfo, var1);
      return this.queriedBlockInfo.compressedOffset;
   }

   public long getBlockCompSize(int var1) {
      this.locateBlockByNumber(this.queriedBlockInfo, var1);
      return this.queriedBlockInfo.unpaddedSize + 3L & -4L;
   }

   public int getBlockCheckType(int var1) {
      this.locateBlockByNumber(this.queriedBlockInfo, var1);
      return this.queriedBlockInfo.getCheckType();
   }

   public int getBlockNumber(long var1) {
      this.locateBlockByPos(this.queriedBlockInfo, var1);
      return this.queriedBlockInfo.blockNumber;
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
         } else {
            int var4 = 0;

            try {
               if (this.seekNeeded) {
                  this.seek();
               }

               if (this.endReached) {
                  return -1;
               }

               while(var3 > 0) {
                  if (this.blockDecoder == null) {
                     this.seek();
                     if (this.endReached) {
                        break;
                     }
                  }

                  int var7 = this.blockDecoder.read(var1, var2, var3);
                  if (var7 > 0) {
                     this.curPos += (long)var7;
                     var4 += var7;
                     var2 += var7;
                     var3 -= var7;
                  } else if (var7 == -1) {
                     this.blockDecoder = null;
                  }
               }
            } catch (IOException var6) {
               Object var5 = var6;
               if (var6 instanceof EOFException) {
                  var5 = new CorruptedInputException();
               }

               this.exception = (IOException)var5;
               if (var4 == 0) {
                  throw var5;
               }
            }

            return var4;
         }
      } else {
         throw new IndexOutOfBoundsException();
      }
   }

   public int available() throws IOException {
      if (this.in == null) {
         throw new XZIOException("Stream closed");
      } else if (this.exception != null) {
         throw this.exception;
      } else {
         return !this.endReached && !this.seekNeeded && this.blockDecoder != null ? this.blockDecoder.available() : 0;
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

   public long length() {
      return this.uncompressedSize;
   }

   public long position() throws IOException {
      if (this.in == null) {
         throw new XZIOException("Stream closed");
      } else {
         return this.seekNeeded ? this.seekPos : this.curPos;
      }
   }

   public void seek(long var1) throws IOException {
      if (this.in == null) {
         throw new XZIOException("Stream closed");
      } else if (var1 < 0L) {
         throw new XZIOException("Negative seek position: " + var1);
      } else {
         this.seekPos = var1;
         this.seekNeeded = true;
      }
   }

   public void seekToBlock(int var1) throws IOException {
      if (this.in == null) {
         throw new XZIOException("Stream closed");
      } else if (var1 >= 0 && var1 < this.blockCount) {
         this.seekPos = this.getBlockPos(var1);
         this.seekNeeded = true;
      } else {
         throw new XZIOException("Invalid XZ Block number: " + var1);
      }
   }

   private void seek() throws IOException {
      if (!this.seekNeeded) {
         if (this.curBlockInfo.hasNext()) {
            this.curBlockInfo.setNext();
            this.initBlockDecoder();
            return;
         }

         this.seekPos = this.curPos;
      }

      this.seekNeeded = false;
      if (this.seekPos >= this.uncompressedSize) {
         this.curPos = this.seekPos;
         if (this.blockDecoder != null) {
            this.blockDecoder.close();
            this.blockDecoder = null;
         }

         this.endReached = true;
      } else {
         this.endReached = false;
         this.locateBlockByPos(this.curBlockInfo, this.seekPos);
         if (this.curPos <= this.curBlockInfo.uncompressedOffset || this.curPos > this.seekPos) {
            this.in.seek(this.curBlockInfo.compressedOffset);
            this.check = Check.getInstance(this.curBlockInfo.getCheckType());
            this.initBlockDecoder();
            this.curPos = this.curBlockInfo.uncompressedOffset;
         }

         if (this.seekPos > this.curPos) {
            long var1 = this.seekPos - this.curPos;
            if (this.blockDecoder.skip(var1) != var1) {
               throw new CorruptedInputException();
            }

            this.curPos = this.seekPos;
         }

      }
   }

   private void locateBlockByPos(BlockInfo var1, long var2) {
      if (var2 >= 0L && var2 < this.uncompressedSize) {
         int var5 = 0;

         while(true) {
            IndexDecoder var4 = (IndexDecoder)this.streams.get(var5);
            if (var4.hasUncompressedOffset(var2)) {
               var4.locateBlock(var1, var2);

               assert (var1.compressedOffset & 3L) == 0L;

               assert var1.uncompressedSize > 0L;

               assert var2 >= var1.uncompressedOffset;

               assert var2 < var1.uncompressedOffset + var1.uncompressedSize;

               return;
            }

            ++var5;
         }
      } else {
         throw new IndexOutOfBoundsException("Invalid uncompressed position: " + var2);
      }
   }

   private void locateBlockByNumber(BlockInfo var1, int var2) {
      if (var2 >= 0 && var2 < this.blockCount) {
         if (var1.blockNumber != var2) {
            int var3 = 0;

            while(true) {
               IndexDecoder var4 = (IndexDecoder)this.streams.get(var3);
               if (var4.hasRecord(var2)) {
                  var4.setBlockInfo(var1, var2);
                  return;
               }

               ++var3;
            }
         }
      } else {
         throw new IndexOutOfBoundsException("Invalid XZ Block number: " + var2);
      }
   }

   private void initBlockDecoder() throws IOException {
      try {
         if (this.blockDecoder != null) {
            this.blockDecoder.close();
            this.blockDecoder = null;
         }

         this.blockDecoder = new BlockInputStream(this.in, this.check, this.verifyCheck, this.memoryLimit, this.curBlockInfo.unpaddedSize, this.curBlockInfo.uncompressedSize, this.arrayCache);
      } catch (MemoryLimitException var2) {
         assert this.memoryLimit >= 0;

         throw new MemoryLimitException(var2.getMemoryNeeded() + this.indexMemoryUsage, this.memoryLimit + this.indexMemoryUsage);
      } catch (IndexIndicatorException var3) {
         throw new CorruptedInputException();
      }
   }
}
