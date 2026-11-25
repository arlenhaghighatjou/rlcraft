package goblinbob.mobends.core.math.vector;

public interface IVec4f extends IVec4fRead {
   void set(float var1, float var2, float var3, float var4);

   void setX(float var1);

   void setY(float var1);

   void setZ(float var1);

   void setW(float var1);

   void add(float var1, float var2, float var3, float var4);

   default void set(IVec4fRead vector) {
      this.set(vector.getX(), vector.getY(), vector.getZ(), vector.getW());
   }

   default void add(IVec4fRead vector) {
      this.add(vector.getX(), vector.getY(), vector.getZ(), vector.getW());
   }

   default void scale(float x, float y, float z, float w) {
      this.set(this.getX() * x, this.getY() * y, this.getZ() * z, this.getW() * w);
   }

   default void scale(float a) {
      this.set(this.getX() * a, this.getY() * a, this.getZ() * a, this.getW() * a);
   }
}
