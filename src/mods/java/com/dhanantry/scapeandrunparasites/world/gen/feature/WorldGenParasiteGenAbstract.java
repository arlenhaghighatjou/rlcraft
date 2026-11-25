package com.dhanantry.scapeandrunparasites.world.gen.feature;

import com.dhanantry.scapeandrunparasites.init.SRPBlocks;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.WorldGenerator;

public abstract class WorldGenParasiteGenAbstract extends WorldGenerator {
   public WorldGenParasiteGenAbstract(boolean notify) {
      super(notify);
   }

   public boolean canGrowInto(Block blockType) {
      Material material = blockType.getDefaultState().getMaterial();
      return material == Material.AIR || material == Material.LEAVES || blockType == Blocks.GRASS || blockType == Blocks.DIRT || blockType == Blocks.LOG || blockType == Blocks.LOG2 || blockType == Blocks.SAPLING || blockType == Blocks.VINE || blockType == SRPBlocks.ParasiteBush;
   }

   public boolean isReplaceable(World world, BlockPos pos) {
      IBlockState state = world.getBlockState(pos);
      return state.getBlock().isAir(state, world, pos) || state.getBlock().isLeaves(state, world, pos) || state.getBlock().isWood(world, pos) || this.canGrowInto(state.getBlock()) || state.getBlock() == SRPBlocks.ParasiteBush;
   }
}
