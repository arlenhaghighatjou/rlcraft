package rtg.api.util.noise;

public interface ISimplexData2D {
   void addToDeltaX(double var1);

   void addToDeltaY(double var1);

   double getDeltaX();

   void setDeltaX(double var1);

   double getDeltaY();

   void setDeltaY(double var1);

   void clear();

   ISimplexData2D.DataRequest request();

   @FunctionalInterface
   public interface DataRequest {
      void apply(double var1, double var3, double var5, double var7, int var9, double var10, double var12);
   }
}
