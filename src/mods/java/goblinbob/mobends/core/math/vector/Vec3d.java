package goblinbob.mobends.core.math.vector;

public class Vec3d implements IVec3d {
   public static final Vec3dReadonly ZERO = new Vec3dReadonly(0.0F, 0.0F, 0.0F);
   public static final Vec3dReadonly ONE = new Vec3dReadonly(1.0F, 1.0F, 1.0F);
   public double x;
   public double y;
   public double z;

   public Vec3d(double x, double y, double z) {
      this.x = x;
      this.y = y;
      this.z = z;
   }

   public Vec3d(IVec3fRead other) {
      this.x = (double)other.getX();
      this.y = (double)other.getY();
      this.z = (double)other.getZ();
   }

   public Vec3d() {
      this(0.0D, 0.0D, 0.0D);
   }

   public void set(double x, double y, double z) {
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

   public void add(double x, double y, double z) {
      this.x += x;
      this.y += y;
      this.z += z;
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

   public double lengthSq() {
      return this.x * this.x + this.y * this.y + this.z * this.z;
   }
}
