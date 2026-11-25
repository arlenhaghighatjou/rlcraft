package rustic.common.world;

import net.lax1dude.eaglercraft.v1_8.EaglercraftRandom;
import net.minecraft.block.BlockLeaves;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.WorldGenerator;
import rustic.common.Config;
import rustic.common.blocks.BlockBeehive;
import rustic.common.blocks.ModBlocks;

public class WorldGenBeehive extends WorldGenerator {
   protected static IBlockState[] orientations = new IBlockState[]{ModBlocks.BEEHIVE.getDefaultState().withProperty(BlockBeehive.FACING, EnumFacing.NORTH), ModBlocks.BEEHIVE.getDefaultState().withProperty(BlockBeehive.FACING, EnumFacing.EAST), ModBlocks.BEEHIVE.getDefaultState().withProperty(BlockBeehive.FACING, EnumFacing.SOUTH), ModBlocks.BEEHIVE.getDefaultState().withProperty(BlockBeehive.FACING, EnumFacing.WEST)};

   public WorldGenBeehive() {
   }

   public boolean generate(World worldIn, EaglercraftRandom rand, BlockPos centerPos) {
      for(int i = 0; i < Config.MAX_BEEHIVE_ATTEMPTS; ++i) {
         int j = centerPos.getX() + rand.nextInt(8) - rand.nextInt(8);
         int l = centerPos.getZ() + rand.nextInt(8) - rand.nextInt(8);
         int k = worldIn.getHeight(j, l);
         if (worldIn.getBlockState(new BlockPos(j, k - 1, l)).getBlock() instanceof BlockLeaves) {
            BlockPos tempPos;
            for(tempPos = new BlockPos(j, k - 1, l); worldIn.getBlockState(tempPos).getBlock() instanceof BlockLeaves; tempPos = tempPos.down()) {
               ;
            }

            if (ModBlocks.BEEHIVE.canPlaceBlockAt(worldIn, tempPos)) {
               worldIn.setBlockState(tempPos, orientations[rand.nextInt(4)]);
               return true;
            }
         }
      }

      return false;
   }
}
