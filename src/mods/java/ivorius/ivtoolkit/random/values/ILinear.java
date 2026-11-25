package ivorius.ivtoolkit.random.values;

import net.lax1dude.eaglercraft.v1_8.EaglercraftRandom;

public class ILinear implements IValue {
   public int min;
   public int max;

   public ILinear(int min, int max) {
      this.min = min;
      this.max = max;
   }

   public Integer getValue(EaglercraftRandom random) {
      return this.min + random.nextInt(this.max - this.min + 1);
   }
}
