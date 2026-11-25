package ivorius.ivtoolkit.tools;

import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;

public class IvAABBs {
   public IvAABBs() {
   }

   public static AxisAlignedBB intersection(AxisAlignedBB bb, BlockPos pos) {
      return new AxisAlignedBB(Math.max(bb.minX, (double)pos.getX()), Math.max(bb.minY, (double)pos.getY()), Math.max(bb.minZ, (double)pos.getZ()), Math.min(bb.maxX, (double)(pos.getX() + 1)), Math.min(bb.maxY, (double)(pos.getY() + 1)), Math.min(bb.maxZ, (double)(pos.getZ() + 1)));
   }

   public static AxisAlignedBB boundsIntersection(AxisAlignedBB bb, BlockPos pos) {
      return new AxisAlignedBB(Math.max(bb.minX - (double)pos.getX(), 0.0D), Math.max(bb.minY - (double)pos.getY(), 0.0D), Math.max(bb.minZ - (double)pos.getZ(), 0.0D), Math.min(bb.maxX - (double)pos.getX(), 1.0D), Math.min(bb.maxY - (double)pos.getY(), 1.0D), Math.min(bb.maxZ - (double)pos.getZ(), 1.0D));
   }
}
