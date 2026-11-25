package goblinbob.mobends.core.math.vector;

public interface IVec4d extends IVec4dRead {
   void set(double var1, double var3, double var5, double var7);

   void setX(double var1);

   void setY(double var1);

   void setZ(double var1);

   void setW(double var1);

   void add(double var1, double var3, double var5, double var7);

   default void set(IVec4dRead vector) {
      this.set(vector.getX(), vector.getY(), vector.getZ(), vector.getW());
   }

   default void add(IVec4dRead vector) {
      this.add(vector.getX(), vector.getY(), vector.getZ(), vector.getW());
   }

   default void scale(double x, double y, double z, double w) {
      this.set(this.getX() * x, this.getY() * y, this.getZ() * z, this.getW() * w);
   }

   default void scale(double a) {
      this.set(this.getX() * a, this.getY() * a, this.getZ() * a, this.getW() * a);
   }
}
