package paulevs.betternether.noise;

public class WorleyNoise3D {
   private PermutationTable randX;
   private PermutationTable randY;
   private PermutationTable randZ;

   public WorleyNoise3D(long seed) {
      this.randX = new PermutationTable(seed);
      this.randY = new PermutationTable(seed + 1337L);
      this.randZ = new PermutationTable(seed + 2673L);
   }

   public double GetValue(double x, double y, double z) {
      int px = (int)x;
      int py = (int)y;
      int pz = (int)z;
      x = x - (double)px;
      y = y - (double)py;
      z = z - (double)pz;
      double d = 10.0D;

      for(int i = -1; i < 2; ++i) {
         int cx = px + i & 255;

         for(int j = -1; j < 2; ++j) {
            int cy = py + j & 255;

            for(int k = -1; k < 2; ++k) {
               int cz = pz + k & 255;
               double ox = this.sqr(x - this.randX.PosReal(cx, -cy) - (double)i);
               double oy = this.sqr(y - this.randY.PosReal(-cz, -cx) - (double)j);
               double oz = this.sqr(z - this.randZ.PosReal(-cx, cy) - (double)k);
               double nd = Math.sqrt(ox + oy + oz);
               if (nd < d) {
                  d = nd;
               }
            }
         }
      }

      return d;
   }

   private double sqr(double x) {
      return x * x;
   }
}
