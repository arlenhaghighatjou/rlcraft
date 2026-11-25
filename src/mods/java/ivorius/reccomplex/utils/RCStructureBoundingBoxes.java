package ivorius.reccomplex.utils;

import ivorius.ivtoolkit.blocks.BlockAreas;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Stream;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.BlockPos.MutableBlockPos;
import net.minecraft.world.gen.structure.StructureBoundingBox;

public class RCStructureBoundingBoxes {
   public RCStructureBoundingBoxes() {
   }

   public static Set<ChunkPos> rasterize(StructureBoundingBox boundingBox, boolean decorate) {
      if (!valid(boundingBox)) {
         return Collections.emptySet();
      } else {
         int shift = decorate ? 8 : 0;
         int minX = boundingBox.minX - shift >> 4;
         int maxX = boundingBox.maxX - shift >> 4;
         int minZ = boundingBox.minZ - shift >> 4;
         int maxZ = boundingBox.maxZ - shift >> 4;
         Set<ChunkPos> pairs = new HashSet((maxX - minX + 1) * (maxZ - minZ + 1));

         for(int x = minX; x <= maxX; ++x) {
            for(int z = minZ; z <= maxZ; ++z) {
               pairs.add(new ChunkPos(x, z));
            }
         }

         return pairs;
      }
   }

   public static boolean valid(StructureBoundingBox boundingBox) {
      return boundingBox != null && boundingBox.minX <= boundingBox.maxX && boundingBox.minY <= boundingBox.maxY && boundingBox.minZ <= boundingBox.maxZ;
   }

   public static StructureBoundingBox expand(StructureBoundingBox box, int x, int y, int z) {
      box.minX -= x;
      box.minY -= y;
      box.minZ -= z;
      box.maxX += x;
      box.maxY += y;
      box.maxZ += z;
      return box;
   }

   public static StructureBoundingBox offset(StructureBoundingBox box, int x, int y, int z) {
      box.offset(x, y, z);
      return box;
   }

   @Nonnull
   public static BlockPos getCenter(StructureBoundingBox boundingBox) {
      return new BlockPos((boundingBox.minX + boundingBox.maxX) / 2, (boundingBox.minY + boundingBox.maxY) / 2, (boundingBox.minZ + boundingBox.maxZ) / 2);
   }

   @Nullable
   public static StructureBoundingBox intersection(StructureBoundingBox left, StructureBoundingBox right) {
      return !left.intersectsWith(right) ? null : new StructureBoundingBox(Math.max(left.minX, right.minX), Math.max(left.minY, right.minY), Math.max(left.minZ, right.minZ), Math.min(left.maxX, right.maxX), Math.min(left.maxY, right.maxY), Math.min(left.maxZ, right.maxZ));
   }

   @Nonnull
   public static Iterable<BlockPos> positions(@Nonnull StructureBoundingBox area) {
      return BlockAreas.positions(RCBlockAreas.from(area));
   }

   @Nonnull
   public static Iterable<MutableBlockPos> mutablePositions(@Nonnull StructureBoundingBox area) {
      return BlockAreas.mutablePositions(RCBlockAreas.from(area));
   }

   @Nonnull
   public static Stream<BlockPos> streamPositions(@Nonnull StructureBoundingBox area) {
      return BlockAreas.streamPositions(RCBlockAreas.from(area));
   }

   @Nonnull
   public static Stream<MutableBlockPos> streamMutablePositions(@Nonnull StructureBoundingBox area) {
      return BlockAreas.streamMutablePositions(RCBlockAreas.from(area));
   }
}
