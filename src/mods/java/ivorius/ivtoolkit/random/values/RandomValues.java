package ivorius.ivtoolkit.random.values;

import net.lax1dude.eaglercraft.v1_8.EaglercraftRandom;

public class RandomValues {
   public RandomValues() {
   }

   public static int[] getValueRange(IValue value, EaglercraftRandom random) {
      int[] result = new int[2];
      int val1 = value.getValue(random).intValue();
      int val2 = value.getValue(random).intValue();
      boolean val1F = val1 < val2;
      result[0] = val1F ? val1 : val2;
      result[1] = val1F ? val2 : val1;
      return result;
   }

   public static double[] getValueRange(DValue value, EaglercraftRandom random) {
      double[] result = new double[2];
      double val1 = value.getValue(random).doubleValue();
      double val2 = value.getValue(random).doubleValue();
      boolean val1F = val1 < val2;
      result[0] = val1F ? val1 : val2;
      result[1] = val1F ? val2 : val1;
      return result;
   }
}
