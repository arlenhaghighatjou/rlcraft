package meldexun.entityculling.integration;

import io.github.opencubicchunks.cubicchunks.api.world.ICubicWorld;
import net.minecraft.entity.Entity;
import net.minecraft.util.ClassInheritanceMultiMap;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.storage.ExtendedBlockStorage;

public class CubicChunks {
   public CubicChunks() {
   }

   public static ClassInheritanceMultiMap<Entity> getEntityList(World world, BlockPos pos) {
      return !((ICubicWorld)world).isCubicWorld() ? world.getChunkFromBlockCoords(pos).getEntityLists()[pos.getY() >> 4] : ((ICubicWorld)world).getCubeCache().getCube(pos.getX() >> 4, pos.getY() >> 4, pos.getZ() >> 4).getEntitySet();
   }

   public static ExtendedBlockStorage getBlockStorage(World world, int chunkX, int chunkY, int chunkZ) {
      return !((ICubicWorld)world).isCubicWorld() ? world.getChunkProvider().provideChunk(chunkX, chunkZ).getBlockStorageArray()[chunkY] : ((ICubicWorld)world).getCubeCache().getCube(chunkX, chunkY, chunkZ).getStorage();
   }
}
