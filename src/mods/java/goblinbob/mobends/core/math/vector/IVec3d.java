package goblinbob.mobends.core.math.vector;

public interface IVec3d extends IVec3dRead {
   void set(double var1, double var3, double var5);

   void setX(double var1);

   void setY(double var1);

   void setZ(double var1);

   void add(double var1, double var3, double var5);

   default void set(IVec3fRead vector) {
      this.set((double)vector.getX(), (double)vector.getY(), (double)vector.getZ());
   }

   default void add(IVec3fRead vector) {
      this.add((double)vector.getX(), (double)vector.getY(), (double)vector.getZ());
   }

   default void scale(double x, double y, double z) {
      this.set(this.getX() * x, this.getY() * y, this.getZ() * z);
   }

   default void scale(double a) {
      this.set(this.getX() * a, this.getY() * a, this.getZ() * a);
   }
}
