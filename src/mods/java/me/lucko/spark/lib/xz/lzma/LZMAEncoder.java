package me.lucko.spark.lib.xz.lzma;

import java.io.IOException;
import me.lucko.spark.lib.xz.ArrayCache;
import me.lucko.spark.lib.xz.lz.LZEncoder;
import me.lucko.spark.lib.xz.lz.Matches;
import me.lucko.spark.lib.xz.rangecoder.RangeEncoder;

public abstract class LZMAEncoder extends LZMACoder {
   public static final int MODE_FAST = 1;
   public static final int MODE_NORMAL = 2;
   private static final int LZMA2_UNCOMPRESSED_LIMIT = 2096879;
   private static final int LZMA2_COMPRESSED_LIMIT = 65510;
   private static final int DIST_PRICE_UPDATE_INTERVAL = 128;
   private static final int ALIGN_PRICE_UPDATE_INTERVAL = 16;
   private final RangeEncoder rc;
   final LZEncoder lz;
   final LZMAEncoder.LiteralEncoder literalEncoder;
   final LZMAEncoder.LengthEncoder matchLenEncoder;
   final LZMAEncoder.LengthEncoder repLenEncoder;
   final int niceLen;
   private int distPriceCount = 0;
   private int alignPriceCount = 0;
   private final int distSlotPricesSize;
   private final int[][] distSlotPrices;
   private final int[][] fullDistPrices = new int[4][128];
   private final int[] alignPrices = new int[16];
   int back = 0;
   int readAhead = -1;
   private int uncompressedSize = 0;

   public static int getMemoryUsage(int var0, int var1, int var2, int var3) {
      int var4 = 80;
      switch(var0) {
      case 1:
         var4 = var4 + LZMAEncoderFast.getMemoryUsage(var1, var2, var3);
         break;
      case 2:
         var4 = var4 + LZMAEncoderNormal.getMemoryUsage(var1, var2, var3);
         break;
      default:
         throw new IllegalArgumentException();
      }

      return var4;
   }

   public static LZMAEncoder getInstance(RangeEncoder var0, int var1, int var2, int var3, int var4, int var5, int var6, int var7, int var8, int var9, ArrayCache var10) {
      switch(var4) {
      case 1:
         return new LZMAEncoderFast(var0, var1, var2, var3, var5, var6, var7, var8, var9, var10);
      case 2:
         return new LZMAEncoderNormal(var0, var1, var2, var3, var5, var6, var7, var8, var9, var10);
      default:
         throw new IllegalArgumentException();
      }
   }

   public void putArraysToCache(ArrayCache var1) {
      this.lz.putArraysToCache(var1);
   }

   public static int getDistSlot(int var0) {
      if (var0 <= 4 && var0 >= 0) {
         return var0;
      } else {
         int var1 = var0;
         int var2 = 31;
         if ((var0 & -65536) == 0) {
            var1 = var0 << 16;
            var2 = 15;
         }

         if ((var1 & -16777216) == 0) {
            var1 <<= 8;
            var2 -= 8;
         }

         if ((var1 & -268435456) == 0) {
            var1 <<= 4;
            var2 -= 4;
         }

         if ((var1 & -1073741824) == 0) {
            var1 <<= 2;
            var2 -= 2;
         }

         if ((var1 & Integer.MIN_VALUE) == 0) {
            --var2;
         }

         return (var2 << 1) + (var0 >>> var2 - 1 & 1);
      }
   }

   abstract int getNextSymbol();

   LZMAEncoder(RangeEncoder var1, LZEncoder var2, int var3, int var4, int var5, int var6, int var7) {
      super(var5);
      this.rc = var1;
      this.lz = var2;
      this.niceLen = var7;
      this.literalEncoder = new LZMAEncoder.LiteralEncoder(var3, var4);
      this.matchLenEncoder = new LZMAEncoder.LengthEncoder(var5, var7);
      this.repLenEncoder = new LZMAEncoder.LengthEncoder(var5, var7);
      this.distSlotPricesSize = getDistSlot(var6 - 1) + 1;
      this.distSlotPrices = new int[4][this.distSlotPricesSize];
      this.reset();
   }

