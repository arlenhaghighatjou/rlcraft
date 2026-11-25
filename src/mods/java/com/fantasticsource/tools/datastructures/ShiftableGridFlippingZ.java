package com.fantasticsource.tools.datastructures;

import com.fantasticsource.tools.Tools;

public class ShiftableGridFlippingZ<T> extends ShiftableGrid<T> {
   private ShiftableGridFlippingZ[] shiftableGrids = new ShiftableGridFlippingZ[2];

   private ShiftableGridFlippingZ(int width, int height, boolean flag) {
      super(width, height);
   }

   public ShiftableGridFlippingZ(int width, int height) {
      super(width, height);
      this.shiftableGrids[0] = this;
      this.shiftableGrids[1] = new ShiftableGridFlippingZ(width, height, true);
   }

   public void set(int x, int y, int z, T value) {
      this.shiftableGrids[Tools.posMod(z, 2)].set(x, y, value);
   }

   public T get(int x, int y, int z) {
      return (T)this.shiftableGrids[Tools.posMod(z, 2)].get(x, y);
   }

   public void clearBoth() {
      this.shiftableGrids[0].clear();
      this.shiftableGrids[1].clear();
   }

   public void shift(int x, int y) {
      this.shift(x, y, 0);
   }

   private void shift(int x, int y, boolean flag) {
      super.shift(x, y);
   }

   public void shift(int x, int y, int z) {
      this.shiftableGrids[0].shift(x, y, true);
      this.shiftableGrids[1].shift(x, y, true);
      if (Tools.posMod(z, 2) == 1) {
         ShiftableGridFlippingZ sg = this.shiftableGrids[0];
         this.shiftableGrids[0] = this.shiftableGrids[1];
         this.shiftableGrids[1] = sg;
      }

   }

   private String toString(boolean flag) {
      return super.toString();
   }

   public String toString() {
      return this.shiftableGrids[0].toString(true) + "\r\n" + this.shiftableGrids[1].toString(true);
   }
}
