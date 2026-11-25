package org.orecruncher.dsurround.lib.scanner;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.util.math.BlockPos;

public class ComplementsPointIterator implements IPointIterator {
   protected final CuboidPointIterator[] segments = new CuboidPointIterator[3];
   protected int activeSegment = 0;
   protected BlockPos peeked;

   public ComplementsPointIterator(@Nonnull Cuboid volume, @Nonnull Cuboid intersect) {
      BlockPos vmax = volume.maximum();
      BlockPos imax = intersect.maximum();
      BlockPos vmin = volume.minimum();
      BlockPos imin = intersect.minimum();
      if (vmax.getX() == imax.getX() && vmin.getX() == imin.getX()) {
         this.segments[0] = CuboidPointIterator.NULL_ITERATOR;
      } else if (vmax.getX() > imax.getX()) {
         this.segments[0] = new CuboidPointIterator(new BlockPos(imax.getX(), vmin.getY(), vmin.getZ()), new BlockPos(vmax.getX(), vmax.getY(), vmax.getZ()));
      } else {
         this.segments[0] = new CuboidPointIterator(new BlockPos(vmin.getX(), vmin.getY(), vmin.getZ()), new BlockPos(imin.getX(), vmax.getY(), vmax.getZ()));
      }

      if (vmax.getY() == imax.getY() && vmin.getY() == imin.getY()) {
         this.segments[1] = CuboidPointIterator.NULL_ITERATOR;
      } else if (vmax.getY() > imax.getY()) {
         this.segments[1] = new CuboidPointIterator(new BlockPos(imin.getX(), imax.getY(), vmin.getZ()), new BlockPos(imax.getX(), vmax.getY(), vmax.getZ()));
      } else {
         this.segments[1] = new CuboidPointIterator(new BlockPos(imin.getX(), vmin.getY(), vmin.getZ()), new BlockPos(imax.getX(), imin.getY(), vmax.getZ()));
      }

      if (vmax.getZ() == imax.getZ() && vmin.getZ() == imin.getZ()) {
         this.segments[2] = CuboidPointIterator.NULL_ITERATOR;
      } else if (vmax.getZ() > imax.getZ()) {
         this.segments[2] = new CuboidPointIterator(new BlockPos(imin.getX(), imin.getY(), imax.getZ()), new BlockPos(imax.getX(), imax.getY(), vmax.getZ()));
      } else {
         this.segments[2] = new CuboidPointIterator(new BlockPos(imin.getX(), imin.getY(), vmin.getZ()), new BlockPos(imax.getX(), imax.getY(), imin.getZ()));
      }

      this.peeked = this.next0();
   }

   protected BlockPos next0() {
      while(this.activeSegment < this.segments.length) {
         BlockPos rv = this.segments[this.activeSegment].next();
         if (rv != null) {
            return rv;
         }

         ++this.activeSegment;
      }

      return null;
   }

   @Nullable
   public BlockPos peek() {
      return this.peeked;
   }

   @Nullable
   public BlockPos next() {
      BlockPos result = this.peeked;
      this.peeked = this.next0();
      return result;
   }
}
