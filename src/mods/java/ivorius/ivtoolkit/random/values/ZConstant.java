package ivorius.ivtoolkit.random.values;

import java.util.Random;

public class ZConstant implements ZValue {
   public boolean value;

   public ZConstant(boolean value) {
      this.value = value;
   }

   public Boolean getValue(Random random) {
      return this.value;
   }
}
