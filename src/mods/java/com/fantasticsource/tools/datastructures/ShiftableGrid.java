package com.fantasticsource.tools.datastructures;

import com.fantasticsource.tools.Tools;

public class ShiftableGrid<T> {
   private Object[][] values;
   private int xOffset = 0;
   private int yOffset = 0;
   private int w;
   private int h;

   public ShiftableGrid(int width, int height) {
      if (width >= 0 && height >= 0) {
         this.w = width;
         this.h = height;
         this.values = new Object[this.w][this.h];
      } else {
         throw new IllegalArgumentException("Width and height must both be >= 0. Given values: w = " + width + ", h = " + height);
      }
   }

   public void set(int x, int y, T value) {
      this.values[Tools.posMod(x + this.xOffset, this.w)][Tools.posMod(y + this.yOffset, this.h)] = value;
   }

   public T get(int x, int y) {
      return (T)this.values[Tools.posMod(x + this.xOffset, this.w)][Tools.posMod(y + this.yOffset, this.h)];
   }

   public void clear() {
      this.values = new Object[this.w][this.h];
   }

   public void shift(int x, int y) {
      if (x < 0) {
         for(int ix = 0; ix > x && ix > -this.w; --ix) {
            this.clearColumn(this.xOffset);
            ++this.xOffset;
            if (this.xOffset == this.w) {
               this.xOffset = 0;
            }
         }
      } else {
         for(int ix = 0; ix < x && ix < this.w; ++ix) {
            if (this.xOffset == 0) {
               this.xOffset = this.w;
            }

            --this.xOffset;
            this.clearColumn(this.xOffset);
         }
      }

      if (y < 0) {
         for(int iy = 0; iy > y && iy > -this.h; --iy) {
            this.clearRow(this.yOffset);
            ++this.yOffset;
            if (this.yOffset == this.h) {
               this.yOffset = 0;
            }
         }
      } else {
         for(int iy = 0; iy < y && iy < this.h; ++iy) {
            if (this.yOffset == 0) {
               this.yOffset = this.h;
            }

            --this.yOffset;
            this.clearRow(this.yOffset);
         }
      }

   }

   private void clearRow(int y) {
      for(int i = 0; i < this.h; ++i) {
         this.values[i][y] = null;
      }

   }

   private void clearColumn(int x) {
      for(int i = 0; i < this.h; ++i) {
         this.values[x][i] = null;
      }

   }

   public String toString(int x, int y) {
      T t = (T)this.get(x, y);
      return t == null ? "" : t.toString();
   }

   public String toString() {
      if (this.w != 0 && this.h != 0) {
         StringBuilder result = new StringBuilder();

         for(int iy = 0; iy < this.h; ++iy) {
            result.append(this.toString(0, iy));

            for(int ix = 1; ix < this.w; ++ix) {
               result.append(", ").append(this.toString(ix, iy));
            }

            result.append("\r\n");
         }

         return result.toString();
      } else {
         return "Grid can't hold anything (width = " + this.w + ", height = " + this.h + ")";
      }
   }

   public void print() {
      System.out.println(this.toString());
   }
}
