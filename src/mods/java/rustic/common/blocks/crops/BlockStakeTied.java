package rustic.common.blocks.crops;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumFacing.Axis;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.registry.GameRegistry;
import rustic.common.blocks.BlockBase;
import rustic.common.blocks.BlockRope;
import rustic.common.blocks.ModBlocks;

public class BlockStakeTied extends BlockBase {
   public static final PropertyBool NORTH = PropertyBool.create("north");
   public static final PropertyBool WEST = PropertyBool.create("west");
   public static final PropertyBool SOUTH = PropertyBool.create("south");
   public static final PropertyBool EAST = PropertyBool.create("east");
   protected static final AxisAlignedBB KNOT_AABB = new AxisAlignedBB(0.3125D, 0.0D, 0.3125D, 0.6875D, 1.0D, 0.6875D);
   protected static final AxisAlignedBB STAKE_AABB = new AxisAlignedBB(0.375D, 0.0D, 0.375D, 0.625D, 1.0D, 0.625D);

   public BlockStakeTied() {
      super(Material.WOOD, "stake_tied", false);
      this.setSoundType(SoundType.WOOD);
      this.setHardness(1.0F);
      this.setResistance(5.0F);
      this.setDefaultState(this.blockState.getBaseState().withProperty(NORTH, Boolean.valueOf(false)).withProperty(WEST, Boolean.valueOf(false)).withProperty(SOUTH, Boolean.valueOf(false)).withProperty(EAST, Boolean.valueOf(false)));
      GameRegistry.findRegistry(Block.class).register(this);
      Blocks.FIRE.setFireInfo(this, 15, 40);
   }

   public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
      return KNOT_AABB;
   }

   public AxisAlignedBB getCollisionBoundingBox(IBlockState blockState, IBlockAccess worldIn, BlockPos pos) {
      return STAKE_AABB;
   }

   public boolean isOpaqueCube(IBlockState state) {
      return false;
   }

   public boolean isFullCube(IBlockState state) {
      return false;
   }

   public boolean isPassable(IBlockAccess worldIn, BlockPos pos) {
      return false;
   }

   public void breakBlock(World world, BlockPos pos, IBlockState state) {
      super.breakBlock(world, pos, state);
      world.setBlockState(pos, ModBlocks.CROP_STAKE.getDefaultState(), 3);
      this.dropBlockAsItem(world, pos, state, 0);
      world.scheduleUpdate(pos, ModBlocks.CROP_STAKE, 1);
   }

   public List<ItemStack> getDrops(IBlockAccess world, BlockPos pos, IBlockState state, int fortune) {
      List<ItemStack> stacks = new ArrayList();
      stacks.add(new ItemStack(ModBlocks.ROPE, 1));
      return stacks;
   }

   protected BlockStateContainer createBlockState() {
      return new BlockStateContainer(this, new IProperty[]{NORTH, WEST, SOUTH, EAST});
   }

   public IBlockState getStateFromMeta(int meta) {
      return this.getDefaultState();
   }

   public int getMetaFromState(IBlockState state) {
      return 0;
   }

   public IBlockState getActualState(IBlockState state, IBlockAccess world, BlockPos pos) {
      boolean n = false;
      boolean w = false;
      boolean s = false;
      boolean e = false;
      IBlockState state1 = world.getBlockState(pos.north());
      if (state1.getBlock() instanceof BlockRope && state1.getValue(BlockRope.AXIS) == Axis.Z) {
         n = true;
      }

      if (state1.getBlock() == ModBlocks.GRAPE_LEAVES && state1.getValue(BlockGrapeLeaves.AXIS) == Axis.Z) {
         n = true;
      }

      if (state1.getBlock() == this) {
         n = true;
      }

      state1 = world.getBlockState(pos.west());
      if (state1.getBlock() instanceof BlockRope && state1.getValue(BlockRope.AXIS) == Axis.X) {
         w = true;
      }

      if (state1.getBlock() == ModBlocks.GRAPE_LEAVES && state1.getValue(BlockGrapeLeaves.AXIS) == Axis.X) {
         w = true;
      }

      if (state1.getBlock() == this) {
         w = true;
      }

      state1 = world.getBlockState(pos.south());
      if (state1.getBlock() instanceof BlockRope && state1.getValue(BlockRope.AXIS) == Axis.Z) {
         s = true;
      }

      if (state1.getBlock() == ModBlocks.GRAPE_LEAVES && state1.getValue(BlockGrapeLeaves.AXIS) == Axis.Z) {
         s = true;
      }

      if (state1.getBlock() == this) {
         s = true;
      }

      state1 = world.getBlockState(pos.east());
      if (state1.getBlock() instanceof BlockRope && state1.getValue(BlockRope.AXIS) == Axis.X) {
         e = true;
      }

      if (state1.getBlock() == ModBlocks.GRAPE_LEAVES && state1.getValue(BlockGrapeLeaves.AXIS) == Axis.X) {
         e = true;
      }

      if (state1.getBlock() == this) {
         e = true;
      }

      return this.getDefaultState().withProperty(NORTH, Boolean.valueOf(n)).withProperty(WEST, Boolean.valueOf(w)).withProperty(SOUTH, Boolean.valueOf(s)).withProperty(EAST, Boolean.valueOf(e));
   }

   public ItemStack getPickBlock(IBlockState state, RayTraceResult target, World world, BlockPos pos, EntityPlayer player) {
      return new ItemStack(ModBlocks.ROPE);
   }

   public BlockFaceShape getBlockFaceShape(IBlockAccess world, IBlockState state, BlockPos pos, EnumFacing side) {
      return side != EnumFacing.UP && side != EnumFacing.DOWN ? BlockFaceShape.MIDDLE_POLE : BlockFaceShape.CENTER;
   }
}
