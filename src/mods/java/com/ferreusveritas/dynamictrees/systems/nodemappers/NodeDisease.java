package com.ferreusveritas.dynamictrees.systems.nodemappers;

import com.ferreusveritas.dynamictrees.api.TreeHelper;
import com.ferreusveritas.dynamictrees.api.network.INodeInspector;
import com.ferreusveritas.dynamictrees.blocks.BlockBranch;
import com.ferreusveritas.dynamictrees.trees.Species;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class NodeDisease implements INodeInspector {
   Species species;

   public NodeDisease(Species tree) {
      this.species = tree;
   }

   public boolean run(IBlockState blockState, World world, BlockPos pos, EnumFacing fromDir) {
      BlockBranch branch = TreeHelper.getBranch(blockState);
      if (branch != null && this.species.getFamily() == branch.getFamily() && branch.getRadius(blockState) == 1) {
         world.setBlockToAir(pos);
      }

      return true;
   }

   public boolean returnRun(IBlockState blockState, World world, BlockPos pos, EnumFacing fromDir) {
      return false;
   }
}
