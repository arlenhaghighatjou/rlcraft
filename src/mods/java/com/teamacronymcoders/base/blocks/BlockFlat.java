package com.teamacronymcoders.base.blocks;

import javax.annotation.Nonnull;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public abstract class BlockFlat extends BlockSubBase {
   public static final AxisAlignedBB AABB = new AxisAlignedBB(0.0D, 0.0D, 0.0D, 1.0D, 0.0625D, 1.0D);

   public BlockFlat(Material mat, String[] names) {
      super(mat, names);
   }

   public boolean canPlaceBlockAt(World worldIn, @Nonnull BlockPos pos) {
      return super.canPlaceBlockAt(worldIn, pos) && this.canBlockStay(worldIn, pos);
   }

   public void neighborChanged(IBlockState state, World worldIn, BlockPos pos, Block blockIn, BlockPos fromPos) {
      this.checkForDrop(worldIn, pos, state);
   }

   public void checkForDrop(World worldIn, BlockPos pos, IBlockState state) {
      if (!this.canBlockStay(worldIn, pos)) {
         this.dropBlockAsItem(worldIn, pos, state, 0);
         worldIn.setBlockToAir(pos);
      }

   }

   public abstract boolean canBlockStay(World var1, BlockPos var2);

   @Nonnull
   public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
      return AABB;
   }

   public boolean isOpaqueCube(IBlockState state) {
      return false;
   }

   public boolean isFullCube(IBlockState state) {
      return false;
   }

   @SideOnly(Side.CLIENT)
   public boolean shouldSideBeRendered(IBlockState blockState, @Nonnull IBlockAccess blockAccess, @Nonnull BlockPos pos, EnumFacing side) {
      return side == EnumFacing.UP || blockAccess.getBlockState(pos.offset(side)).getBlock() == this || super.shouldSideBeRendered(blockState, blockAccess, pos, side);
   }
}
