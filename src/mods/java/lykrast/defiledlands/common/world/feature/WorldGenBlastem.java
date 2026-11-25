package lykrast.defiledlands.common.world.feature;

import net.lax1dude.eaglercraft.v1_8.EaglercraftRandom;
import lykrast.defiledlands.common.block.BlockBlastem;
import lykrast.defiledlands.common.init.ModBlocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.WorldGenerator;

public class WorldGenBlastem extends WorldGenerator {
   public WorldGenBlastem() {
   }

   public boolean generate(World worldIn, EaglercraftRandom rand, BlockPos position) {
      for(int i = 0; i < 64; ++i) {
         BlockPos blockpos = position.add(rand.nextInt(8) - rand.nextInt(8), rand.nextInt(4) - rand.nextInt(4), rand.nextInt(8) - rand.nextInt(8));
         if (ModBlocks.blastem.canPlaceBlockAt(worldIn, blockpos) && worldIn.isAirBlock(blockpos)) {
            worldIn.setBlockState(blockpos, ModBlocks.blastem.getDefaultState().withProperty(BlockBlastem.AGE, Integer.valueOf(15)), 2);
         }
      }

      return true;
   }
}
