package com.ferreusveritas.dynamictrees.api.treedata;

import com.ferreusveritas.dynamictrees.api.cells.ICell;
import com.ferreusveritas.dynamictrees.api.network.MapSignal;
import com.ferreusveritas.dynamictrees.blocks.BlockBranch;
import com.ferreusveritas.dynamictrees.systems.GrowSignal;
import com.ferreusveritas.dynamictrees.trees.TreeFamily;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public interface ITreePart {
   ICell getHydrationCell(IBlockAccess var1, BlockPos var2, IBlockState var3, EnumFacing var4, ILeavesProperties var5);

   GrowSignal growSignal(World var1, BlockPos var2, GrowSignal var3);

   int probabilityForBlock(IBlockState var1, IBlockAccess var2, BlockPos var3, BlockBranch var4);

   int getRadiusForConnection(IBlockState var1, IBlockAccess var2, BlockPos var3, BlockBranch var4, EnumFacing var5, int var6);

   int getRadius(IBlockState var1);

   boolean shouldAnalyse();

   MapSignal analyse(IBlockState var1, World var2, BlockPos var3, EnumFacing var4, MapSignal var5);

   TreeFamily getFamily(IBlockState var1, IBlockAccess var2, BlockPos var3);

   int branchSupport(IBlockState var1, IBlockAccess var2, BlockBranch var3, BlockPos var4, EnumFacing var5, int var6);

   ITreePart.TreePartType getTreePartType();

   default boolean isRootNode() {
      return false;
   }

   public static enum TreePartType {
      NULL,
      ROOT,
      BRANCH,
      LEAVES,
      OTHER;

      private TreePartType() {
      }
   }
}
