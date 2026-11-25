package knightminer.inspirations.utility.block;

import net.lax1dude.eaglercraft.v1_8.EaglercraftRandom;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.BlockDirectional;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class BlockRedstoneCharge extends Block {
   public static final PropertyBool QUICK = PropertyBool.create("quick");
   public static final PropertyDirection FACING = BlockDirectional.FACING;
   protected static final AxisAlignedBB BOUNDS = new AxisAlignedBB(0.375D, 0.375D, 0.375D, 0.625D, 0.625D, 0.625D);

   public BlockRedstoneCharge() {
      super(Material.CIRCUITS);
      this.setHardness(0.0F);
      this.setLightLevel(0.5F);
      this.setDefaultState(this.blockState.getBaseState().withProperty(FACING, EnumFacing.DOWN).withProperty(QUICK, Boolean.valueOf(false)));
   }

   protected BlockStateContainer createBlockState() {
      return new BlockStateContainer(this, new IProperty[]{FACING, QUICK});
   }

   /** @deprecated */
   @Deprecated
   public IBlockState getStateFromMeta(int meta) {
      return this.getDefaultState().withProperty(QUICK, Boolean.valueOf((meta & 8) > 0)).withProperty(FACING, EnumFacing.getFront(meta & 7));
   }

   public int getMetaFromState(IBlockState state) {
      return ((EnumFacing)state.getValue(FACING)).getIndex() | (((Boolean)state.getValue(QUICK)).booleanValue() ? 8 : 0);
   }

   public int tickRate(World world) {
      return 20;
   }

   public void onBlockAdded(World world, BlockPos pos, IBlockState state) {
      if (!world.isRemote) {
         world.notifyNeighborsOfStateChange(pos.offset((EnumFacing)state.getValue(FACING)), this, false);
         world.scheduleUpdate(pos, this, ((Boolean)state.getValue(QUICK)).booleanValue() ? 2 : 20);
      }

   }

   public void breakBlock(World world, BlockPos pos, IBlockState state) {
      world.notifyNeighborsOfStateChange(pos, this, false);
      world.notifyNeighborsOfStateChange(pos.offset((EnumFacing)state.getValue(FACING)), this, false);
      super.breakBlock(world, pos, state);
   }

   public void randomTick(World worldIn, BlockPos pos, IBlockState state, EaglercraftRandom random) {
   }

   public void updateTick(World world, BlockPos pos, IBlockState state, EaglercraftRandom rand) {
      if (!world.isRemote) {
         world.setBlockToAir(pos);
         world.playSound((EntityPlayer)null, pos, SoundEvents.BLOCK_REDSTONE_TORCH_BURNOUT, SoundCategory.BLOCKS, 0.5F, 2.6F + (world.rand.nextFloat() - world.rand.nextFloat()) * 0.8F);
      }

   }

   /** @deprecated */
   @Deprecated
   public int getWeakPower(IBlockState blockState, IBlockAccess blockAccess, BlockPos pos, EnumFacing side) {
      return 15;
   }

   /** @deprecated */
   @Deprecated
   public int getStrongPower(IBlockState state, IBlockAccess blockAccess, BlockPos pos, EnumFacing side) {
      return ((EnumFacing)state.getValue(FACING)).getOpposite() == side ? 15 : 0;
   }

   /** @deprecated */
   @Deprecated
   public boolean canProvidePower(IBlockState state) {
      return true;
   }

   public boolean canConnectRedstone(IBlockState state, IBlockAccess world, BlockPos pos, @Nullable EnumFacing side) {
      return false;
   }

   /** @deprecated */
   @Deprecated
   @Nonnull
   public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
      return BOUNDS;
   }

   /** @deprecated */
   @Deprecated
   @Nullable
   public AxisAlignedBB getCollisionBoundingBox(IBlockState state, IBlockAccess world, BlockPos pos) {
      return NULL_AABB;
   }

   public boolean canPlaceBlockAt(World world, BlockPos pos) {
      return super.canPlaceBlockAt(world, pos) && !world.getBlockState(pos).getMaterial().isLiquid();
   }

   /** @deprecated */
   @Deprecated
   public BlockFaceShape getBlockFaceShape(IBlockAccess world, IBlockState state, BlockPos pos, EnumFacing side) {
      return BlockFaceShape.UNDEFINED;
   }

   /** @deprecated */
   @Deprecated
   public boolean isFullCube(IBlockState state) {
      return false;
   }

   /** @deprecated */
   @Deprecated
   public boolean isOpaqueCube(IBlockState state) {
      return false;
   }

   @SideOnly(Side.CLIENT)
   public void randomDisplayTick(IBlockState stateIn, World worldIn, BlockPos pos, EaglercraftRandom rand) {
      EnumFacing facing = (EnumFacing)stateIn.getValue(FACING);
      int offX = facing.getFrontOffsetX();
      int offY = facing.getFrontOffsetY();
      int offZ = facing.getFrontOffsetZ();
      double x = (double)pos.getX() + 0.5D;
      double y = (double)pos.getY() + 0.5D;
      double z = (double)pos.getZ() + 0.5D;

      for(double i = 0.0D; i <= 0.25D; i += 0.05D) {
         worldIn.spawnParticle(EnumParticleTypes.REDSTONE, x + (double)offX * i, y + (double)offY * i, z + (double)offZ * i, 0.0D, 0.0D, 0.0D, new int[0]);
      }

   }

   /** @deprecated */
   @Deprecated
   @Nonnull
   public EnumBlockRenderType getRenderType(IBlockState state) {
      return EnumBlockRenderType.INVISIBLE;
   }
}