   public LZEncoder getLZEncoder() {
      return this.lz;
   }

   public void reset() {
      super.reset();
      this.literalEncoder.reset();
      this.matchLenEncoder.reset();
      this.repLenEncoder.reset();
      this.distPriceCount = 0;
      this.alignPriceCount = 0;
      this.uncompressedSize += this.readAhead + 1;
      this.readAhead = -1;
   }

   public int getUncompressedSize() {
      return this.uncompressedSize;
   }

   public void resetUncompressedSize() {
      this.uncompressedSize = 0;
   }

   public void encodeForLZMA1() throws IOException {
      if (this.lz.isStarted() || this.encodeInit()) {
         while(this.encodeSymbol()) {
            ;
         }

      }
   }

   public void encodeLZMA1EndMarker() throws IOException {
      int var1 = this.lz.getPos() - this.readAhead & this.posMask;
      this.rc.encodeBit(this.isMatch[this.state.get()], var1, 1);
      this.rc.encodeBit(this.isRep, this.state.get(), 0);
      this.encodeMatch(-1, 2, var1);
   }

   public boolean encodeForLZMA2() {
      try {
         if (!this.lz.isStarted() && !this.encodeInit()) {
            return false;
         } else {
            while(this.uncompressedSize <= 2096879 && this.rc.getPendingSize() <= 65510) {
               if (!this.encodeSymbol()) {
                  return false;
               }
            }

            return true;
         }
      } catch (IOException var2) {
         throw new Error();
      }
   }

   private boolean encodeInit() throws IOException {
      assert this.readAhead == -1;

      if (!this.lz.hasEnoughData(0)) {
         return false;
      } else {
         this.skip(1);
         this.rc.encodeBit(this.isMatch[this.state.get()], 0, 0);
         this.literalEncoder.encodeInit();
         --this.readAhead;

         assert this.readAhead == -1;

         ++this.uncompressedSize;

         assert this.uncompressedSize == 1;

         return true;
      }
   }

   private boolean encodeSymbol() throws IOException {
      if (!this.lz.hasEnoughData(this.readAhead + 1)) {
         return false;
      } else {
         int var1 = this.getNextSymbol();

         assert this.readAhead >= 0;

         int var2 = this.lz.getPos() - this.readAhead & this.posMask;
         if (this.back == -1) {
            assert var1 == 1;

            this.rc.encodeBit(this.isMatch[this.state.get()], var2, 0);
            this.literalEncoder.encode();
         } else {
            this.rc.encodeBit(this.isMatch[this.state.get()], var2, 1);
            if (this.back < 4) {
               assert this.lz.getMatchLen(-this.readAhead, this.reps[this.back], var1) == var1;

               this.rc.encodeBit(this.isRep, this.state.get(), 1);
               this.encodeRepMatch(this.back, var1, var2);
            } else {
               assert this.lz.getMatchLen(-this.readAhead, this.back - 4, var1) == var1;

               this.rc.encodeBit(this.isRep, this.state.get(), 0);
               this.encodeMatch(this.back - 4, var1, var2);
            }
         }

         this.readAhead -= var1;
         this.uncompressedSize += var1;
         return true;
      }
   }

   private void encodeMatch(int var1, int var2, int var3) throws IOException {
      this.state.updateMatch();
      this.matchLenEncoder.encode(var2, var3);
      int var4 = getDistSlot(var1);
      this.rc.encodeBitTree(this.distSlots[getDistState(var2)], var4);
      if (var4 >= 4) {
         int var5 = (var4 >>> 1) - 1;
         int var6 = (2 | var4 & 1) << var5;
         int var7 = var1 - var6;
         if (var4 < 14) {
            this.rc.encodeReverseBitTree(this.distSpecial[var4 - 4], var7);
         } else {
            this.rc.encodeDirectBits(var7 >>> 4, var5 - 4);
            this.rc.encodeReverseBitTree(this.distAlign, var7 & 15);
            --this.alignPriceCount;
         }
      }

      this.reps[3] = this.reps[2];
      this.reps[2] = this.reps[1];
      this.reps[1] = this.reps[0];
      this.reps[0] = var1;
      --this.distPriceCount;
   }

