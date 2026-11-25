package com.creativemd.creativecore.common.utils.math.vec;

public class Vec2 extends Vec {
   public double x;
   public double y;

   public Vec2(Vec vec) {
      super(vec);
   }

   public Vec2(double x, double y) {
      this.x = x;
      this.y = y;
   }

   public double getValueByDim(int dim) {
      if (dim == 0) {
         return this.x;
      } else {
         return dim == 1 ? this.y : 0.0D;
      }
   }

   public void setValueByDim(int dim, double value) {
      if (dim == 0) {
         this.x = value;
      }

      if (dim == 1) {
         this.y = value;
      }

   }

   public int getDimensionCount() {
      return 2;
   }
}
