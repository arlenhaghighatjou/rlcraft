package paulevs.betternether.structures.plants;

import net.lax1dude.eaglercraft.v1_8.EaglercraftRandom;
import net.minecraft.block.BlockMagma;
import net.minecraft.block.BlockNetherrack;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import paulevs.betternether.blocks.BlocksRegister;
import paulevs.betternether.noise.WorleyNoise;
import paulevs.betternether.structures.IStructure;

public class StructureMagmaFlower implements IStructure {
   private static WorleyNoise offsetNoise;

   public StructureMagmaFlower() {
   }

   public void generate(World world, BlockPos pos, EaglercraftRandom random) {
      if (world.getBlockState(pos).getBlock() instanceof BlockNetherrack) {
         IBlockState state = BlocksRegister.BLOCK_MAGMA_FLOWER.getDefaultState();
         offsetNoise = new WorleyNoise((long)pos.getX() << 32 | (long)pos.getZ());

         for(int y = -3; y < 4; ++y) {
            for(int x = -3; x < 4; ++x) {
               int oz = x + (int)(offsetNoise.GetValue((double)x, (double)y) * 3.0D);

               for(int z = -3; z < 4; ++z) {
                  int ox = y + (int)(offsetNoise.GetValue((double)y, (double)z) * 3.0D);
                  int oy = z + (int)(offsetNoise.GetValue((double)x, (double)z) * 3.0D);
                  if (ox * ox + oy * oy + oz * oz <= 9) {
                     BlockPos npos = pos.add(ox, oy, oz);
                     if (world.getBlockState(npos).isFullBlock()) {
                        world.setBlockState(npos, Blocks.MAGMA.getDefaultState());
                     } else if (world.getBlockState(npos).getBlock() == Blocks.AIR && world.getBlockState(npos.down()).getBlock() instanceof BlockMagma) {
                        world.setBlockState(npos, state);
                     }
                  }
               }
            }
         }
      }

   }
}
