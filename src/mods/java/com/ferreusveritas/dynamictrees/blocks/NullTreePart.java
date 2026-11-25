package com.ferreusveritas.dynamictrees.blocks;

import com.ferreusveritas.dynamictrees.api.cells.CellNull;
import com.ferreusveritas.dynamictrees.api.cells.ICell;
import com.ferreusveritas.dynamictrees.api.network.MapSignal;
import com.ferreusveritas.dynamictrees.api.treedata.ILeavesProperties;
import com.ferreusveritas.dynamictrees.api.treedata.ITreePart;
import com.ferreusveritas.dynamictrees.systems.GrowSignal;
import com.ferreusveritas.dynamictrees.trees.TreeFamily;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class NullTreePart implements ITreePart {
   public NullTreePart() {
   }

   public ICell getHydrationCell(IBlockAccess world, BlockPos pos, IBlockState state, EnumFacing dir, ILeavesProperties leavesTree) {
      return CellNull.NULLCELL;
   }

   public GrowSignal growSignal(World world, BlockPos pos, GrowSignal signal) {
      return signal;
   }

   public int getRadiusForConnection(IBlockState state, IBlockAccess blockAccess, BlockPos pos, BlockBranch from, EnumFacing side, int fromRadius) {
      if (fromRadius == 1) {
         return from.getFamily().isCompatibleVanillaLeaves(state, blockAccess, pos) ? 1 : 0;
      } else {
         return 0;
      }
   }

   public int probabilityForBlock(IBlockState state, IBlockAccess world, BlockPos pos, BlockBranch from) {
      return state.getBlock().isAir(state, world, pos) ? 1 : 0;
   }

   public int getRadius(IBlockState state) {
      return 0;
   }

   public boolean shouldAnalyse() {
      return false;
   }

   public MapSignal analyse(IBlockState state, World world, BlockPos pos, EnumFacing fromDir, MapSignal signal) {
      return signal;
   }

   public int branchSupport(IBlockState state, IBlockAccess world, BlockBranch branch, BlockPos pos, EnumFacing dir, int radius) {
      return BlockBranch.setSupport(0, branch.getFamily().isCompatibleVanillaLeaves(state, world, pos) ? 1 : 0);
   }

   public TreeFamily getFamily(IBlockState state, IBlockAccess world, BlockPos pos) {
      return TreeFamily.NULLFAMILY;
   }

   public final ITreePart.TreePartType getTreePartType() {
      return ITreePart.TreePartType.NULL;
   }
}
