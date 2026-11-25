package com.alcatrazescapee.notreepunching.common.blocks;

import com.alcatrazescapee.alcatrazcore.block.BlockTileCore;
import com.alcatrazescapee.notreepunching.NoTreePunching;
import com.alcatrazescapee.notreepunching.common.tile.TileLargeVessel;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

@ParametersAreNonnullByDefault
public class BlockLargeVessel extends BlockTileCore {
   private static final AxisAlignedBB AABB = new AxisAlignedBB(0.125D, 0.0D, 0.125D, 0.875D, 0.875D, 0.875D);

   public BlockLargeVessel() {
      super(Material.GROUND);
      this.setHarvestLevel("pickaxe", 0);
      this.setHardness(1.0F);
   }

   public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
      if (!worldIn.isRemote && !playerIn.isSneaking()) {
         playerIn.openGui(NoTreePunching.getInstance(), 0, worldIn, pos.getX(), pos.getY(), pos.getZ());
      }

      return true;
   }

   @Nullable
   public TileEntity createNewTileEntity(World worldIn, int meta) {
      return new TileLargeVessel();
   }

   public boolean isFullCube(IBlockState state) {
      return false;
   }

   @Nonnull
   public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
      return AABB;
   }

   @Nonnull
   public BlockFaceShape getBlockFaceShape(IBlockAccess worldIn, IBlockState state, BlockPos pos, EnumFacing face) {
      return BlockFaceShape.UNDEFINED;
   }

   @Nullable
   public AxisAlignedBB getCollisionBoundingBox(IBlockState blockState, IBlockAccess worldIn, BlockPos pos) {
      return AABB;
   }

   public boolean isOpaqueCube(IBlockState state) {
      return false;
   }
}
