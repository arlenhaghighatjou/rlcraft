package ivorius.ivtoolkit.random.values;

import java.util.Random;

public class IConstant implements IValue {
   public int constant;

   public IConstant(int constant) {
      this.constant = constant;
   }

   public Integer getValue(Random random) {
      return this.constant;
   }
}
