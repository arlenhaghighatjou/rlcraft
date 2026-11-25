package com.ferreusveritas.dynamictrees.api.treedata;

import com.ferreusveritas.dynamictrees.api.cells.ICellKit;
import com.ferreusveritas.dynamictrees.blocks.BlockBranch;
import com.ferreusveritas.dynamictrees.trees.TreeFamily;
import net.lax1dude.eaglercraft.v1_8.EaglercraftRandom;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public interface ILeavesProperties {
   ILeavesProperties setTree(TreeFamily var1);

   TreeFamily getTree();

   IBlockState getPrimitiveLeaves();

   ItemStack getPrimitiveLeavesItemStack();

   ILeavesProperties setDynamicLeavesState(IBlockState var1);

   IBlockState getDynamicLeavesState();

   IBlockState getDynamicLeavesState(int var1);

   int getFlammability();

   int getFireSpreadSpeed();

   int getSmotherLeavesMax();

   int getLightRequirement();

   ICellKit getCellKit();

   @SideOnly(Side.CLIENT)
   int foliageColorMultiplier(IBlockState var1, IBlockAccess var2, BlockPos var3);

   boolean updateTick(World var1, BlockPos var2, IBlockState var3, EaglercraftRandom var4);

   boolean appearanceChangesWithHydro();

   int getRadiusForConnection(IBlockState var1, IBlockAccess var2, BlockPos var3, BlockBranch var4, EnumFacing var5, int var6);
}
