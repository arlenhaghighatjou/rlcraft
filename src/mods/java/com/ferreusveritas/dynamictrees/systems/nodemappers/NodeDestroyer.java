package com.ferreusveritas.dynamictrees.systems.nodemappers;

import com.ferreusveritas.dynamictrees.api.TreeHelper;
import com.ferreusveritas.dynamictrees.api.network.INodeInspector;
import com.ferreusveritas.dynamictrees.blocks.BlockBranch;
import com.ferreusveritas.dynamictrees.trees.Species;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class NodeDestroyer implements INodeInspector {
   Species species;
   private final List<BlockPos> endPoints = new ArrayList(32);

   public NodeDestroyer(Species species) {
      this.species = species;
   }

   public List<BlockPos> getEnds() {
      return this.endPoints;
   }

   public boolean run(IBlockState blockState, World world, BlockPos pos, EnumFacing fromDir) {
      BlockBranch branch = TreeHelper.getBranch(blockState);
      if (branch != null && this.species.getFamily() == branch.getFamily()) {
         if ((float)branch.getRadius(blockState) == this.species.getFamily().getPrimaryThickness()) {
            this.endPoints.add(pos);
         }

         world.setBlockState(pos, Blocks.AIR.getDefaultState(), 0);
      }

      return true;
   }

   public boolean returnRun(IBlockState blockState, World world, BlockPos pos, EnumFacing fromDir) {
      return false;
   }
}
