package ivorius.ivtoolkit.blocks;

import ivorius.ivtoolkit.util.IvStreams;
import java.util.Iterator;
import java.util.function.IntFunction;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import net.minecraft.world.gen.structure.StructureBoundingBox;

public class BlockSurfaceArea implements Iterable<BlockSurfacePos> {
   private BlockSurfacePos point1;
   private BlockSurfacePos point2;

   public BlockSurfaceArea(BlockSurfacePos point1, BlockSurfacePos point2) {
      this.point1 = point1;
      this.point2 = point2;
   }

   public static BlockSurfaceArea areaFromSize(BlockSurfacePos coord, int[] size) {
      if (size[0] > 0 && size[1] > 0 && size[2] > 0) {
         return new BlockSurfaceArea(coord, new BlockSurfacePos(coord.getX() + size[0] - 1, coord.getZ() + size[2] - 1));
      } else {
         throw new IllegalArgumentException();
      }
   }

   public static BlockSurfaceArea from(BlockArea area) {
      return new BlockSurfaceArea(BlockSurfacePos.from(area.getPoint1()), BlockSurfacePos.from(area.getPoint2()));
   }

   public static BlockSurfaceArea from(StructureBoundingBox boundingBox) {
      return new BlockSurfaceArea(new BlockSurfacePos(boundingBox.minX, boundingBox.minZ), new BlockSurfacePos(boundingBox.maxX, boundingBox.maxZ));
   }

   public BlockSurfacePos getPoint1() {
      return this.point1;
   }

   public void setPoint1(BlockSurfacePos point1) {
      this.point1 = point1;
   }

   public BlockSurfacePos getPoint2() {
      return this.point2;
   }

   public void setPoint2(BlockSurfacePos point2) {
      this.point2 = point2;
   }

   public BlockSurfacePos getLowerCorner() {
      return BlockSurfacePositions.getLowerCorner(this.point1, this.point2);
   }

   public BlockSurfacePos getHigherCorner() {
      return BlockSurfacePositions.getHigherCorner(this.point1, this.point2);
   }

   public int[] areaSize() {
      BlockSurfacePos lower = this.getLowerCorner();
      BlockSurfacePos higher = this.getHigherCorner();
      return new int[]{higher.getX() - lower.getX() + 1, higher.getZ() - lower.getZ() + 1};
   }

   public boolean contains(BlockSurfacePos coord) {
      BlockSurfacePos lower = this.getLowerCorner();
      BlockSurfacePos higher = this.getHigherCorner();
      return coord.getX() >= lower.getX() && coord.getZ() >= lower.getZ() && coord.getX() <= higher.getX() && coord.getZ() <= higher.getZ();
   }

   public Iterator<BlockSurfacePos> iterator() {
      return this.stream().iterator();
   }

   public Stream<BlockSurfacePos> stream() {
      BlockSurfacePos lower = this.getLowerCorner();
      BlockSurfacePos higher = this.getHigherCorner();
      return IvStreams.flatMapToObj(IntStream.range(lower.x, higher.x + 1), (x) -> {
         return IntStream.range(lower.z, higher.z + 1).mapToObj((z) -> {
            return new BlockSurfacePos(x, z);
         });
      });
   }

   public StructureBoundingBox toStructureBoundingBox(int minY, int maxY) {
      BlockSurfacePos lower = this.getLowerCorner();
      BlockSurfacePos higher = this.getHigherCorner();
      return new StructureBoundingBox(lower.getX(), minY, lower.getZ(), higher.getX(), maxY, higher.getZ());
   }
}
