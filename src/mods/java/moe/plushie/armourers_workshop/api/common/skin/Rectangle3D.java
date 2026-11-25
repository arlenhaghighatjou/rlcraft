package moe.plushie.armourers_workshop.api.common.skin;

import moe.plushie.armourers_workshop.api.common.IRectangle3D;

public class Rectangle3D implements IRectangle3D {
   private int x;
   private int y;
   private int z;
   private int width;
   private int height;
   private int depth;

   public Rectangle3D(int x, int y, int z, int width, int height, int depth) {
      this.x = x;
      this.y = y;
      this.z = z;
      this.width = width;
      this.height = height;
      this.depth = depth;
   }

   public void setX(int x) {
      this.x = x;
   }

   public int getX() {
      return this.x;
   }

   public void setY(int y) {
      this.y = y;
   }

   public int getY() {
      return this.y;
   }

   public void setZ(int z) {
      this.z = z;
   }

   public int getZ() {
      return this.z;
   }

   public void setWidth(int width) {
      this.width = width;
   }

   public int getWidth() {
      return this.width;
   }

   public void setHeight(int height) {
      this.height = height;
   }

   public int getHeight() {
      return this.height;
   }

   public void setDepth(int depth) {
      this.depth = depth;
   }

   public int getDepth() {
      return this.depth;
   }

   public String toString() {
      return "Rectangle3D [x=" + this.x + ", y=" + this.y + ", z=" + this.z + ", width=" + this.width + ", height=" + this.height + ", depth=" + this.depth + "]";
   }
}
