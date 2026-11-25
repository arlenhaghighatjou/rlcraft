package net.ilexiconn.llibrary.server.structure.rule;

import java.util.Random;
import net.minecraft.util.math.BlockPos.MutableBlockPos;
import net.minecraft.world.World;

public class RandomRule extends FixedRule {
   private int max;
   private int min;

   public RandomRule(int min, int max) {
      this(min, max, false);
   }

   public RandomRule(int min, int max, boolean maxIncluded) {
      super(0);
      this.min = min;
      this.max = max;
      if (maxIncluded) {
         ++this.max;
      }

   }

   public void reset(World world, Random random, MutableBlockPos pos) {
      this.times = random.nextInt(this.max - this.min) + this.min;
   }

   public void init(World world, Random random, MutableBlockPos pos) {
      this.countdown = this.times;
   }
}
