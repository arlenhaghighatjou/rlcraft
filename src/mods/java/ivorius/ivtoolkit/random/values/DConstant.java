package ivorius.ivtoolkit.random.values;

import java.util.Random;

public class DConstant implements DValue {
   public double constant;

   public DConstant(double constant) {
      this.constant = constant;
   }

   public Double getValue(Random random) {
      return this.constant;
   }
}
