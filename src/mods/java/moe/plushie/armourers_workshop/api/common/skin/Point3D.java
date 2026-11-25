package moe.plushie.armourers_workshop.api.common.skin;

import moe.plushie.armourers_workshop.api.common.IPoint3D;

public class Point3D implements IPoint3D {
   private final int x;
   private final int y;
   private final int z;

   public Point3D(int x, int y, int z) {
      this.x = x;
      this.y = y;
      this.z = z;
   }

   public int getX() {
      return this.x;
   }

   public int getY() {
      return this.y;
   }

   public int getZ() {
      return this.z;
   }

   public String toString() {
      return "Point3D [x=" + this.x + ", y=" + this.y + ", z=" + this.z + "]";
   }
}
