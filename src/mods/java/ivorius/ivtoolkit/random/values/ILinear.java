package ivorius.ivtoolkit.random.values;

import java.util.Random;

public class ILinear implements IValue {
   public int min;
   public int max;

   public ILinear(int min, int max) {
      this.min = min;
      this.max = max;
   }

   public Integer getValue(Random random) {
      return this.min + random.nextInt(this.max - this.min + 1);
   }
}
