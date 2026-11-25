package net.ilexiconn.llibrary.server.structure.rule;

import java.util.Random;
import net.minecraft.util.math.BlockPos.MutableBlockPos;
import net.minecraft.world.World;

public class FixedRule extends RepeatRule {
   protected int times;
   protected int countdown;

   public FixedRule(int times) {
      this.times = times;
      this.countdown = times;
   }

   public boolean continueRepeating(World world, Random rand, MutableBlockPos position) {
      return this.countdown > 0;
   }

   public void repeat(World world, Random rand, MutableBlockPos position) {
      --this.countdown;
      position.setPos(position.getX() + this.getSpacingX(), position.getY() + this.getSpacingY(), position.getZ() + this.getSpacingZ());
   }

   public void reset(World world, Random random, MutableBlockPos pos) {
      this.countdown = this.times;
   }
}
