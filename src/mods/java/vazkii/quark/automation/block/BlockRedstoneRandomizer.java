package vazkii.quark.automation.block;

import java.util.EnumSet;
import java.util.Random;
import javax.annotation.Nonnull;
import net.minecraft.block.Block;
import net.minecraft.block.BlockHorizontal;
import net.minecraft.block.BlockRedstoneWire;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumFacing.Axis;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.event.ForgeEventFactory;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import vazkii.arl.block.BlockMod;
import vazkii.arl.util.RotationHandler;
import vazkii.quark.base.block.IQuarkBlock;

public class BlockRedstoneRandomizer extends BlockMod implements IQuarkBlock {
   protected static final AxisAlignedBB REDSTONE_DIODE_AABB = new AxisAlignedBB(0.0D, 0.0D, 0.0D, 1.0D, 0.125D, 1.0D);
   public static final PropertyDirection FACING = BlockHorizontal.FACING;
   public static final PropertyBool POWERED = PropertyBool.create("powered");
   public static final PropertyBool POWER_LEFT = PropertyBool.create("power_left");

   public BlockRedstoneRandomizer() {
      super("redstone_randomizer", Material.CIRCUITS, new String[0]);
      this.setDefaultState(this.blockState.getBaseState().withProperty(FACING, EnumFacing.NORTH).withProperty(POWERED, Boolean.valueOf(false)).withProperty(POWER_LEFT, Boolean.valueOf(false)));
      this.setCreativeTab(CreativeTabs.REDSTONE);
      this.setSoundType(SoundType.WOOD);
   }

   public void updateTick(World worldIn, BlockPos pos, IBlockState state, Random rand) {
      boolean isPowered = this.isPowered(state);
      boolean willBePowered = this.shouldBePowered(worldIn, pos, state);
      if (isPowered != willBePowered) {
         IBlockState target = state.withProperty(POWERED, Boolean.valueOf(willBePowered));
         if (willBePowered) {
            target = target.withProperty(POWER_LEFT, Boolean.valueOf(worldIn.rand.nextBoolean()));
         }

         worldIn.setBlockState(pos, target);
      }

   }

   protected int getActiveSignal(IBlockState state, EnumFacing side) {
      return this.isPowered(state) && side == this.getOutputFace(state) ? 15 : 0;
   }

   protected void updateState(World world, BlockPos pos, IBlockState currState) {
      boolean isPowered = this.isPowered(currState);
      boolean willBePowered = this.shouldBePowered(world, pos, currState);
      if (isPowered != willBePowered) {
         world.updateBlockTick(pos, this, 2, -1);
      }

   }

   private EnumFacing getOutputFace(IBlockState state) {
      EnumFacing target = ((Boolean)state.getValue(POWER_LEFT)).booleanValue() ? EnumFacing.WEST : EnumFacing.EAST;
      return this.correct(state, target);
   }

   private EnumFacing correct(IBlockState state, EnumFacing target) {
      return RotationHandler.rotateFacing(target, (EnumFacing)state.getValue(FACING));
   }

   @Nonnull
   protected BlockStateContainer createBlockState() {
      return new BlockStateContainer(this, new IProperty[]{FACING, POWERED, POWER_LEFT});
   }

   public int getMetaFromState(IBlockState state) {
      return ((EnumFacing)state.getValue(FACING)).ordinal() - 2 + (((Boolean)state.getValue(POWERED)).booleanValue() ? 4 : 0) + (((Boolean)state.getValue(POWER_LEFT)).booleanValue() ? 8 : 0);
   }

   @Nonnull
   public IBlockState getStateFromMeta(int meta) {
      EnumFacing face = EnumFacing.VALUES[(meta & 3) + 2];
      boolean powered = (meta & 4) != 0;
      boolean left = (meta & 8) != 0;
      return this.getDefaultState().withProperty(FACING, face).withProperty(POWERED, Boolean.valueOf(powered)).withProperty(POWER_LEFT, Boolean.valueOf(left));
   }

