package ivorius.ivtoolkit.random.values;

import java.util.Random;

public class DLinear implements DValue {
   public double min;
   public double max;

   public DLinear(double min, double max) {
      this.min = min;
      this.max = max;
   }

   public Double getValue(Random random) {
      return this.min + random.nextDouble() * (this.max - this.min);
   }
}
