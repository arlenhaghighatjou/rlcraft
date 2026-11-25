package ivorius.ivtoolkit.random.values;

import java.util.Random;

public class DGaussian implements DValue {
   public double min;
   public double max;

   public DGaussian(double min, double max) {
      this.min = min;
      this.max = max;
   }

   public Double getValue(Random random) {
      return this.min + this.max * 0.5D + (random.nextDouble() - random.nextDouble()) * (this.max - this.min) * 0.5D;
   }
}
