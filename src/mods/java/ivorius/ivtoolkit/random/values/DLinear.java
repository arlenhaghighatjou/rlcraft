package ivorius.ivtoolkit.random.values;

import net.lax1dude.eaglercraft.v1_8.EaglercraftRandom;

public class DLinear implements DValue {
   public double min;
   public double max;

   public DLinear(double min, double max) {
      this.min = min;
      this.max = max;
   }

   public Double getValue(EaglercraftRandom random) {
      return this.min + random.nextDouble() * (this.max - this.min);
   }
}
