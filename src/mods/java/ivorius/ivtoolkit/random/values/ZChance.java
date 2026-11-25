package ivorius.ivtoolkit.random.values;

import net.lax1dude.eaglercraft.v1_8.EaglercraftRandom;

public class ZChance implements ZValue {
   public double chance;

   public ZChance(double chance) {
      this.chance = chance;
   }

   public Boolean getValue(EaglercraftRandom random) {
      return random.nextDouble() < this.chance;
   }
}
