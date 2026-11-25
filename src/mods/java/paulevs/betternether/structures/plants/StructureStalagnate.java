package paulevs.betternether.structures.plants;

import java.util.Random;
import net.minecraft.block.BlockNetherrack;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import paulevs.betternether.blocks.BlocksRegister;
import paulevs.betternether.structures.IStructure;

public class StructureStalagnate implements IStructure {
   public StructureStalagnate() {
   }

   public void generate(World chunk, BlockPos pos, Random random) {
      BlockPos up = this.upRay(chunk, pos);
      if (up != BlockPos.ORIGIN) {
         IBlockState bottom = BlocksRegister.BLOCK_STALAGNATE_BOTTOM.getDefaultState();
         IBlockState middle = BlocksRegister.BLOCK_STALAGNATE_MIDDLE.getDefaultState();
         IBlockState top = BlocksRegister.BLOCK_STALAGNATE_TOP.getDefaultState();
         chunk.setBlockState(pos.up(), bottom);

         for(int y = 2; y < up.getY() - pos.getY() - 1; ++y) {
            chunk.setBlockState(pos.up(y), middle);
         }

         chunk.setBlockState(up.down(), top);
      }

   }

   private BlockPos upRay(World chunk, BlockPos start) {
      int dist = 0;

      for(int j = start.getY() + 1; j < 126; ++j) {
         if (chunk.getBlockState(new BlockPos(start.getX(), j, start.getZ())).getBlock() != Blocks.AIR) {
            dist = j;
            break;
         }
      }

      int var6 = dist - start.getY();
      BlockPos result = new BlockPos(start.getX(), dist, start.getZ());
      return var6 < 25 && var6 > 2 && chunk.getBlockState(result).getBlock() instanceof BlockNetherrack ? result : BlockPos.ORIGIN;
   }
}
