package goblinbob.mobends.core.math.vector;

public class Vec4f implements IVec4f {
   public static final Vec4fReadOnly ZERO = new Vec4fReadOnly(0.0F, 0.0F, 0.0F, 0.0F);
   public float x;
   public float y;
   public float z;
   public float w;

   public Vec4f(float x, float y, float z, float w) {
      this.x = x;
      this.y = y;
      this.z = z;
      this.w = w;
   }

   public Vec4f(IVec4fRead other) {
      this.x = other.getX();
      this.y = other.getY();
      this.z = other.getZ();
      this.w = other.getW();
   }

   public Vec4f() {
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

   public void set(float x, float y, float z, float w) {
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

   public void setW(float w) {
      this.w = w;
   }

   public void add(float x, float y, float z, float w) {
      this.x += x;
      this.y += y;
      this.z += z;
      this.w += w;
   }
}
