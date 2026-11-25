package goblinbob.mobends.core.math.vector;

public class Vec3dReadonly implements IVec3dRead {
   private final double x;
   private final double y;
   private final double z;

   public Vec3dReadonly(float x, float y, float z) {
      this.x = (double)x;
      this.y = (double)y;
      this.z = (double)z;
   }

   public Vec3dReadonly(IVec3dRead other) {
      this.x = other.getX();
      this.y = other.getY();
      this.z = other.getZ();
   }

   public Vec3dReadonly() {
      this(0.0F, 0.0F, 0.0F);
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
