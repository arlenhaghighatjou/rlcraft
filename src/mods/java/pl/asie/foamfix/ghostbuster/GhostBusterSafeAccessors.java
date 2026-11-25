package pl.asie.foamfix.ghostbuster;

import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;

public class GhostBusterSafeAccessors {
   public GhostBusterSafeAccessors() {
   }

   public static boolean isAreaLoaded(IBlockAccess access, BlockPos pos, int radius) {
      return access instanceof World ? ((World)access).isAreaLoaded(pos, radius) : true;
   }

   public static boolean isBlockLoaded(IBlockAccess access, BlockPos pos) {
      return access instanceof World ? ((World)access).isBlockLoaded(pos) : true;
   }

   public static IBlockState getBlockState(IBlockAccess access, BlockPos pos) {
      if (access instanceof World) {
         if (!((World)access).isOutsideBuildHeight(pos)) {
            Chunk c = ((World)access).getChunkProvider().getLoadedChunk(pos.getX() >> 4, pos.getZ() >> 4);
            if (c != null) {
               return c.getBlockState(pos);
            }
         }

         return Blocks.AIR.getDefaultState();
      } else {
         return access.getBlockState(pos);
      }
   }
}
