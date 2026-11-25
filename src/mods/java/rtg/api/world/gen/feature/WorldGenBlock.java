package rtg.api.world.gen.feature;

import java.util.Random;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.WorldGenerator;

public class WorldGenBlock extends WorldGenerator {
   protected IBlockState placeBlock;
   protected IBlockState replaceBlock;
   protected IBlockState adjacentBlock;
   protected int minAdjacents;

   public WorldGenBlock(IBlockState placeBlock, IBlockState replaceBlock, IBlockState adjacentBlock, int minAdjacents) {
      super(false);
      this.placeBlock = placeBlock;
      this.replaceBlock = replaceBlock;
      this.setAdjacentBlock(adjacentBlock);
      this.setMinAdjacents(minAdjacents);
   }

   public boolean generate(World world, Random rand, BlockPos pos) {
      int x = pos.getX();
      int y = pos.getY();
      int z = pos.getZ();
      IBlockState targetBlock = world.getBlockState(new BlockPos(x, y, z));
      if (targetBlock != this.replaceBlock) {
         return false;
      } else if (!this.isAdjacent(world, x, y, z)) {
         return false;
      } else {
         world.setBlockState(new BlockPos(x, y, z), this.placeBlock, 2);
         return true;
      }
   }

   protected boolean isAdjacent(World world, int x, int y, int z) {
      int adjacentCount = 0;
      if (world.getBlockState(new BlockPos(x + 1, y, z)) == this.adjacentBlock) {
         ++adjacentCount;
      }

      if (world.getBlockState(new BlockPos(x - 1, y, z)) == this.adjacentBlock) {
         ++adjacentCount;
      }

      if (world.getBlockState(new BlockPos(x, y + 1, z)) == this.adjacentBlock) {
         ++adjacentCount;
      }

      if (world.getBlockState(new BlockPos(x, y - 1, z)) == this.adjacentBlock) {
         ++adjacentCount;
      }

      if (world.getBlockState(new BlockPos(x, y, z + 1)) == this.adjacentBlock) {
         ++adjacentCount;
      }

      if (world.getBlockState(new BlockPos(x, y, z - 1)) == this.adjacentBlock) {
         ++adjacentCount;
      }

      return adjacentCount > 0 && adjacentCount >= this.minAdjacents;
   }

   public IBlockState getPlaceBlock() {
      return this.placeBlock;
   }

   public WorldGenBlock setPlaceBlock(IBlockState placeBlock) {
      this.placeBlock = placeBlock;
      return this;
   }

   public IBlockState getReplaceBlock() {
      return this.replaceBlock;
   }

   public WorldGenBlock setReplaceBlock(IBlockState replaceBlock) {
      this.replaceBlock = replaceBlock;
      return this;
   }

   public IBlockState getAdjacentBlock() {
      return this.adjacentBlock;
   }

   public WorldGenBlock setAdjacentBlock(IBlockState adjacentBlock) {
      this.adjacentBlock = adjacentBlock;
      return this;
   }

   public int getMinAdjacents() {
      return this.minAdjacents;
   }

   public WorldGenBlock setMinAdjacents(int minAdjacents) {
      this.minAdjacents = minAdjacents;
      return this;
   }
}
