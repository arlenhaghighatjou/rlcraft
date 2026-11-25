package com.ferreusveritas.dynamictrees.util;

public class SimpleBitmap {
   private final int h;
   private final int w;
   private int[] bits;
   public boolean touched;
   private static int dstOffsety;
   private static int srcOffsety;
   private static int runH;

   public SimpleBitmap(int w, int h) {
      this.w = net.minecraft.util.math.MathHelper.clamp(w, 1, 32);
      this.h = Math.max(1, h);
      this.bits = new int[this.h];
      this.touched = false;
   }

   public SimpleBitmap(int w, int h, int[] bits) {
      this(w, h);
      if (bits != null) {
         int size = Math.min(bits.length, this.h);

         for(int i = 0; i < size; ++i) {
            this.bits[i] = bits[i];
         }
      }

      this.touched = true;
   }

   public SimpleBitmap(SimpleBitmap bmp) {
      this(bmp.w, bmp.h);

      for(int i = 0; i < this.bits.length; ++i) {
         this.bits[i] = bmp.bits[i];
      }

      this.touched = bmp.touched;
   }

   public int[] getBits() {
      return this.bits;
   }

   public int getH() {
      return this.h;
   }

   public int getW() {
      return this.w;
   }

   public SimpleBitmap clear() {
      this.bits = new int[this.h];
      this.touched = false;
      return this;
   }

   public boolean isColliding(int relX, int relY, SimpleBitmap src) {
      if (this.prepBlit(relX, relY, src)) {
         if (relX < 0) {
            relX = -relX;

            while(runH-- > 0) {
               if ((this.bits[dstOffsety++] << relX & src.getBits()[srcOffsety++]) != 0) {
                  return true;
               }
            }
         } else {
            while(runH-- > 0) {
               if ((this.bits[dstOffsety++] & src.getBits()[srcOffsety++] << relX) != 0) {
                  return true;
               }
            }
         }
      }

      return false;
   }

   public void BlitOr(int relX, int relY, SimpleBitmap src) {
      if (this.prepBlit(relX, relY, src)) {
         int[] var7;
         int var8;
         if (relX < 0) {
            for(int var4 = -relX; runH-- > 0; var7[var8] |= src.getBits()[srcOffsety++] >>> var4) {
               var7 = this.bits;
               var8 = dstOffsety++;
            }
         } else {
            while(runH-- > 0) {
               var7 = this.bits;
               var8 = dstOffsety++;
               var7[var8] |= src.getBits()[srcOffsety++] << relX;
            }
         }

         this.touched = true;
      }

   }

   public void BlitSub(int relX, int relY, SimpleBitmap src) {
      if (this.prepBlit(relX, relY, src)) {
         int[] var7;
         int var8;
         if (relX < 0) {
            for(int var4 = -relX; runH-- > 0; var7[var8] &= ~(src.getBits()[srcOffsety++] >>> var4)) {
               var7 = this.bits;
               var8 = dstOffsety++;
            }
         } else {
            while(runH-- > 0) {
               var7 = this.bits;
               var8 = dstOffsety++;
               var7[var8] &= ~(src.getBits()[srcOffsety++] << relX);
            }
         }

         this.touched = true;
      }

   }

   public void BlitAnd(int relX, int relY, SimpleBitmap src) {
      if (this.prepBlit(relX, relY, src)) {
         int[] var7;
         int var8;
         if (relX < 0) {
            for(int var4 = -relX; runH-- > 0; var7[var8] &= src.getBits()[srcOffsety++] >>> var4) {
               var7 = this.bits;
               var8 = dstOffsety++;
            }
         } else {
            while(runH-- > 0) {
               var7 = this.bits;
               var8 = dstOffsety++;
               var7[var8] &= ~(src.getBits()[srcOffsety++] << relX);
            }
         }

         this.touched = true;
      }

   }

   private boolean prepBlit(int relX, int relY, SimpleBitmap src) {
      if (relX > -src.w && relX < this.w && relY > -src.h && relY < this.h && (this.touched || src.touched)) {
         if (relY >= 0) {
            dstOffsety = relY;
            srcOffsety = 0;
         } else {
            dstOffsety = 0;
            srcOffsety = -relY;
         }

         runH = Math.min(this.h - dstOffsety, Math.min(src.h, this.h - relY) - srcOffsety);
         return true;
      } else {
         return false;
      }
   }

   public void setPixel(int x, int y, int mode) {
      if (x >= 0 && y >= 0 && x < this.w && y < this.h) {
         mode = mode & 1;
         this.bits[y] = this.bits[y] & ~(mode << x) | mode << x;
         this.touched = true;
      }

   }

   public boolean isPixelOn(int x, int y) {
      return x >= 0 && y >= 0 && x < this.w && y < this.h && (this.bits[y] >> x & 1) == 1;
   }

   public boolean isRowBlank(int y) {
      return y >= 0 && y < this.h && this.bits[y] == 0;
   }

   public void print() {
      for(int y = 0; y < this.h; ++y) {
         String buff = "";

         for(int x = 0; x < this.w; ++x) {
            buff = buff + (this.isPixelOn(x, y) ? "\u00e2\u2013\u02c6" : "\u00e2\u2013\u2018");
         }

         System.out.println(buff);
      }

   }
}
