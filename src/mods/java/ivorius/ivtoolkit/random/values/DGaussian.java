package ivorius.ivtoolkit.random.values;

import net.lax1dude.eaglercraft.v1_8.EaglercraftRandom;

public class DGaussian implements DValue {
   public double min;
   public double max;

   public DGaussian(double min, double max) {
      this.min = min;
      this.max = max;
   }

   public Double getValue(EaglercraftRandom random) {
      return this.min + this.max * 0.5D + (random.nextDouble() - random.nextDouble()) * (this.max - this.min) * 0.5D;
   }
}
