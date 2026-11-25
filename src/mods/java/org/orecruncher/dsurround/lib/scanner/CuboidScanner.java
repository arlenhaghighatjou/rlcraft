package org.orecruncher.dsurround.lib.scanner;

import net.lax1dude.eaglercraft.v1_8.EaglercraftRandom;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockPos.MutableBlockPos;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.orecruncher.dsurround.event.BlockUpdateEvent;
import org.orecruncher.lib.chunk.IBlockAccessEx;

public abstract class CuboidScanner extends Scanner {
   protected boolean scanFinished = false;
   protected Cuboid activeCuboid;
   protected CuboidPointIterator fullRange;
   protected BlockPos lastPos;
   protected int lastReference = 0;

   protected CuboidScanner(@Nonnull ScanLocus locus, @Nonnull String name, int range, int blocksPerTick) {
      super(locus, name, range, blocksPerTick);
   }

   public boolean isScanFinished() {
      return this.scanFinished;
   }

   protected BlockPos[] getMinMaxPointsForVolume(@Nonnull BlockPos pos) {
      BlockPos min = pos.add(-this.xRange, -this.yRange, -this.zRange);
      BlockPos max = pos.add(this.xRange, this.yRange, this.zRange);
      if (min.getY() < 0) {
         min = new BlockPos(min.getX(), 0, min.getZ());
      }

      return new BlockPos[]{min, max};
   }

   protected Cuboid getVolumeFor(BlockPos pos) {
      BlockPos[] points = this.getMinMaxPointsForVolume(pos);
      return new Cuboid(points);
   }

   protected void resetFullScan() {
      this.lastPos = this.locus.getCenter();
      this.lastReference = this.locus.getReference();
      this.scanFinished = false;
      BlockPos[] points = this.getMinMaxPointsForVolume(this.lastPos);
      this.activeCuboid = new Cuboid(points);
      this.fullRange = new CuboidPointIterator(points);
   }

   public void update() {
      BlockPos playerPos = this.locus.getCenter();
      if (playerPos != null && playerPos.getY() >= 0) {
         if (this.fullRange != null && this.locus.getReference() == this.lastReference) {
            if (this.lastPos.equals(playerPos)) {
               if (!this.scanFinished) {
                  super.update();
               }
            } else {
               Cuboid oldVolume = this.activeCuboid != null ? this.activeCuboid : this.getVolumeFor(this.lastPos);
               Cuboid newVolume = this.getVolumeFor(playerPos);
               Cuboid intersect = oldVolume.intersection(newVolume);
               if (intersect != null && oldVolume.volume() >= (oldVolume.volume() - intersect.volume()) * 2L) {
                  if (this.scanFinished) {
                     this.lastPos = playerPos;
                     this.activeCuboid = newVolume;
                     this.updateScan(newVolume, intersect);
                  } else {
                     super.update();
                  }
               } else {
                  this.resetFullScan();
                  super.update();
               }
            }
         } else {
            this.resetFullScan();
            super.update();
         }
      } else {
         this.fullRange = null;
      }

   }

   protected void updateScan(@Nonnull Cuboid newVolume, @Nonnull Cuboid intersect) {
      IBlockAccessEx provider = this.locus.getWorld();
      ComplementsPointIterator newInRange = new ComplementsPointIterator(newVolume, intersect);

      for(BlockPos point = newInRange.next(); point != null; point = newInRange.next()) {
         if (point.getY() > 0) {
            IBlockState state = provider.getBlockState(point);
            if (this.interestingBlock(state)) {
               this.blockScan(state, point, this.random);
            }
         }
      }

      this.scanFinished = true;
   }

   @Nullable
   protected BlockPos nextPos(@Nonnull MutableBlockPos workingPos, @Nonnull EaglercraftRandom rand) {
      if (this.scanFinished) {
         return null;
      } else {
         IBlockAccessEx provider = this.locus.getWorld();
         int checked = 0;

         BlockPos point;
         while((point = this.fullRange.peek()) != null) {
            if (!provider.isAvailable(point)) {
               return null;
            }

            this.fullRange.next();
            if (point.getY() > 0) {
               return point;
            }

            ++checked;
            if (checked >= this.blocksPerTick) {
               return null;
            }
         }

         this.scanFinished = true;
         return null;
      }
   }

   protected boolean isInteresting(@Nonnull BlockUpdateEvent event) {
      if (this.activeCuboid != null && event.oldState != event.newState) {
         if (!this.activeCuboid.contains(event.pos)) {
            return false;
         } else {
            return !this.interestingBlock(event.newState) ? false : this.locus.getWorld().isAvailable(event.pos);
         }
      } else {
         return false;
      }
   }

   @SubscribeEvent
   public void onBlockUpdate(@Nonnull BlockUpdateEvent event) {
      try {
         if (this.isInteresting(event)) {
            this.blockScan(event.newState, event.pos, this.random);
         }
      } catch (Throwable var3) {
         this.log.error("onBlockUpdate() error", var3);
      }

   }
}
