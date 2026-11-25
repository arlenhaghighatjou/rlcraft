package ivorius.ivtoolkit.random.values;

import net.lax1dude.eaglercraft.v1_8.EaglercraftRandom;

public class IConstant implements IValue {
   public int constant;

   public IConstant(int constant) {
      this.constant = constant;
   }

   public Integer getValue(EaglercraftRandom random) {
      return this.constant;
   }
}
