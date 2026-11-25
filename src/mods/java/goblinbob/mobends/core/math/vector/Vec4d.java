package goblinbob.mobends.core.math.vector;

public class Vec4d implements IVec4d {
   public double x;
   public double y;
   public double z;
   public double w;

   public Vec4d(double x, double y, double z, double w) {
      this.x = x;
      this.y = y;
      this.z = z;
      this.w = w;
   }

   public Vec4d(IVec4dRead other) {
      this.x = other.getX();
      this.y = other.getY();
      this.z = other.getZ();
      this.w = other.getW();
   }

   public Vec4d() {
      this(0.0D, 0.0D, 0.0D, 0.0D);
   }

   public double getX() {
      return this.x;
   }

   public double getY() {
      return this.y;
   }

   public double getZ() {
      return this.z;
   }

   public double getW() {
      return this.w;
   }

   public void set(double x, double y, double z, double w) {
      this.x = x;
      this.y = y;
      this.z = z;
   }

   public void setX(double x) {
      this.x = x;
   }

   public void setY(double y) {
      this.y = y;
   }

   public void setZ(double z) {
      this.z = z;
   }

   public void setW(double w) {
      this.w = w;
   }

   public void add(double x, double y, double z, double w) {
      this.x += x;
      this.y += y;
      this.z += z;
      this.w += w;
   }
}
