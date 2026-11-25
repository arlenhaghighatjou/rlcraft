package com.dhanantry.scapeandrunparasites.world.gen.feature;

import com.dhanantry.scapeandrunparasites.init.SRPBlocks;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.WorldGenAbstractTree;

public abstract class WorldGenParasiteTreeAbstract extends WorldGenAbstractTree {
   public WorldGenParasiteTreeAbstract(boolean notify) {
      super(notify);
   }

   protected boolean canGrowInto(Block blockType) {
      Material material = blockType.getDefaultState().getMaterial();
      return material == Material.AIR || material == Material.LEAVES || blockType == Blocks.GRASS || blockType == Blocks.DIRT || blockType == Blocks.LOG || blockType == Blocks.LOG2 || blockType == Blocks.SAPLING || blockType == Blocks.VINE || blockType == SRPBlocks.ParasiteBush;
   }

   protected void setDirtAt(World worldIn, BlockPos pos) {
      if (worldIn.getBlockState(pos).getBlock() != Blocks.DIRT) {
         this.setBlockAndNotifyAdequately(worldIn, pos, SRPBlocks.ParasiteStain.getDefaultState());
      }

   }

   public boolean isReplaceable(World world, BlockPos pos) {
      IBlockState state = world.getBlockState(pos);
      return state.getBlock().isAir(state, world, pos) || state.getBlock().isLeaves(state, world, pos) || state.getBlock().isWood(world, pos) || this.canGrowInto(state.getBlock()) || state.getBlock() == SRPBlocks.ParasiteBush;
   }
}
