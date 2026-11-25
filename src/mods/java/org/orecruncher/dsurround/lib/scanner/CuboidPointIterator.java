package org.orecruncher.dsurround.lib.scanner;

import java.util.Iterator;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockPos.MutableBlockPos;
import org.orecruncher.lib.BlockPosHelper;

public class CuboidPointIterator implements IPointIterator {
   static final CuboidPointIterator NULL_ITERATOR = new CuboidPointIterator() {
      public BlockPos next() {
         return null;
      }

      public BlockPos peek() {
         return null;
      }
   };
   protected final Iterator<MutableBlockPos> itr;
   protected BlockPos peeked;

   private CuboidPointIterator() {
      this.itr = null;
   }

   public CuboidPointIterator(@Nonnull BlockPos[] points) {
      this(points[0], points[1]);
   }

   public CuboidPointIterator(@Nonnull BlockPos p1, @Nonnull BlockPos p2) {
      this.itr = BlockPosHelper.getAllInBoxMutable(p1, p2).iterator();
      if (this.itr.hasNext()) {
         this.peeked = (BlockPos)this.itr.next();
      }

   }

   @Nullable
   public BlockPos next() {
      BlockPos result = this.peeked;
      this.peeked = this.itr.hasNext() ? (BlockPos)this.itr.next() : null;
      return result;
   }

   @Nullable
   public BlockPos peek() {
      return this.peeked;
   }
}
