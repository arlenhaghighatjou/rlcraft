package net.ilexiconn.llibrary.server.structure.rule;

import java.util.Random;
import net.minecraft.util.math.BlockPos.MutableBlockPos;
import net.minecraft.world.World;

public class PlaceRule extends RepeatRule {
   private boolean placed;

   public PlaceRule() {
   }

   public boolean continueRepeating(World world, Random rand, MutableBlockPos position) {
      return !this.placed;
   }

   public void repeat(World world, Random rand, MutableBlockPos position) {
      this.placed = true;
   }

   public void reset(World world, Random random, MutableBlockPos pos) {
      this.placed = false;
   }
}
