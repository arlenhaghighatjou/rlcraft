package org.orecruncher.dsurround.lib.scanner;

import net.lax1dude.eaglercraft.v1_8.EaglercraftRandom;
import javax.annotation.Nonnull;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockPos.MutableBlockPos;
import org.orecruncher.lib.random.LCGRandom;

public abstract class RandomScanner extends Scanner {
   private final LCGRandom lcg = new LCGRandom();
   private int playerX;
   private int playerY;
   private int playerZ;

   public RandomScanner(@Nonnull ScanLocus locus, @Nonnull String name, int range) {
      super(locus, name, range);
   }

   public RandomScanner(@Nonnull ScanLocus locus, @Nonnull String name, int range, int blocksPerTick) {
      super(locus, name, range, blocksPerTick);
   }

   private int randomRange(int range) {
      return this.lcg.nextInt(range) - this.lcg.nextInt(range);
   }

   public void preScan() {
      BlockPos pos = this.locus.getCenter();
      this.playerX = pos.getX();
      this.playerY = pos.getY();
      this.playerZ = pos.getZ();
   }

   @Nonnull
   protected BlockPos nextPos(@Nonnull MutableBlockPos workingPos, @Nonnull EaglercraftRandom rand) {
      return workingPos.setPos(this.playerX + this.randomRange(this.xRange), this.playerY + this.randomRange(this.yRange), this.playerZ + this.randomRange(this.zRange));
   }
}
