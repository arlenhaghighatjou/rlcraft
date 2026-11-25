package ivorius.ivtoolkit.random.values;

import java.util.Random;

public class IExp implements IValue {
   public int min;
   public int max;
   public double exp;

   public IExp(int min, int max, double exp) {
      this.min = min;
      this.max = max;
      this.exp = exp;
   }

   public Integer getValue(Random random) {
      return (int)Math.round((double)this.min + (Math.pow(this.exp, random.nextDouble()) - 1.0D) / (this.exp - 1.0D) * (double)(this.max - this.min));
   }
}
