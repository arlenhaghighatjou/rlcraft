package net.ilexiconn.llibrary.server.structure.rule;

import net.lax1dude.eaglercraft.v1_8.EaglercraftRandom;
import net.minecraft.util.math.BlockPos.MutableBlockPos;
import net.minecraft.world.World;

public class FixedRule extends RepeatRule {
   protected int times;
   protected int countdown;

   public FixedRule(int times) {
      this.times = times;
      this.countdown = times;
   }

   public boolean continueRepeating(World world, EaglercraftRandom rand, MutableBlockPos position) {
      return this.countdown > 0;
   }

   public void repeat(World world, EaglercraftRandom rand, MutableBlockPos position) {
      --this.countdown;
      position.setPos(position.getX() + this.getSpacingX(), position.getY() + this.getSpacingY(), position.getZ() + this.getSpacingZ());
   }

   public void reset(World world, EaglercraftRandom random, MutableBlockPos pos) {
      this.countdown = this.times;
   }
}
