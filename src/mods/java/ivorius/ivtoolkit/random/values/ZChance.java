package ivorius.ivtoolkit.random.values;

import java.util.Random;

public class ZChance implements ZValue {
   public double chance;

   public ZChance(double chance) {
      this.chance = chance;
   }

   public Boolean getValue(Random random) {
      return random.nextDouble() < this.chance;
   }
}
