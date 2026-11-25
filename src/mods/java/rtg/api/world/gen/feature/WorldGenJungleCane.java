package rtg.api.world.gen.feature;

import java.util.Random;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.WorldGenerator;

public class WorldGenJungleCane extends WorldGenerator {
   private int height;

   public WorldGenJungleCane(int h) {
      this.height = h;
   }

   public boolean generate(World world, Random rand, BlockPos pos) {
      int x = pos.getX();
      int y = pos.getY();

      int z;
      for(z = pos.getZ(); y > 0; --y) {
         IBlockState b = world.getBlockState(new BlockPos(x, y, z));
         if (!world.isAirBlock(new BlockPos(x, y, z)) || b.getBlock().isLeaves(b, world, new BlockPos(x, y, z))) {
            break;
         }
      }

      IBlockState b = world.getBlockState(new BlockPos(x, y, z));
      if (b != Blocks.GRASS.getDefaultState() && b != Blocks.DIRT.getDefaultState()) {
         return false;
      } else {
         for(int j = 0; j < 4; ++j) {
            b = world.getBlockState(new BlockPos(j == 0 ? x - 1 : (j == 1 ? x + 1 : x), y, j == 2 ? z - 1 : (j == 3 ? z + 1 : z)));
            if (b.getMaterial() != Material.GROUND && b.getMaterial() != Material.GRASS) {
               return false;
            }
         }

         for(int var17 = 0; var17 < 4; ++var17) {
            int sx = var17 == 0 ? x - 1 : (var17 == 1 ? x + 1 : x);
            int sz = var17 == 2 ? z - 1 : (var17 == 3 ? z + 1 : z);
            int ra = rand.nextInt(this.height * 2 + 1) + this.height;
            b = world.getBlockState(new BlockPos(sx, y + 1, sz));
            if (b.getMaterial() == Material.AIR || b.getMaterial() == Material.VINE) {
               for(int k = 0; k < ra; ++k) {
                  b = world.getBlockState(new BlockPos(sx, y + 1 + k, sz));
                  if (b.getMaterial() != Material.AIR && b.getMaterial() != Material.VINE) {
                     break;
                  }

                  world.setBlockState(new BlockPos(sx, y + 1 + k, sz), Blocks.REEDS.getDefaultState(), 2);
               }
            }
         }

         world.setBlockState(new BlockPos(x, y, z), Blocks.WATER.getDefaultState());
         return true;
      }
   }
}
