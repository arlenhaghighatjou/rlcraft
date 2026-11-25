package jaredbgreat.dldungeons.themes;

import net.lax1dude.eaglercraft.v1_8.EaglercraftRandom;

public enum Degree implements Autoselectable {
   NONE(0),
   FEW(1),
   SOME(3),
   PLENTY(5),
   HEAPS(9),
   ALWAYS(10);

   public final int value;
   public static final int scale = 10;

   private Degree(int val) {
      this.value = val;
   }

   public boolean use(EaglercraftRandom random) {
      return random.nextInt(10) < this.value;
   }
}
