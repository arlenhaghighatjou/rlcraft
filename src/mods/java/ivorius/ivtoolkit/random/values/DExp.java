package ivorius.ivtoolkit.random.values;

import net.lax1dude.eaglercraft.v1_8.EaglercraftRandom;

public class DExp implements DValue {
   public double min;
   public double max;
   public double exp;

   public DExp(double min, double max, double exp) {
      this.min = min;
      this.max = max;
      this.exp = exp;
   }

   public Double getValue(EaglercraftRandom random) {
      return this.min + (Math.pow(this.exp, random.nextDouble()) - 1.0D) / (this.exp - 1.0D) * (this.max - this.min);
   }
}
