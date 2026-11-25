package crafttweaker.util;

import net.lax1dude.eaglercraft.v1_8.EaglercraftRandom;
import stanhebben.zenscript.value.IntRange;

public class IntegerRange {
   private final int min;
   private final int max;
   private final EaglercraftRandom rand;

   public IntegerRange(int min, int max) {
      this.min = Math.min(min, max);
      this.max = Math.max(min, max);
      this.rand = new EaglercraftRandom(2906L);
   }

   public IntegerRange(IntRange range) {
      this.min = Math.min(range.getFrom(), range.getTo());
      this.max = Math.max(range.getFrom(), range.getTo());
      this.rand = new EaglercraftRandom(2906L);
   }

   public int getRandom() {
      return this.getRandom(this.rand);
   }

   public int getRandom(EaglercraftRandom rand) {
      return rand.nextInt(this.max - this.min + 1) + this.min;
   }

   public int getMin() {
      return this.min;
   }

   public int getMax() {
      return this.max;
   }
}
