package goblinbob.mobends.core.math.vector;

public class Vec3f implements IVec3f {
   public static final Vec3fReadonly ZERO = new Vec3fReadonly(0.0F, 0.0F, 0.0F);
   public float x;
   public float y;
   public float z;

   public Vec3f(float x, float y, float z) {
      this.x = x;
      this.y = y;
      this.z = z;
   }

   public Vec3f(IVec3fRead other) {
      this.x = other.getX();
      this.y = other.getY();
      this.z = other.getZ();
   }

   public Vec3f() {
      this(0.0F, 0.0F, 0.0F);
   }

   public void set(float x, float y, float z) {
      this.x = x;
      this.y = y;
      this.z = z;
   }

   public void setX(float x) {
      this.x = x;
   }

   public void setY(float y) {
      this.y = y;
   }

   public void setZ(float z) {
      this.z = z;
   }

   public void add(float x, float y, float z) {
      this.x += x;
      this.y += y;
      this.z += z;
   }

   public float getX() {
      return this.x;
   }

   public float getY() {
      return this.y;
   }

   public float getZ() {
      return this.z;
   }

   public float lengthSq() {
      return this.x * this.x + this.y * this.y + this.z * this.z;
   }
}
