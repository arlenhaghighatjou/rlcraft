package goblinbob.mobends.core.math.vector;

public class Vec3fReadonly implements IVec3fRead {
   private final float x;
   private final float y;
   private final float z;

   public Vec3fReadonly(float x, float y, float z) {
      this.x = x;
      this.y = y;
      this.z = z;
   }

   public Vec3fReadonly(IVec3fRead other) {
      this.x = other.getX();
      this.y = other.getY();
      this.z = other.getZ();
   }

   public Vec3fReadonly() {
      this(0.0F, 0.0F, 0.0F);
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
