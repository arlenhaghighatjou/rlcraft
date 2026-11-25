package vazkii.quark.world.world;

import java.util.Random;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.gen.IChunkGenerator;
import net.minecraftforge.fml.common.IWorldGenerator;
import vazkii.quark.world.feature.NetherSmoker;

public class SmokerGenerator implements IWorldGenerator {
   public SmokerGenerator() {
   }

   public void generate(Random random, int chunkX, int chunkZ, World world, IChunkGenerator chunkGenerator, IChunkProvider chunkProvider) {
      if (world.provider.isNether() && random.nextFloat() < 0.4F) {
         label40:
         for(int i = 0; i < 20; ++i) {
            int x = chunkX * 16 + random.nextInt(16) + 8;
            int z = chunkZ * 16 + random.nextInt(16) + 8;

            for(BlockPos pos = new BlockPos(x, 50, z); pos.getY() > 10; pos = pos.down()) {
               IBlockState state = world.getBlockState(pos);
               if (state.getBlock() == Blocks.LAVA) {
                  if (world.getBlockState(pos.down()).getBlock() != Blocks.NETHERRACK) {
                     break;
                  }

                  EnumFacing[] var12 = EnumFacing.HORIZONTALS;
                  int var13 = var12.length;
                  int var14 = 0;

                  while(true) {
                     if (var14 >= var13) {
                        continue label40;
                     }

                     EnumFacing face = var12[var14];
                     if (world.getBlockState(pos.offset(face)).getBlock() == Blocks.NETHERRACK) {
                        world.setBlockState(pos.down(), NetherSmoker.smoker.getDefaultState());
                        continue label40;
                     }

                     ++var14;
                  }
               }
            }
         }
      }

   }
}
