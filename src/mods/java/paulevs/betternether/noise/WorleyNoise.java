package paulevs.betternether.noise;

public class WorleyNoise {
   private PermutationTable randX;
   private PermutationTable randY;

   public WorleyNoise(long seed) {
      this.randX = new PermutationTable(seed);
      this.randY = new PermutationTable(seed + 1337L);
   }

   public double GetValue(double x, double y) {
      int px = (int)x;
      int py = (int)y;
      x = x - (double)px;
      y = y - (double)py;
      double d = 10.0D;

      for(int i = -1; i < 2; ++i) {
         int cx = px + i & 255;

         for(int j = -1; j < 2; ++j) {
            int cy = py + j & 255;
            double nd = Math.sqrt(this.sqr(x - this.randX.PosReal(cx, cy) - (double)i) + this.sqr(y - this.randY.PosReal(-cy, -cx) - (double)j));
            if (nd < d) {
               d = nd;
            }
         }
      }

      return d;
   }

   private double sqr(double x) {
      return x * x;
   }
}
