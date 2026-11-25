package goblinbob.mobends.core.math.matrix;

public interface IMatd {
   double[] getFields();

   double get(int var1, int var2);

   int getCols();

   int getRows();

   void set(int var1, int var2, double var3);

   void setFields(double... var1);

   void scale(double var1);
}
