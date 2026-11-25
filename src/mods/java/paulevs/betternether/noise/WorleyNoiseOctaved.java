package paulevs.betternether.noise;

public class WorleyNoiseOctaved {
   private WorleyNoise noise;
   private int octaves;
   private double[] multipliers;
   private double[] negativeMultipliers;
   private int[] offsets;

   public WorleyNoiseOctaved(long seed, int octaves) {
      this.octaves = octaves;
      this.noise = new WorleyNoise(seed);
      --octaves;
      this.multipliers = new double[octaves];
      this.negativeMultipliers = new double[octaves];
      this.offsets = new int[octaves];

      for(int i = 0; i < octaves; ++i) {
         this.offsets[i] = 2 << i;
         this.negativeMultipliers[i] = 1.0D / (double)this.offsets[i];
         this.multipliers[i] = 1.0D - this.negativeMultipliers[i];
      }

   }

   public double GetValue(double x, double y) {
      double result = this.noise.GetValue(x, y);

      for(int i = 0; i < this.octaves - 1; ++i) {
         result = result * this.multipliers[i] + this.noise.GetValue(x * (double)this.offsets[i], y * (double)this.offsets[i]) * this.negativeMultipliers[i];
      }

      return result;
   }
}
