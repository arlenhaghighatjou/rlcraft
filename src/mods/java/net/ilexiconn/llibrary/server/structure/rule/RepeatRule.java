package net.ilexiconn.llibrary.server.structure.rule;

import net.lax1dude.eaglercraft.v1_8.EaglercraftRandom;
import net.minecraft.util.math.BlockPos.MutableBlockPos;
import net.minecraft.world.World;

public abstract class RepeatRule {
   private int spacingX;
   private int spacingY;
   private int spacingZ;

   public RepeatRule() {
   }

   public void setSpacing(int spacingX, int spacingY, int spacingZ) {
      this.spacingX = spacingX;
      this.spacingY = spacingY;
      this.spacingZ = spacingZ;
   }

   public int getSpacingX() {
      return this.spacingX;
   }

   public int getSpacingY() {
      return this.spacingY;
   }

   public int getSpacingZ() {
      return this.spacingZ;
   }

   public abstract boolean continueRepeating(World var1, EaglercraftRandom var2, MutableBlockPos var3);

   public abstract void repeat(World var1, EaglercraftRandom var2, MutableBlockPos var3);

   public abstract void reset(World var1, EaglercraftRandom var2, MutableBlockPos var3);

   public void init(World world, EaglercraftRandom random, MutableBlockPos pos) {
      this.reset(world, random, pos);
   }
}
