package org.orecruncher.lib;

import com.google.common.collect.AbstractIterator;
import javax.annotation.Nonnull;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.BlockPos.MutableBlockPos;

public final class BlockPosHelper {
   private BlockPosHelper() {
   }

   public static BlockPos getNonOffsetPos(@Nonnull Entity entity) {
      return new BlockPos(entity.posX, entity.posY, entity.posZ);
   }

   public static MutableBlockPos setPos(@Nonnull MutableBlockPos pos, @Nonnull Vec3d vec) {
      return pos.setPos(vec.x, vec.y, vec.z);
   }

   public static boolean canFormCuboid(@Nonnull BlockPos p1, @Nonnull BlockPos p2) {
      return p1.getX() != p2.getX() && p1.getZ() != p2.getZ() && p1.getY() != p2.getY();
   }

   public static BlockPos createMinPoint(@Nonnull BlockPos p1, @Nonnull BlockPos p2) {
      return new BlockPos(Math.min(p1.getX(), p2.getX()), Math.min(p1.getY(), p2.getY()), Math.min(p1.getZ(), p2.getZ()));
   }

   public static BlockPos createMaxPoint(@Nonnull BlockPos p1, @Nonnull BlockPos p2) {
      return new BlockPos(Math.max(p1.getX(), p2.getX()), Math.max(p1.getY(), p2.getY()), Math.max(p1.getZ(), p2.getZ()));
   }

   public static boolean contains(@Nonnull BlockPos test, @Nonnull BlockPos min, @Nonnull BlockPos max) {
      return test.getX() >= min.getX() && test.getX() <= max.getX() && test.getY() >= min.getY() && test.getY() <= max.getY() && test.getZ() >= min.getZ() && test.getZ() <= max.getZ();
   }

   public static boolean notContains(@Nonnull BlockPos test, @Nonnull BlockPos min, @Nonnull BlockPos max) {
      return test.getX() < min.getX() || test.getX() > max.getX() || test.getY() < min.getY() || test.getY() > max.getY() || test.getZ() < min.getZ() || test.getZ() > max.getZ();
   }

   public static Iterable<MutableBlockPos> getAllInBoxMutable(BlockPos from, BlockPos to) {
      BlockPos blockpos = createMinPoint(from, to);
      BlockPos blockpos1 = createMaxPoint(from, to);
      return () -> {
         return new AbstractIterator<MutableBlockPos>() {
            private MutableBlockPos theBlockPos;

            protected MutableBlockPos computeNext() {
               if (this.theBlockPos == null) {
                  this.theBlockPos = new MutableBlockPos(blockpos.getX(), blockpos.getY(), blockpos.getZ());
                  return this.theBlockPos;
               } else if (this.theBlockPos.equals(blockpos1)) {
                  return (MutableBlockPos)this.endOfData();
               } else {
                  int i = this.theBlockPos.getX();
                  int j = this.theBlockPos.getY();
                  int k = this.theBlockPos.getZ();
                  if (j < blockpos1.getY()) {
                     ++j;
                  } else if (i < blockpos1.getX()) {
                     j = blockpos.getY();
                     ++i;
                  } else if (k < blockpos1.getZ()) {
                     i = blockpos.getX();
                     j = blockpos.getY();
                     ++k;
                  }

                  this.theBlockPos.setPos(i, j, k);
                  return this.theBlockPos;
               }
            }
         };
      };
   }
}