   private void encodeRepMatch(int var1, int var2, int var3) throws IOException {
      if (var1 == 0) {
         this.rc.encodeBit(this.isRep0, this.state.get(), 0);
         this.rc.encodeBit(this.isRep0Long[this.state.get()], var3, var2 == 1 ? 0 : 1);
      } else {
         int var4 = this.reps[var1];
         this.rc.encodeBit(this.isRep0, this.state.get(), 1);
         if (var1 == 1) {
            this.rc.encodeBit(this.isRep1, this.state.get(), 0);
         } else {
            this.rc.encodeBit(this.isRep1, this.state.get(), 1);
            this.rc.encodeBit(this.isRep2, this.state.get(), var1 - 2);
            if (var1 == 3) {
               this.reps[3] = this.reps[2];
            }

            this.reps[2] = this.reps[1];
         }

         this.reps[1] = this.reps[0];
         this.reps[0] = var4;
      }

      if (var2 == 1) {
         this.state.updateShortRep();
      } else {
         this.repLenEncoder.encode(var2, var3);
         this.state.updateLongRep();
      }

   }

   Matches getMatches() {
      ++this.readAhead;
      Matches var1 = this.lz.getMatches();

      assert this.lz.verifyMatches(var1);

      return var1;
   }

   void skip(int var1) {
      this.readAhead += var1;
      this.lz.skip(var1);
   }

   int getAnyMatchPrice(State var1, int var2) {
      return RangeEncoder.getBitPrice(this.isMatch[var1.get()][var2], 1);
   }

   int getNormalMatchPrice(int var1, State var2) {
      return var1 + RangeEncoder.getBitPrice(this.isRep[var2.get()], 0);
   }

   int getAnyRepPrice(int var1, State var2) {
      return var1 + RangeEncoder.getBitPrice(this.isRep[var2.get()], 1);
   }

   int getShortRepPrice(int var1, State var2, int var3) {
      return var1 + RangeEncoder.getBitPrice(this.isRep0[var2.get()], 0) + RangeEncoder.getBitPrice(this.isRep0Long[var2.get()][var3], 0);
   }

   int getLongRepPrice(int var1, int var2, State var3, int var4) {
      int var5;
      if (var2 == 0) {
         var5 = var1 + RangeEncoder.getBitPrice(this.isRep0[var3.get()], 0) + RangeEncoder.getBitPrice(this.isRep0Long[var3.get()][var4], 1);
      } else {
         var5 = var1 + RangeEncoder.getBitPrice(this.isRep0[var3.get()], 1);
         if (var2 == 1) {
            var5 = var5 + RangeEncoder.getBitPrice(this.isRep1[var3.get()], 0);
         } else {
            var5 = var5 + RangeEncoder.getBitPrice(this.isRep1[var3.get()], 1) + RangeEncoder.getBitPrice(this.isRep2[var3.get()], var2 - 2);
         }
      }

      return var5;
   }

   int getLongRepAndLenPrice(int var1, int var2, State var3, int var4) {
      int var5 = this.getAnyMatchPrice(var3, var4);
      int var6 = this.getAnyRepPrice(var5, var3);
      int var7 = this.getLongRepPrice(var6, var1, var3, var4);
      return var7 + this.repLenEncoder.getPrice(var2, var4);
   }

   int getMatchAndLenPrice(int var1, int var2, int var3, int var4) {
      int var5 = var1 + this.matchLenEncoder.getPrice(var3, var4);
      int var6 = getDistState(var3);
      if (var2 < 128) {
         var5 = var5 + this.fullDistPrices[var6][var2];
      } else {
         int var7 = getDistSlot(var2);
         var5 = var5 + this.distSlotPrices[var6][var7] + this.alignPrices[var2 & 15];
      }

      return var5;
   }

