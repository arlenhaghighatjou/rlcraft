package me.lucko.spark.lib.xz.lzma;

import java.io.IOException;
import me.lucko.spark.lib.xz.lz.LZDecoder;
import me.lucko.spark.lib.xz.rangecoder.RangeDecoder;

public final class LZMADecoder extends LZMACoder {
   private final LZDecoder lz;
   private final RangeDecoder rc;
   private final LZMADecoder.LiteralDecoder literalDecoder;
   private final LZMADecoder.LengthDecoder matchLenDecoder = new LZMADecoder.LengthDecoder();
   private final LZMADecoder.LengthDecoder repLenDecoder = new LZMADecoder.LengthDecoder();

   public LZMADecoder(LZDecoder var1, RangeDecoder var2, int var3, int var4, int var5) {
      super(var5);
      this.lz = var1;
      this.rc = var2;
      this.literalDecoder = new LZMADecoder.LiteralDecoder(var3, var4);
      this.reset();
   }

   public void reset() {
      super.reset();
      this.literalDecoder.reset();
      this.matchLenDecoder.reset();
      this.repLenDecoder.reset();
   }

   public boolean endMarkerDetected() {
      return this.reps[0] == -1;
   }

   public void decode() throws IOException {
      this.lz.repeatPending();

      while(this.lz.hasSpace()) {
         int var1 = this.lz.getPos() & this.posMask;
         if (this.rc.decodeBit(this.isMatch[this.state.get()], var1) == 0) {
            this.literalDecoder.decode();
         } else {
            int var2 = this.rc.decodeBit(this.isRep, this.state.get()) == 0 ? this.decodeMatch(var1) : this.decodeRepMatch(var1);
            this.lz.repeat(this.reps[0], var2);
         }
      }

      this.rc.normalize();
   }

   private int decodeMatch(int var1) throws IOException {
      this.state.updateMatch();
      this.reps[3] = this.reps[2];
      this.reps[2] = this.reps[1];
      this.reps[1] = this.reps[0];
      int var2 = this.matchLenDecoder.decode(var1);
      int var3 = this.rc.decodeBitTree(this.distSlots[getDistState(var2)]);
      if (var3 < 4) {
         this.reps[0] = var3;
      } else {
         int var4 = (var3 >> 1) - 1;
         this.reps[0] = (2 | var3 & 1) << var4;
         if (var3 < 14) {
            this.reps[0] |= this.rc.decodeReverseBitTree(this.distSpecial[var3 - 4]);
         } else {
            this.reps[0] |= this.rc.decodeDirectBits(var4 - 4) << 4;
            this.reps[0] |= this.rc.decodeReverseBitTree(this.distAlign);
         }
      }

      return var2;
   }

   private int decodeRepMatch(int var1) throws IOException {
      if (this.rc.decodeBit(this.isRep0, this.state.get()) == 0) {
         if (this.rc.decodeBit(this.isRep0Long[this.state.get()], var1) == 0) {
            this.state.updateShortRep();
            return 1;
         }
      } else {
         int var2;
         if (this.rc.decodeBit(this.isRep1, this.state.get()) == 0) {
            var2 = this.reps[1];
         } else {
            if (this.rc.decodeBit(this.isRep2, this.state.get()) == 0) {
               var2 = this.reps[2];
            } else {
               var2 = this.reps[3];
               this.reps[3] = this.reps[2];
            }

            this.reps[2] = this.reps[1];
         }

         this.reps[1] = this.reps[0];
         this.reps[0] = var2;
      }

      this.state.updateLongRep();
      return this.repLenDecoder.decode(var1);
   }

   private class LengthDecoder extends LZMACoder.LengthCoder {
      private LengthDecoder() {
         super();
      }

      int decode(int var1) throws IOException {
         if (LZMADecoder.this.rc.decodeBit(this.choice, 0) == 0) {
            return LZMADecoder.this.rc.decodeBitTree(this.low[var1]) + 2;
         } else {
            return LZMADecoder.this.rc.decodeBit(this.choice, 1) == 0 ? LZMADecoder.this.rc.decodeBitTree(this.mid[var1]) + 2 + 8 : LZMADecoder.this.rc.decodeBitTree(this.high) + 2 + 8 + 8;
         }
      }
   }

   private class LiteralDecoder extends LZMACoder.LiteralCoder {
      private final LZMADecoder.LiteralDecoder.LiteralSubdecoder[] subdecoders;

      LiteralDecoder(int var2, int var3) {
         super(var2, var3);
         this.subdecoders = new LZMADecoder.LiteralDecoder.LiteralSubdecoder[1 << var2 + var3];

         for(int var4 = 0; var4 < this.subdecoders.length; ++var4) {
            this.subdecoders[var4] = new LZMADecoder.LiteralDecoder.LiteralSubdecoder();
         }

      }

      void reset() {
         for(int var1 = 0; var1 < this.subdecoders.length; ++var1) {
            this.subdecoders[var1].reset();
         }

      }

      void decode() throws IOException {
         int var1 = this.getSubcoderIndex(LZMADecoder.this.lz.getByte(0), LZMADecoder.this.lz.getPos());
         this.subdecoders[var1].decode();
      }

      private class LiteralSubdecoder extends LZMACoder.LiteralCoder.LiteralSubcoder {
         private LiteralSubdecoder() {
            super();
         }

         void decode() throws IOException {
            int var1 = 1;
            if (LZMADecoder.this.state.isLiteral()) {
               while(true) {
                  var1 = var1 << 1 | LZMADecoder.this.rc.decodeBit(this.probs, var1);
                  if (var1 >= 256) {
                     break;
                  }
               }
            } else {
               int var2 = LZMADecoder.this.lz.getByte(LZMADecoder.this.reps[0]);
               int var3 = 256;

               while(true) {
                  var2 <<= 1;
                  int var4 = var2 & var3;
                  int var5 = LZMADecoder.this.rc.decodeBit(this.probs, var3 + var4 + var1);
                  var1 = var1 << 1 | var5;
                  var3 &= 0 - var5 ^ ~var4;
                  if (var1 >= 256) {
                     break;
                  }
               }
            }

            LZMADecoder.this.lz.putByte((byte)var1);
            LZMADecoder.this.state.updateLiteral();
         }
      }
   }
}
