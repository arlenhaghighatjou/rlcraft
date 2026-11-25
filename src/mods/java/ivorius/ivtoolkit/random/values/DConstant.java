package ivorius.ivtoolkit.random.values;

import net.lax1dude.eaglercraft.v1_8.EaglercraftRandom;

public class DConstant implements DValue {
   public double constant;

   public DConstant(double constant) {
      this.constant = constant;
   }

   public Double getValue(EaglercraftRandom random) {
      return this.constant;
   }
}