   private void updateDistPrices() {
      this.distPriceCount = 128;

      for(int var1 = 0; var1 < 4; ++var1) {
         for(int var2 = 0; var2 < this.distSlotPricesSize; ++var2) {
            this.distSlotPrices[var1][var2] = RangeEncoder.getBitTreePrice(this.distSlots[var1], var2);
         }

         for(int var11 = 14; var11 < this.distSlotPricesSize; ++var11) {
            int var3 = (var11 >>> 1) - 1 - 4;
            this.distSlotPrices[var1][var11] += RangeEncoder.getDirectBitsPrice(var3);
         }

         for(int var12 = 0; var12 < 4; ++var12) {
            this.fullDistPrices[var1][var12] = this.distSlotPrices[var1][var12];
         }
      }

      int var10 = 4;

      for(int var13 = 4; var13 < 14; ++var13) {
         int var14 = (var13 >>> 1) - 1;
         int var4 = (2 | var13 & 1) << var14;
         int var5 = this.distSpecial[var13 - 4].length;

         for(int var6 = 0; var6 < var5; ++var6) {
            int var7 = var10 - var4;
            int var8 = RangeEncoder.getReverseBitTreePrice(this.distSpecial[var13 - 4], var7);

            for(int var9 = 0; var9 < 4; ++var9) {
               this.fullDistPrices[var9][var10] = this.distSlotPrices[var9][var13] + var8;
            }

            ++var10;
         }
      }

      assert var10 == 128;

   }

   private void updateAlignPrices() {
      this.alignPriceCount = 16;

      for(int var1 = 0; var1 < 16; ++var1) {
         this.alignPrices[var1] = RangeEncoder.getReverseBitTreePrice(this.distAlign, var1);
      }

   }

   void updatePrices() {
      if (this.distPriceCount <= 0) {
         this.updateDistPrices();
      }

      if (this.alignPriceCount <= 0) {
         this.updateAlignPrices();
      }

      this.matchLenEncoder.updatePrices();
      this.repLenEncoder.updatePrices();
   }

   class LengthEncoder extends LZMACoder.LengthCoder {
      private static final int PRICE_UPDATE_INTERVAL = 32;
      private final int[] counters;
      private final int[][] prices;

      LengthEncoder(int var2, int var3) {
         super();
         int var4 = 1 << var2;
         this.counters = new int[var4];
         int var5 = Math.max(var3 - 2 + 1, 16);
         this.prices = new int[var4][var5];
      }

      void reset() {
         super.reset();

         for(int var1 = 0; var1 < this.counters.length; ++var1) {
            this.counters[var1] = 0;
         }

      }

      void encode(int var1, int var2) throws IOException {
         var1 = var1 - 2;
         if (var1 < 8) {
            LZMAEncoder.this.rc.encodeBit(this.choice, 0, 0);
            LZMAEncoder.this.rc.encodeBitTree(this.low[var2], var1);
         } else {
            LZMAEncoder.this.rc.encodeBit(this.choice, 0, 1);
            var1 = var1 - 8;
            if (var1 < 8) {
               LZMAEncoder.this.rc.encodeBit(this.choice, 1, 0);
               LZMAEncoder.this.rc.encodeBitTree(this.mid[var2], var1);
            } else {
               LZMAEncoder.this.rc.encodeBit(this.choice, 1, 1);
               LZMAEncoder.this.rc.encodeBitTree(this.high, var1 - 8);
            }
         }

         --this.counters[var2];
      }

      int getPrice(int var1, int var2) {
         return this.prices[var2][var1 - 2];
      }

      void updatePrices() {
         for(int var1 = 0; var1 < this.counters.length; ++var1) {
            if (this.counters[var1] <= 0) {
               this.counters[var1] = 32;
               this.updatePrices(var1);
            }
         }

      }

      private void updatePrices(int var1) {
         int var2 = RangeEncoder.getBitPrice(this.choice[0], 0);

         int var3;
         for(var3 = 0; var3 < 8; ++var3) {
            this.prices[var1][var3] = var2 + RangeEncoder.getBitTreePrice(this.low[var1], var3);
         }

         var2 = RangeEncoder.getBitPrice(this.choice[0], 1);

         for(int var4 = RangeEncoder.getBitPrice(this.choice[1], 0); var3 < 16; ++var3) {
            this.prices[var1][var3] = var2 + var4 + RangeEncoder.getBitTreePrice(this.mid[var1], var3 - 8);
         }

         for(int var6 = RangeEncoder.getBitPrice(this.choice[1], 1); var3 < this.prices[var1].length; ++var3) {
            this.prices[var1][var3] = var2 + var6 + RangeEncoder.getBitTreePrice(this.high, var3 - 8 - 8);
         }

      }
   }

