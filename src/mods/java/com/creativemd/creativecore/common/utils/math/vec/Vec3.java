package com.creativemd.creativecore.common.utils.math.vec;

public class Vec3 extends Vec {
   public double x;
   public double y;
   public double z;

   public Vec3(Vec vec) {
      super(vec);
   }

   public Vec3(double x, double y, double z) {
      this.x = x;
      this.y = y;
      this.z = z;
   }

   public double getValueByDim(int dim) {
      if (dim == 0) {
         return this.x;
      } else if (dim == 1) {
         return this.y;
      } else {
         return dim == 2 ? this.z : 0.0D;
      }
   }

   public void setValueByDim(int dim, double value) {
      if (dim == 0) {
         this.x = value;
      }

      if (dim == 1) {
         this.y = value;
      }

      if (dim == 2) {
         this.z = value;
      }

   }

   public int getDimensionCount() {
      return 3;
   }
}
