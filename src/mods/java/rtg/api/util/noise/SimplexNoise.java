package rtg.api.util.noise;

public interface SimplexNoise {
   float noise2f(float var1, float var2);

   float noise3f(float var1, float var2, float var3);

   double noise2d(double var1, double var3);

   double noise3d(double var1, double var3, double var5);

   void multiEval2D(double var1, double var3, ISimplexData2D var5);
}
