package com.dhanantry.scapeandrunparasites.block;

import com.dhanantry.scapeandrunparasites.SRPMain;
import com.dhanantry.scapeandrunparasites.init.SRPBlocks;
import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.Item;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class BlockParasiteFog extends Block {
   public BlockParasiteFog(String name) {
      super(Material.AIR);
      this.setRegistryName(name);
      this.setUnlocalizedName("srparasites." + name);
      this.setCreativeTab(SRPMain.SRP_CREATIVETAB);
   }

   public boolean isOpaqueCube(IBlockState state) {
      return false;
   }

   public boolean isFullCube(IBlockState state) {
      return false;
   }

   public boolean isReplaceable(IBlockAccess worldIn, BlockPos pos) {
      return true;
   }

   public boolean canCollideCheck(IBlockState state, boolean hitIfLiquid) {
      return false;
   }

   public void dropBlockAsItemWithChance(World worldIn, BlockPos pos, IBlockState state, float chance, int fortune) {
   }

   public Item getItemDropped(IBlockState state, Random rand, int fortune) {
      return null;
   }

   @SideOnly(Side.CLIENT)
   public boolean shouldSideBeRendered(IBlockState blockState, IBlockAccess blockAccess, BlockPos pos, EnumFacing side) {
      IBlockState iblockstate = blockAccess.getBlockState(pos.offset(side));
      Block block = iblockstate.getBlock();
      if (this == SRPBlocks.ParasiteFog) {
         if (blockState != iblockstate) {
            return true;
         }

         if (block == this) {
            return false;
         }
      }

      return block == this ? false : super.shouldSideBeRendered(blockState, blockAccess, pos, side);
   }

   @SideOnly(Side.CLIENT)
   public BlockRenderLayer getRenderLayer() {
      return BlockRenderLayer.TRANSLUCENT;
   }

   @Nullable
   public AxisAlignedBB getCollisionBoundingBox(IBlockState blockState, IBlockAccess worldIn, BlockPos pos) {
      return NULL_AABB;
   }

   public BlockFaceShape getBlockFaceShape(IBlockAccess worldIn, IBlockState state, BlockPos pos, EnumFacing face) {
      return BlockFaceShape.UNDEFINED;
   }
}
