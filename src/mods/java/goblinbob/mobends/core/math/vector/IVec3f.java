package goblinbob.mobends.core.math.vector;

public interface IVec3f extends IVec3fRead {
   void set(float var1, float var2, float var3);

   void setX(float var1);

   void setY(float var1);

   void setZ(float var1);

   void add(float var1, float var2, float var3);

   default void set(IVec3fRead vector) {
      this.set(vector.getX(), vector.getY(), vector.getZ());
   }

   default void add(IVec3fRead vector) {
      this.add(vector.getX(), vector.getY(), vector.getZ());
   }

   default void scale(float x, float y, float z) {
      this.set(this.getX() * x, this.getY() * y, this.getZ() * z);
   }

   default void scale(float a) {
      this.set(this.getX() * a, this.getY() * a, this.getZ() * a);
   }
}
