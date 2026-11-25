package ivorius.ivtoolkit.random.values;

import java.util.Random;

public class DExp implements DValue {
   public double min;
   public double max;
   public double exp;

   public DExp(double min, double max, double exp) {
      this.min = min;
      this.max = max;
      this.exp = exp;
   }

   public Double getValue(Random random) {
      return this.min + (Math.pow(this.exp, random.nextDouble()) - 1.0D) / (this.exp - 1.0D) * (this.max - this.min);
   }
}
