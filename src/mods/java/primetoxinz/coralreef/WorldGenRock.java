package primetoxinz.coralreef;

import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.WorldGenerator;

public class WorldGenRock extends WorldGenerator {
   private final IBlockState state;

   public WorldGenRock(IBlockState state) {
      super(false);
      this.state = state;
   }

   public boolean generate(World worldIn, Random rand, BlockPos position) {
      for(; position.getY() > 3; position = position.down()) {
         if (!worldIn.isAirBlock(position.down()) && !worldIn.isAirBlock(position)) {
            Block block = worldIn.getBlockState(position.down()).getBlock();
            if (block == CoralReef.REEF) {
               int b = rand.nextInt(2);

               for(int n = 0; n < 3; ++n) {
                  int i = b + rand.nextInt(2);
                  int j = b + rand.nextInt(2);
                  int k = b + rand.nextInt(2);
                  double f = (double)(i + j + k) / 3.0D + 0.5D;
                  if (worldIn.getBlockState(position.up(j + 1)).getMaterial() == Material.WATER) {
                     for(BlockPos bp : BlockPos.getAllInBox(position.add(-i, -j, -k), position.add(i, j, k))) {
                        if (bp.distanceSq(position) <= f * f) {
                           worldIn.setBlockState(bp, this.state);
                        }
                     }

                     int xzstep = rand.nextInt(2 + b * 2) - b - 1;
                     position = position.add(xzstep, -rand.nextInt(2), xzstep);
                  }
               }

               return true;
            }
         }
      }

      return false;
   }
}
