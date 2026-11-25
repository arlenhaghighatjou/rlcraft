package ivorius.ivtoolkit.blocks;

import ivorius.ivtoolkit.gui.IntegerRange;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.util.EnumFacing;

public class BlockSurfaceAreas {
   public BlockSurfaceAreas() {
   }

   public static int sideLength(BlockSurfaceArea area, EnumFacing side) {
      int[] size = area.areaSize();
      switch(side) {
      case EAST:
      case WEST:
         return size[0];
      case UP:
      case DOWN:
         return size[1];
      case NORTH:
      case SOUTH:
         return size[2];
      default:
         throw new IllegalArgumentException();
      }
   }

   public static BlockSurfaceArea side(BlockSurfaceArea area, EnumFacing side) {
      BlockSurfacePos lowerCorner = area.getLowerCorner();
      BlockSurfacePos higherCorner = area.getHigherCorner();
      switch(side) {
      case EAST:
         return new BlockSurfaceArea(new BlockSurfacePos(higherCorner.getX(), lowerCorner.getZ()), higherCorner);
      case WEST:
         return new BlockSurfaceArea(lowerCorner, new BlockSurfacePos(lowerCorner.getX(), higherCorner.getZ()));
      case UP:
      case DOWN:
      default:
         throw new IllegalArgumentException();
      case NORTH:
         return new BlockSurfaceArea(lowerCorner, new BlockSurfacePos(higherCorner.getX(), lowerCorner.getZ()));
      case SOUTH:
         return new BlockSurfaceArea(new BlockSurfacePos(lowerCorner.getX(), higherCorner.getZ()), higherCorner);
      }
   }

   @Nullable
   public static BlockSurfaceArea shrink(BlockSurfaceArea area, EnumFacing side, int amount) {
      switch(side) {
      case EAST:
         return shrink(area, BlockSurfacePos.ORIGIN, new BlockSurfacePos(amount, 0));
      case WEST:
         return shrink(area, new BlockSurfacePos(amount, 0), BlockSurfacePos.ORIGIN);
      case UP:
      case DOWN:
      default:
         throw new IllegalArgumentException();
      case NORTH:
         return shrink(area, new BlockSurfacePos(0, amount), BlockSurfacePos.ORIGIN);
      case SOUTH:
         return shrink(area, BlockSurfacePos.ORIGIN, new BlockSurfacePos(0, amount));
      }
   }

   @Nullable
   public static BlockSurfaceArea shrink(BlockSurfaceArea area, BlockSurfacePos lower, BlockSurfacePos higher) {
      BlockSurfacePos p1 = area.getPoint1();
      BlockSurfacePos p2 = area.getPoint2();
      IntegerRange x = shrink(p1.getX(), p2.getX(), lower.getX(), higher.getX());
      IntegerRange z = shrink(p1.getZ(), p2.getZ(), lower.getZ(), higher.getZ());
      return x != null && z != null ? new BlockSurfaceArea(new BlockSurfacePos(x.min, z.min), new BlockSurfacePos(x.max, z.max)) : null;
   }

   @Nullable
   private static IntegerRange shrink(int l, int r, int shrMin, int shrMax) {
      boolean c = l < r;
      return Math.abs(l - r) >= shrMin + shrMax ? new IntegerRange(c ? l + shrMin : l - shrMax, c ? r - shrMax : r + shrMin) : null;
   }

   @Nonnull
   public static BlockSurfaceArea expand(BlockSurfaceArea area, BlockSurfacePos lower, BlockSurfacePos higher) {
      BlockSurfacePos p1 = area.getPoint1();
      BlockSurfacePos p2 = area.getPoint2();
      IntegerRange x = expand(p1.getX(), p2.getX(), lower.getX(), higher.getX());
      IntegerRange z = expand(p1.getZ(), p2.getZ(), lower.getZ(), higher.getZ());
      return new BlockSurfaceArea(new BlockSurfacePos(x.min, z.min), new BlockSurfacePos(x.max, z.max));
   }

   @Nonnull
   private static IntegerRange expand(int l, int r, int expMin, int expMax) {
      boolean c = l < r;
      return new IntegerRange(c ? l - expMin : l + expMax, c ? r + expMax : r - expMin);
   }
}
