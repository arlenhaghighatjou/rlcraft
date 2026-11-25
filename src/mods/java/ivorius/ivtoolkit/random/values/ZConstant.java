package ivorius.ivtoolkit.random.values;

import net.lax1dude.eaglercraft.v1_8.EaglercraftRandom;

public class ZConstant implements ZValue {
   public boolean value;

   public ZConstant(boolean value) {
      this.value = value;
   }

   public Boolean getValue(EaglercraftRandom random) {
      return this.value;
   }
}