   @Nonnull
   public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
      return REDSTONE_DIODE_AABB;
   }

   public boolean isFullCube(IBlockState state) {
      return false;
   }

   public boolean canPlaceBlockAt(World worldIn, @Nonnull BlockPos pos) {
      return worldIn.getBlockState(pos.down()).isSideSolid(worldIn, pos.down(), EnumFacing.UP) && super.canPlaceBlockAt(worldIn, pos);
   }

   public boolean canBlockStay(World worldIn, BlockPos pos) {
      return worldIn.getBlockState(pos.down()).isSideSolid(worldIn, pos.down(), EnumFacing.UP);
   }

   @SideOnly(Side.CLIENT)
   public boolean shouldSideBeRendered(IBlockState blockState, @Nonnull IBlockAccess blockAccess, @Nonnull BlockPos pos, EnumFacing side) {
      return side.getAxis() != Axis.Y;
   }

   protected boolean isPowered(IBlockState state) {
      return ((Boolean)state.getValue(POWERED)).booleanValue();
   }

   public int getStrongPower(IBlockState blockState, IBlockAccess blockAccess, BlockPos pos, EnumFacing side) {
      return blockState.getWeakPower(blockAccess, pos, side);
   }

   public int getWeakPower(IBlockState blockState, IBlockAccess blockAccess, BlockPos pos, EnumFacing side) {
      return !this.isPowered(blockState) ? 0 : this.getActiveSignal(blockState, side);
   }

   public void neighborChanged(IBlockState state, World worldIn, BlockPos pos, Block blockIn, BlockPos fromPos) {
      if (this.canBlockStay(worldIn, pos)) {
         this.updateState(worldIn, pos, state);
      } else {
         this.dropBlockAsItem(worldIn, pos, state, 0);
         worldIn.setBlockToAir(pos);

         for(EnumFacing enumfacing : EnumFacing.values()) {
            worldIn.notifyNeighborsOfStateChange(pos.offset(enumfacing), this, false);
         }
      }

   }

   protected int calculateInputStrength(World worldIn, BlockPos pos, IBlockState state) {
      EnumFacing enumfacing = (EnumFacing)state.getValue(FACING);
      BlockPos blockpos = pos.offset(enumfacing);
      int i = worldIn.getRedstonePower(blockpos, enumfacing);
      if (i >= 15) {
         return i;
      } else {
         IBlockState iblockstate = worldIn.getBlockState(blockpos);
         return Math.max(i, iblockstate.getBlock() == Blocks.REDSTONE_WIRE ? ((Integer)iblockstate.getValue(BlockRedstoneWire.POWER)).intValue() : 0);
      }
   }

   protected boolean shouldBePowered(World world, BlockPos pos, IBlockState currState) {
      return this.calculateInputStrength(world, pos, currState) > 0;
   }

   public boolean canProvidePower(IBlockState state) {
      return true;
   }

   @Nonnull
   public IBlockState getStateForPlacement(World worldIn, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer) {
      return this.getDefaultState().withProperty(FACING, placer.getHorizontalFacing().getOpposite());
   }

   public void onBlockPlacedBy(World worldIn, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack) {
      if (this.shouldBePowered(worldIn, pos, state)) {
         worldIn.scheduleUpdate(pos, this, 1);
      }

   }

   public void onBlockAdded(World worldIn, BlockPos pos, IBlockState state) {
      this.notifyNeighbors(worldIn, pos, state);
   }

   protected void notifyNeighbors(World worldIn, BlockPos pos, IBlockState state) {
      notify(this, worldIn, pos, state);
   }

   public static void notify(Block block, World worldIn, BlockPos pos, IBlockState state) {
      EnumFacing enumfacing = (EnumFacing)state.getValue(FACING);
      BlockPos blockpos = pos.offset(enumfacing.getOpposite());
      if (!ForgeEventFactory.onNeighborNotify(worldIn, pos, worldIn.getBlockState(pos), EnumSet.of(enumfacing.getOpposite()), false).isCanceled()) {
         worldIn.neighborChanged(blockpos, block, pos);
         worldIn.notifyNeighborsOfStateExcept(blockpos, block, enumfacing);
      }
   }

   public void breakBlock(@Nonnull World worldIn, @Nonnull BlockPos pos, @Nonnull IBlockState state) {
      if (this.isPowered(state)) {
         for(EnumFacing enumfacing : EnumFacing.values()) {
            worldIn.notifyNeighborsOfStateChange(pos.offset(enumfacing), this, false);
         }
      }

      super.breakBlock(worldIn, pos, state);
   }

   public boolean isOpaqueCube(IBlockState state) {
      return false;
   }

   public boolean rotateBlock(World world, @Nonnull BlockPos pos, @Nonnull EnumFacing axis) {
      if (super.rotateBlock(world, pos, axis)) {
         IBlockState state = world.getBlockState(pos);
         state = state.withProperty(POWERED, Boolean.valueOf(false));
         world.setBlockState(pos, state);
         if (this.shouldBePowered(world, pos, state)) {
            world.scheduleUpdate(pos, this, 1);
         }

         return true;
      } else {
         return false;
      }
   }

   @Nonnull
   public BlockFaceShape getBlockFaceShape(IBlockAccess worldIn, IBlockState state, BlockPos pos, EnumFacing face) {
      return face == EnumFacing.DOWN ? BlockFaceShape.SOLID : BlockFaceShape.UNDEFINED;
   }

   @Nonnull
   @SideOnly(Side.CLIENT)
   public BlockRenderLayer getBlockLayer() {
      return BlockRenderLayer.CUTOUT;
   }
}
