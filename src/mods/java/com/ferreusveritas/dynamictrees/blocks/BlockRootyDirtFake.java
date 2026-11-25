package com.ferreusveritas.dynamictrees.blocks;

import com.ferreusveritas.dynamictrees.ModBlocks;
import com.ferreusveritas.dynamictrees.ModTabs;
import com.ferreusveritas.dynamictrees.api.TreeHelper;
import com.ferreusveritas.dynamictrees.api.cells.CellNull;
import com.ferreusveritas.dynamictrees.api.cells.ICell;
import com.ferreusveritas.dynamictrees.api.network.MapSignal;
import com.ferreusveritas.dynamictrees.api.treedata.ILeavesProperties;
import com.ferreusveritas.dynamictrees.api.treedata.ITreePart;
import com.ferreusveritas.dynamictrees.systems.GrowSignal;
import com.ferreusveritas.dynamictrees.trees.TreeFamily;
import net.lax1dude.eaglercraft.v1_8.EaglercraftRandom;
import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.property.ExtendedBlockState;
import net.minecraftforge.common.property.IExtendedBlockState;
import net.minecraftforge.common.property.IUnlistedProperty;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class BlockRootyDirtFake extends Block implements ITreePart, MimicProperty.IMimic {
   public BlockRootyDirtFake(String name) {
      super(Material.GROUND);
      this.setSoundType(SoundType.GROUND);
      this.setTickRandomly(true);
      this.setUnlocalizedName(name);
      this.setRegistryName(name);
      this.setCreativeTab(ModTabs.dynamicTreesTab);
   }

   public void randomTick(World world, BlockPos pos, IBlockState state, EaglercraftRandom random) {
      for(EnumFacing dir : EnumFacing.VALUES) {
         if (TreeHelper.isBranch(world.getBlockState(pos.add(dir.getDirectionVec())))) {
            return;
         }
      }

      world.setBlockState(pos, ModBlocks.blockStates.dirt);
   }

   protected BlockStateContainer createBlockState() {
      return new ExtendedBlockState(this, new IProperty[0], new IUnlistedProperty[]{MimicProperty.MIMIC});
   }

   public IBlockState getExtendedState(IBlockState state, IBlockAccess access, BlockPos pos) {
      return (IBlockState)(state instanceof IExtendedBlockState ? ((IExtendedBlockState)state).withProperty(MimicProperty.MIMIC, this.getMimic(access, pos)) : state);
   }

   public IBlockState getMimic(IBlockAccess access, BlockPos pos) {
      return MimicProperty.getDirtMimic(access, pos);
   }

   public boolean shouldAnalyse() {
      return false;
   }

   public MapSignal analyse(IBlockState state, World world, BlockPos pos, EnumFacing fromDir, MapSignal signal) {
      return signal;
   }

   public ICell getHydrationCell(IBlockAccess world, BlockPos pos, IBlockState state, EnumFacing dir, ILeavesProperties leavesProperties) {
      return CellNull.NULLCELL;
   }

   public GrowSignal growSignal(World world, BlockPos pos, GrowSignal signal) {
      return signal;
   }

   public int probabilityForBlock(IBlockState state, IBlockAccess world, BlockPos pos, BlockBranch from) {
      return 0;
   }

   public int getRadiusForConnection(IBlockState state, IBlockAccess world, BlockPos pos, BlockBranch from, EnumFacing side, int fromRadius) {
      return 8;
   }

   public int getRadius(IBlockState state) {
      return 8;
   }

   public TreeFamily getFamily(IBlockState state, IBlockAccess world, BlockPos pos) {
      return TreeFamily.NULLFAMILY;
   }

   public int branchSupport(IBlockState state, IBlockAccess world, BlockBranch branch, BlockPos pos, EnumFacing dir, int radius) {
      return BlockBranch.setSupport(1, 1);
   }

   public ITreePart.TreePartType getTreePartType() {
      return ITreePart.TreePartType.OTHER;
   }

   @SideOnly(Side.CLIENT)
   public BlockRenderLayer getBlockLayer() {
      return BlockRenderLayer.CUTOUT_MIPPED;
   }
}
