package ivorius.ivtoolkit.blocks;

import java.util.Iterator;
import java.util.stream.Stream;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;

public class BlockArea implements Iterable<BlockPos> {
   private BlockPos point1;
   private BlockPos point2;

   public BlockArea(BlockPos point1, BlockPos point2) {
      this.point1 = point1;
      this.point2 = point2;
   }

   public static BlockArea areaFromSize(BlockPos coord, int[] size) {
      if (size[0] > 0 && size[1] > 0 && size[2] > 0) {
         return new BlockArea(coord, new BlockPos(coord.getX() + size[0] - 1, coord.getY() + size[1] - 1, coord.getZ() + size[2] - 1));
      } else {
         throw new IllegalArgumentException();
      }
   }

   public BlockPos getPoint1() {
      return this.point1;
   }

   public void setPoint1(BlockPos point1) {
      this.point1 = point1;
   }

   public BlockPos getPoint2() {
      return this.point2;
   }

   public void setPoint2(BlockPos point2) {
      this.point2 = point2;
   }

   public BlockPos getLowerCorner() {
      return BlockPositions.getLowerCorner(this.point1, this.point2);
   }

   public BlockPos getHigherCorner() {
      return BlockPositions.getHigherCorner(this.point1, this.point2);
   }

   public int[] areaSize() {
      BlockPos lower = this.getLowerCorner();
      BlockPos higher = this.getHigherCorner();
      return new int[]{higher.getX() - lower.getX() + 1, higher.getY() - lower.getY() + 1, higher.getZ() - lower.getZ() + 1};
   }

   public boolean contains(BlockPos coord) {
      BlockPos lower = this.getLowerCorner();
      BlockPos higher = this.getHigherCorner();
      return coord.getX() >= lower.getX() && coord.getY() >= lower.getY() && coord.getZ() >= lower.getZ() && coord.getX() <= higher.getX() && coord.getY() <= higher.getY() && coord.getZ() <= higher.getZ();
   }

   public AxisAlignedBB asAxisAlignedBB() {
      return new AxisAlignedBB(this.getLowerCorner(), this.getHigherCorner().add(1, 1, 1));
   }

   public Stream<BlockPos> stream() {
      return BlockAreas.streamPositions(this);
   }

   public Iterator<BlockPos> iterator() {
      return this.stream().iterator();
   }
}
