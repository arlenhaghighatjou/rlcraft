package rtg.api.world.gen.feature;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.WorldGenerator;

public class WorldGenSeaweed extends WorldGenerator {
   protected IBlockState seaweedBlock;
   protected int height;
   protected ArrayList<Block> validGroundBlocks;

   public WorldGenSeaweed(IBlockState seaweedBlock, int height) {
      this.setSeaweedBlock(seaweedBlock);
      this.setHeight(height);
      this.setValidGroundBlocks(new ArrayList(Arrays.asList(Blocks.DIRT, Blocks.GRAVEL, Blocks.CLAY, Blocks.SAND)));
   }

   public boolean generate(World world, Random rand, BlockPos pos) {
      int x = pos.getX();
      int y = pos.getY();
      int z = pos.getZ();
      if (this.canGenerateAt(world, pos)) {
         if (this.seaweedBlock.getBlock().canPlaceBlockAt(world, pos)) {
            world.setBlockState(pos, this.seaweedBlock, 2);
         }

         for(int i = 0; i < this.height; ++i) {
            int direction = rand.nextInt(4);
            switch(direction) {
            case 0:
               --x;
               break;
            case 1:
               ++x;
               break;
            case 2:
               --z;
               break;
            case 3:
               ++z;
            }

            BlockPos p = new BlockPos(x, y + i, z);
            IBlockState b = world.getBlockState(p);
            if (b == Blocks.WATER.getDefaultState() && this.seaweedBlock.getBlock().canPlaceBlockAt(world, p)) {
               world.setBlockState(p, this.seaweedBlock, 2);
            }
         }
      }

      return true;
   }

   protected boolean canGenerateAt(World world, BlockPos pos) {
      Block groundBlock = world.getBlockState(pos.down()).getBlock();
      Block belowBlock = world.getBlockState(pos.down(2)).getBlock();
      return this.getValidGroundBlocks().contains(groundBlock) && this.getValidGroundBlocks().contains(belowBlock);
   }

   public IBlockState getSeaweedBlock() {
      return this.seaweedBlock;
   }

   public WorldGenSeaweed setSeaweedBlock(IBlockState seaweedBlock) {
      this.seaweedBlock = seaweedBlock;
      return this;
   }

   public int getHeight() {
      return this.height;
   }

   public WorldGenSeaweed setHeight(int height) {
      this.height = height;
      return this;
   }

   public ArrayList<Block> getValidGroundBlocks() {
      return this.validGroundBlocks;
   }

   public WorldGenSeaweed setValidGroundBlocks(ArrayList<Block> validGroundBlocks) {
      this.validGroundBlocks = validGroundBlocks;
      return this;
   }
}
