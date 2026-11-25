package net.ilexiconn.llibrary.server.structure.rule;

import net.lax1dude.eaglercraft.v1_8.EaglercraftRandom;
import net.minecraft.util.math.BlockPos.MutableBlockPos;
import net.minecraft.world.World;

public class PlaceRule extends RepeatRule {
   private boolean placed;

   public PlaceRule() {
   }

   public boolean continueRepeating(World world, EaglercraftRandom rand, MutableBlockPos position) {
      return !this.placed;
   }

   public void repeat(World world, EaglercraftRandom rand, MutableBlockPos position) {
      this.placed = true;
   }

   public void reset(World world, EaglercraftRandom random, MutableBlockPos pos) {
      this.placed = false;
   }
}
