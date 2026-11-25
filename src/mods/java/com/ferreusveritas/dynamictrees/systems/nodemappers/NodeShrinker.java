package com.ferreusveritas.dynamictrees.systems.nodemappers;

import com.ferreusveritas.dynamictrees.api.TreeHelper;
import com.ferreusveritas.dynamictrees.api.network.INodeInspector;
import com.ferreusveritas.dynamictrees.blocks.BlockBranch;
import com.ferreusveritas.dynamictrees.trees.Species;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class NodeShrinker implements INodeInspector {
   private float radius;
   Species species;

   public NodeShrinker(Species species) {
      this.species = species;
   }

   public boolean run(IBlockState blockState, World world, BlockPos pos, EnumFacing fromDir) {
      BlockBranch branch = TreeHelper.getBranch(blockState);
      if (branch != null) {
         this.radius = (float)branch.getRadius(blockState);
         if (this.radius > 8.0F) {
            branch.setRadius(world, pos, 8, fromDir);
         }
      }

      return false;
   }

   public boolean returnRun(IBlockState blockState, World world, BlockPos pos, EnumFacing fromDir) {
      return false;
   }
}
