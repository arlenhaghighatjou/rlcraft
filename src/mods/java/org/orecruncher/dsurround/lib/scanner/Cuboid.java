package org.orecruncher.dsurround.lib.scanner;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.util.math.BlockPos;
import org.orecruncher.lib.BlockPosHelper;

public class Cuboid {
   protected final BlockPos[] vertices;
   protected final int volume;
   protected final BlockPos minPoint;
   protected final BlockPos maxPoint;

   public Cuboid(@Nonnull BlockPos[] points) {
      this(points[0], points[1]);
   }

   public Cuboid(@Nonnull BlockPos vx1, @Nonnull BlockPos vx2) {
      this.vertices = new BlockPos[8];
      this.minPoint = BlockPosHelper.createMinPoint(vx1, vx2);
      this.maxPoint = BlockPosHelper.createMaxPoint(vx1, vx2);
      BlockPos t = this.maxPoint.subtract(this.minPoint);
      this.volume = t.getX() * t.getY() * t.getZ();
      this.vertices[0] = this.minPoint;
      this.vertices[1] = this.maxPoint;
      this.vertices[2] = new BlockPos(this.minPoint.getX(), this.maxPoint.getY(), this.maxPoint.getZ());
      this.vertices[3] = new BlockPos(this.maxPoint.getX(), this.minPoint.getY(), this.minPoint.getZ());
      this.vertices[4] = new BlockPos(this.maxPoint.getX(), this.minPoint.getY(), this.maxPoint.getZ());
      this.vertices[5] = new BlockPos(this.minPoint.getX(), this.minPoint.getY(), this.maxPoint.getZ());
      this.vertices[6] = new BlockPos(this.minPoint.getX(), this.maxPoint.getY(), this.minPoint.getZ());
      this.vertices[7] = new BlockPos(this.maxPoint.getX(), this.maxPoint.getY(), this.minPoint.getZ());
   }

   public boolean contains(@Nonnull BlockPos p) {
      return BlockPosHelper.contains(p, this.minPoint, this.maxPoint);
   }

   @Nonnull
   public BlockPos maximum() {
      return this.maxPoint;
   }

   @Nonnull
   public BlockPos minimum() {
      return this.minPoint;
   }

   public long volume() {
      return (long)this.volume;
   }

   @Nullable
   public Cuboid intersection(@Nonnull Cuboid o) {
      BlockPos vx1 = null;

      for(BlockPos vx : this.vertices) {
         if (o.contains(vx)) {
            vx1 = vx;
            break;
         }
      }

      if (vx1 == null) {
         return null;
      } else {
         BlockPos vx2 = null;

         for(BlockPos vx : o.vertices) {
            if (this.contains(vx) && BlockPosHelper.canFormCuboid(vx, vx1)) {
               vx2 = vx;
               break;
            }
         }

         return vx2 == null ? null : new Cuboid(vx1, vx2);
      }
   }
}
