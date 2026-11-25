package ivorius.ivtoolkit.world.chunk;

import ivorius.ivtoolkit.blocks.BlockSurfacePos;
import ivorius.ivtoolkit.util.IvStreams;
import java.util.function.IntFunction;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import net.minecraft.util.math.ChunkPos;

public class Chunks {
   public Chunks() {
   }

   public static boolean contains(ChunkPos chunkPos, BlockSurfacePos pos) {
      return chunkPos.x == pos.x >> 4 && chunkPos.z == pos.z >> 4;
   }

   public static Stream<BlockSurfacePos> repeatIntersections(ChunkPos chunkPos, BlockSurfacePos pos, int repeatX, int repeatZ) {
      int lowestX = pos.x + ((chunkPos.x << 4) - pos.x) / repeatX * repeatX;
      int lowestZ = pos.z + ((chunkPos.z << 4) - pos.z) / repeatZ * repeatZ;
      int repeatsX = (15 - (lowestX - (chunkPos.x << 4))) / repeatX;
      int repeatsZ = (15 - (lowestZ - (chunkPos.z << 4))) / repeatZ;
      return IvStreams.flatMapToObj(IntStream.range(0, repeatsX + 1), (iX) -> {
         return IntStream.range(0, repeatsZ + 1).mapToObj((iZ) -> {
            return new BlockSurfacePos(lowestX + iX * repeatX, lowestZ + iZ * repeatZ);
         });
      });
   }
}
