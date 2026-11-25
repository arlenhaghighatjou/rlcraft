package com.ferreusveritas.dynamictrees.systems.nodemappers;

import com.ferreusveritas.dynamictrees.api.TreeHelper;
import com.ferreusveritas.dynamictrees.api.network.INodeInspector;
import com.ferreusveritas.dynamictrees.blocks.BlockBranch;
import com.ferreusveritas.dynamictrees.blocks.BlockDynamicLeaves;
import com.ferreusveritas.dynamictrees.trees.Species;
import com.ferreusveritas.dynamictrees.trees.TreeFamily;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class NodeTransform implements INodeInspector {
   Species fromSpecies;
   Species toSpecies;
   TreeFamily toFamily;
   IBlockState toSpeciesLeaves;

   public NodeTransform(Species fromSpecies, Species toSpecies) {
      this.fromSpecies = fromSpecies;
      this.toSpecies = toSpecies;
      this.toFamily = toSpecies.getFamily();
      this.toSpeciesLeaves = toSpecies.getLeavesProperties().getDynamicLeavesState();
   }

   public boolean run(IBlockState blockState, World world, BlockPos pos, EnumFacing fromDir) {
      BlockBranch branch = TreeHelper.getBranch(blockState);
      if (branch != null && this.fromSpecies.getFamily() == branch.getFamily()) {
         int radius = branch.getRadius(blockState);
         if (radius > 0) {
            this.toFamily.getDynamicBranch().setRadius(world, pos, radius, (EnumFacing)null);
            if (radius == 1) {
               this.transformSurroundingLeaves(world, pos);
            }
         }
      }

      return true;
   }

   public boolean returnRun(IBlockState blockState, World world, BlockPos pos, EnumFacing fromDir) {
      return false;
   }

   public void transformSurroundingLeaves(World world, BlockPos twigPos) {
      if (!world.isRemote) {
         for(BlockPos leavesPos : BlockPos.getAllInBox(twigPos.add(-3, -3, -3), twigPos.add(3, 3, 3))) {
            if (this.fromSpecies.getLeavesProperties().getCellKit().getLeafCluster().getVoxel(twigPos, leavesPos) != 0) {
               IBlockState state = world.getBlockState(leavesPos);
               if (this.fromSpecies.getFamily().isCompatibleGenericLeaves(state, world, leavesPos)) {
                  int hydro = state.getBlock() instanceof BlockDynamicLeaves ? ((Integer)state.getValue(BlockDynamicLeaves.HYDRO)).intValue() : 2;
                  world.setBlockState(leavesPos, this.toSpeciesLeaves == null ? Blocks.AIR.getDefaultState() : this.toSpeciesLeaves.withProperty(BlockDynamicLeaves.HYDRO, Integer.valueOf(hydro)));
               }
            }
         }
      }

   }
}
