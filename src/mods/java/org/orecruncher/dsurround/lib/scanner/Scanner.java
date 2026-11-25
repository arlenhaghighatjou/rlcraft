package org.orecruncher.dsurround.lib.scanner;

import net.lax1dude.eaglercraft.v1_8.EaglercraftRandom;
import java.util.concurrent.Callable;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockPos.MutableBlockPos;
import org.orecruncher.lib.chunk.IBlockAccessEx;
import org.orecruncher.lib.logging.ModLog;
import org.orecruncher.lib.random.XorShiftRandom;

public abstract class Scanner implements ITickable, Callable<Void> {
   private static final int MAX_BLOCKS_TICK = 3000;
   protected final String name;
   protected final int xRange;
   protected final int yRange;
   protected final int zRange;
   protected final int xSize;
   protected final int ySize;
   protected final int zSize;
   protected final int blocksPerTick;
   protected final int volume;
   protected final ScanLocus locus;
   protected final EaglercraftRandom random;
   protected final MutableBlockPos workingPos;
   protected ModLog log;

   public Scanner(@Nonnull ScanLocus locus, @Nonnull String name, int range) {
      this(locus, name, range, 0);
   }

   public Scanner(@Nonnull ScanLocus locus, @Nonnull String name, int range, int blocksPerTick) {
      this(locus, name, range, range, range, blocksPerTick);
   }

   public Scanner(@Nonnull ScanLocus locus, @Nonnull String name, int xRange, int yRange, int zRange) {
      this(locus, name, xRange, yRange, zRange, 0);
   }

   public Scanner(@Nonnull ScanLocus locus, @Nonnull String name, int xRange, int yRange, int zRange, int blocksPerTick) {
      this.random = new XorShiftRandom();
      this.workingPos = new MutableBlockPos();
      this.log = ModLog.NULL_LOGGER;
      this.name = name;
      this.xRange = xRange;
      this.yRange = yRange;
      this.zRange = zRange;
      this.xSize = xRange * 2;
      this.ySize = yRange * 2;
      this.zSize = zRange * 2;
      this.volume = this.xSize * this.ySize * this.zSize;
      if (blocksPerTick == 0) {
         this.blocksPerTick = Math.min(this.volume / 20, 3000);
      } else {
         this.blocksPerTick = Math.min(blocksPerTick, 3000);
      }

      this.locus = locus;
   }

   public void setLogger(@Nonnull ModLog log) {
      this.log = log;
   }

   public int getVolume() {
      return this.volume;
   }

   public abstract void blockScan(@Nonnull IBlockState var1, @Nonnull BlockPos var2, @Nonnull EaglercraftRandom var3);

   protected boolean interestingBlock(IBlockState state) {
      return state != Blocks.AIR.getDefaultState();
   }

   public Void call() {
      this.update();
      return null;
   }

   public void preScan() {
   }

   public void postScan() {
   }

   public void update() {
      this.preScan();
      IBlockAccessEx provider = this.locus.getWorld();

      for(int count = 0; count < this.blocksPerTick; ++count) {
         BlockPos pos = this.nextPos(this.workingPos, this.random);
         if (pos == null) {
            break;
         }

         IBlockState state = provider.getBlockState(pos);
         if (this.interestingBlock(state)) {
            this.blockScan(state, pos, this.random);
         }
      }

      this.postScan();
   }

   @Nullable
   protected abstract BlockPos nextPos(@Nonnull MutableBlockPos var1, @Nonnull EaglercraftRandom var2);
}
