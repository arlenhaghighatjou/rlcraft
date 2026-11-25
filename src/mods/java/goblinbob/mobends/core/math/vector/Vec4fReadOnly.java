package goblinbob.mobends.core.math.vector;

public class Vec4fReadOnly implements IVec4fRead {
   private final float x;
   private final float y;
   private final float z;
   private final float w;

   public Vec4fReadOnly(float x, float y, float z, float w) {
      this.x = x;
      this.y = y;
      this.z = z;
      this.w = w;
   }

   public Vec4fReadOnly(IVec4fRead other) {
      this.x = other.getX();
      this.y = other.getY();
      this.z = other.getZ();
      this.w = other.getW();
   }

   public Vec4fReadOnly() {
      this(0.0F, 0.0F, 0.0F, 0.0F);
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

   public float getW() {
      return this.w;
   }
}