   class LiteralEncoder extends LZMACoder.LiteralCoder {
      private final LZMAEncoder.LiteralEncoder.LiteralSubencoder[] subencoders;

      LiteralEncoder(int var2, int var3) {
         super(var2, var3);
         this.subencoders = new LZMAEncoder.LiteralEncoder.LiteralSubencoder[1 << var2 + var3];

         for(int var4 = 0; var4 < this.subencoders.length; ++var4) {
            this.subencoders[var4] = new LZMAEncoder.LiteralEncoder.LiteralSubencoder();
         }

      }

      void reset() {
         for(int var1 = 0; var1 < this.subencoders.length; ++var1) {
            this.subencoders[var1].reset();
         }

      }

      void encodeInit() throws IOException {
         assert LZMAEncoder.this.readAhead >= 0;

         this.subencoders[0].encode();
      }

      void encode() throws IOException {
         assert LZMAEncoder.this.readAhead >= 0;

         int var1 = this.getSubcoderIndex(LZMAEncoder.this.lz.getByte(1 + LZMAEncoder.this.readAhead), LZMAEncoder.this.lz.getPos() - LZMAEncoder.this.readAhead);
         this.subencoders[var1].encode();
      }

      int getPrice(int var1, int var2, int var3, int var4, State var5) {
         int var6 = RangeEncoder.getBitPrice(LZMAEncoder.this.isMatch[var5.get()][var4 & LZMAEncoder.this.posMask], 0);
         int var7 = this.getSubcoderIndex(var3, var4);
         var6 = var6 + (var5.isLiteral() ? this.subencoders[var7].getNormalPrice(var1) : this.subencoders[var7].getMatchedPrice(var1, var2));
         return var6;
      }

      private class LiteralSubencoder extends LZMACoder.LiteralCoder.LiteralSubcoder {
         private LiteralSubencoder() {
            super();
         }

         void encode() throws IOException {
            int var1 = LZMAEncoder.this.lz.getByte(LZMAEncoder.this.readAhead) | 256;
            if (LZMAEncoder.this.state.isLiteral()) {
               while(true) {
                  int var2 = var1 >>> 8;
                  int var3 = var1 >>> 7 & 1;
                  LZMAEncoder.this.rc.encodeBit(this.probs, var2, var3);
                  var1 <<= 1;
                  if (var1 >= 65536) {
                     break;
                  }
               }
            } else {
               int var7 = LZMAEncoder.this.lz.getByte(LZMAEncoder.this.reps[0] + 1 + LZMAEncoder.this.readAhead);
               int var8 = 256;

               while(true) {
                  var7 <<= 1;
                  int var5 = var7 & var8;
                  int var4 = var8 + var5 + (var1 >>> 8);
                  int var6 = var1 >>> 7 & 1;
                  LZMAEncoder.this.rc.encodeBit(this.probs, var4, var6);
                  var1 <<= 1;
                  var8 &= ~(var7 ^ var1);
                  if (var1 >= 65536) {
                     break;
                  }
               }
            }

            LZMAEncoder.this.state.updateLiteral();
         }

         int getNormalPrice(int var1) {
            int var2 = 0;
            var1 = var1 | 256;

            while(true) {
               int var3 = var1 >>> 8;
               int var4 = var1 >>> 7 & 1;
               var2 += RangeEncoder.getBitPrice(this.probs[var3], var4);
               var1 <<= 1;
               if (var1 >= 65536) {
                  break;
               }
            }

            return var2;
         }

         int getMatchedPrice(int var1, int var2) {
            int var3 = 0;
            int var4 = 256;
            var1 = var1 | 256;

            while(true) {
               var2 <<= 1;
               int var6 = var2 & var4;
               int var5 = var4 + var6 + (var1 >>> 8);
               int var7 = var1 >>> 7 & 1;
               var3 += RangeEncoder.getBitPrice(this.probs[var5], var7);
               var1 <<= 1;
               var4 &= ~(var2 ^ var1);
               if (var1 >= 65536) {
                  break;
               }
            }

            return var3;
         }
      }
   }
}
