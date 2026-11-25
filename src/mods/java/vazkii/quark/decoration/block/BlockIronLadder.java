package vazkii.quark.decoration.block;

import javax.annotation.Nonnull;
import net.minecraft.block.Block;
import net.minecraft.block.BlockHorizontal;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.Blocks;
import net.minecraft.init.SoundEvents;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.Mirror;
import net.minecraft.util.Rotation;
import net.minecraft.util.EnumFacing.Axis;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import vazkii.arl.block.BlockMod;
import vazkii.quark.base.block.IQuarkBlock;

public class BlockIronLadder extends BlockMod implements IQuarkBlock {
   public static final SoundType IRON_LADDER = new SoundType(1.0F, 1.0F, SoundEvents.BLOCK_METAL_BREAK, SoundEvents.BLOCK_LADDER_STEP, SoundEvents.BLOCK_METAL_PLACE, SoundEvents.BLOCK_METAL_HIT, SoundEvents.BLOCK_LADDER_FALL);
   public static final PropertyDirection FACING = BlockHorizontal.FACING;

   public BlockIronLadder() {
      super("iron_ladder", Material.CIRCUITS, new String[0]);
      this.setDefaultState(this.blockState.getBaseState().withProperty(FACING, EnumFacing.NORTH));
      this.setHardness(0.8F);
      this.setSoundType(IRON_LADDER);
      this.setCreativeTab(CreativeTabs.DECORATIONS);
   }

   @Nonnull
   public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
      return Blocks.LADDER.getBoundingBox(state, source, pos);
   }

   public boolean isOpaqueCube(IBlockState state) {
      return false;
   }

   public boolean isFullCube(IBlockState state) {
      return false;
   }

   public boolean canPlaceBlockOnSide(@Nonnull World worldIn, @Nonnull BlockPos pos, EnumFacing side) {
      return this.canBlockStay(worldIn, pos, side);
   }

   @Nonnull
   public IBlockState getStateForPlacement(World worldIn, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer) {
      if (facing.getAxis() != Axis.Y) {
         return this.getDefaultState().withProperty(FACING, facing);
      } else {
         IBlockState stateUp = worldIn.getBlockState(pos.up());
         return stateUp.getBlock() == this ? this.getDefaultState().withProperty(FACING, stateUp.getValue(FACING)) : this.getDefaultState();
      }
   }

   public void neighborChanged(IBlockState state, World worldIn, BlockPos pos, Block blockIn, BlockPos fromPos) {
      if (!this.canBlockStay(worldIn, pos)) {
         this.dropBlockAsItem(worldIn, pos, state, 0);
         worldIn.setBlockToAir(pos);
      }

   }

   protected boolean canBlockStay(World worldIn, BlockPos pos) {
      EnumFacing facing = (EnumFacing)worldIn.getBlockState(pos).getValue(FACING);
      return this.canBlockStay(worldIn, pos, facing);
   }

   public boolean canBlockStay(World worldIn, BlockPos pos, EnumFacing facing) {
      boolean solid = facing.getAxis() != Axis.Y && worldIn.getBlockState(pos.offset(facing.getOpposite())).isSideSolid(worldIn, pos.offset(facing.getOpposite()), facing);
      IBlockState topState = worldIn.getBlockState(pos.up());
      return solid || topState.getBlock() == this && (facing.getAxis() == Axis.Y || topState.getValue(FACING) == facing);
   }

   @Nonnull
   public IBlockState getStateFromMeta(int meta) {
      EnumFacing enumfacing = EnumFacing.getFront(meta);
      if (enumfacing.getAxis() == Axis.Y) {
         enumfacing = EnumFacing.NORTH;
      }

      return this.getDefaultState().withProperty(FACING, enumfacing);
   }

   @Nonnull
   @SideOnly(Side.CLIENT)
   public BlockRenderLayer getBlockLayer() {
      return BlockRenderLayer.CUTOUT;
   }

   public int getMetaFromState(IBlockState state) {
      return ((EnumFacing)state.getValue(FACING)).getIndex();
   }

   @Nonnull
   public IBlockState withRotation(@Nonnull IBlockState state, Rotation rot) {
      return state.withProperty(FACING, rot.rotate((EnumFacing)state.getValue(FACING)));
   }

   @Nonnull
   public IBlockState withMirror(@Nonnull IBlockState state, Mirror mirrorIn) {
      return state.withRotation(mirrorIn.toRotation((EnumFacing)state.getValue(FACING)));
   }

   @Nonnull
   protected BlockStateContainer createBlockState() {
      return new BlockStateContainer(this, new IProperty[]{FACING});
   }

   public boolean isLadder(IBlockState state, IBlockAccess world, BlockPos pos, EntityLivingBase entity) {
      return true;
   }

   @Nonnull
   public BlockFaceShape getBlockFaceShape(IBlockAccess worldIn, IBlockState state, BlockPos blockPos, EnumFacing face) {
      return BlockFaceShape.UNDEFINED;
   }
}
