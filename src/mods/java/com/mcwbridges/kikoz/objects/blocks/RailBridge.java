package com.mcwbridges.kikoz.objects.blocks;

import com.mcwbridges.kikoz.Mcwbridges;
import com.mcwbridges.kikoz.init.BlockInit;
import com.mcwbridges.kikoz.init.ItemInit;
import java.util.List;
import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.EnumFacing.Axis;
import net.minecraft.util.EnumFacing.Plane;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class RailBridge extends Block {
   public static final AxisAlignedBB HANDRAIL2_X = new AxisAlignedBB(-0.3125D, 1.0625D, 0.0D, -0.125D, 1.9375D, 1.0D);
   public static final AxisAlignedBB HANDRAIL1_X = new AxisAlignedBB(1.125D, 1.0625D, 0.0D, 1.3125D, 1.9375D, 1.0D);
   public static final AxisAlignedBB PART_BASE_Z = new AxisAlignedBB(-0.3125D, 0.6875D, 0.0D, 1.3125D, 1.0D, 1.0D);
   public static final AxisAlignedBB HANDRAIL1_Z = new AxisAlignedBB(0.0D, 1.0625D, 1.125D, 1.0D, 1.9375D, 1.3125D);
   public static final AxisAlignedBB HANDRAIL2_Z = new AxisAlignedBB(0.0D, 1.0625D, -0.3125D, 1.0D, 1.9375D, -0.125D);
   public static final AxisAlignedBB PART_BASE_X = new AxisAlignedBB(0.0D, 0.6875D, -0.3125D, 1.0D, 1.0D, 1.3125D);
   public static final PropertyBool LIT = PropertyBool.create("lit");
   public static final PropertyDirection FACING = PropertyDirection.create("facing", Plane.HORIZONTAL);

   public BlockFaceShape getBlockFaceShape(IBlockAccess worldIn, IBlockState state, BlockPos pos, EnumFacing face) {
      return face == EnumFacing.UP ? BlockFaceShape.UNDEFINED : super.getBlockFaceShape(worldIn, state, pos, face);
   }

   public RailBridge(String name, Material material) {
      super(material);
      this.setDefaultState(this.blockState.getBaseState().withProperty(LIT, Boolean.valueOf(false)).withProperty(FACING, EnumFacing.NORTH));
      this.setUnlocalizedName(name);
      this.setRegistryName(name);
      this.setCreativeTab(Mcwbridges.MACAWBRIDGESTAB);
      this.setHardness(0.5F);
      this.setHarvestLevel("axe", 1);
      this.setResistance(20.0F);
      this.setSoundType(SoundType.WOOD);
      BlockInit.BLOCKS.add(this);
      ItemInit.ITEMS.add((new ItemBlock(this)).setRegistryName(this.getRegistryName()));
   }

   public int getLightValue(IBlockState state, IBlockAccess world, BlockPos pos) {
      return ((Boolean)state.getValue(LIT)).booleanValue() ? 7 : (!((Boolean)state.getValue(LIT)).booleanValue() ? 0 : 0);
   }

   public boolean canProvidePower(IBlockState state) {
      return ((Boolean)state.getValue(LIT)).booleanValue() ? true : (!((Boolean)state.getValue(LIT)).booleanValue() ? false : false);
   }

   public int getStrongPower(IBlockState state, IBlockAccess blockAccess, BlockPos pos, EnumFacing side) {
      return ((Boolean)state.getValue(LIT)).booleanValue() ? 15 : (!((Boolean)state.getValue(LIT)).booleanValue() ? 0 : 0);
   }

   public int getWeakPower(IBlockState state, IBlockAccess blockAccess, BlockPos pos, EnumFacing side) {
      return ((Boolean)state.getValue(LIT)).booleanValue() ? 1 : (!((Boolean)state.getValue(LIT)).booleanValue() ? 0 : 0);
   }

   public static void dropTorch(World world, BlockPos pos) {
      spawnAsEntity(world, pos, new ItemStack(Blocks.REDSTONE_TORCH));
   }

   public void onBlockHarvested(World world, BlockPos pos, IBlockState state, EntityPlayer player) {
      super.onBlockHarvested(world, pos, state, player);
      Boolean i = (Boolean)state.getValue(LIT);
      if (i.booleanValue()) {
         dropTorch(world, pos);
      }

   }

   public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
      ItemStack itemstack = playerIn.getHeldItem(hand);
      Boolean i = (Boolean)state.getValue(LIT);
      if (!this.canBePotted(itemstack) && !i.booleanValue()) {
         return false;
      } else {
         if (i.booleanValue()) {
            state = state.cycleProperty(LIT);
            worldIn.setBlockState(pos, state, 2);
            dropTorch(worldIn, pos);
         }

         if (!i.booleanValue() && this.canBePotted(itemstack)) {
            state = state.cycleProperty(LIT);
            worldIn.setBlockState(pos, state, 2);
            if (!playerIn.capabilities.isCreativeMode) {
               itemstack.shrink(1);
            }
         }

         return true;
      }
   }

   private boolean canBePotted(ItemStack stack) {
      Block block = Block.getBlockFromItem(stack.getItem());
      return block == Blocks.REDSTONE_TORCH;
   }

   public IBlockState getStateFromMeta(int meta) {
      EnumFacing facing = EnumFacing.getFront(meta);
      if (facing.getAxis() == Axis.Y) {
         facing = EnumFacing.NORTH;
      }

      return this.getDefaultState().withProperty(FACING, facing);
   }

   public int getMetaFromState(IBlockState state) {
      return ((EnumFacing)state.getValue(FACING)).getIndex();
   }

   protected BlockStateContainer createBlockState() {
      return new BlockStateContainer(this, new IProperty[]{FACING, LIT});
   }

   public IBlockState getStateForPlacement(World worldIn, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer) {
      return this.getDefaultState().withProperty(FACING, placer.getHorizontalFacing().getOpposite());
   }

   public boolean canPlaceBlockOnSide(World worldIn, BlockPos pos, EnumFacing side) {
      return true;
   }

   private boolean canBlockStay(World worldIn, BlockPos pos) {
      return worldIn.getBlockState(pos.down()).isSideSolid(worldIn, pos, EnumFacing.UP);
   }

   public boolean canPlaceBlockAt(World worldIn, BlockPos pos) {
      return super.canPlaceBlockAt(worldIn, pos) ? this.canBlockStay(worldIn, pos) : false;
   }

   public BlockRenderLayer getBlockLayer() {
      return BlockRenderLayer.CUTOUT;
   }

   public boolean isFullBlock(IBlockState state) {
      return true;
   }

   public boolean isFullCube(IBlockState state) {
      return true;
   }

   public boolean isOpaqueCube(IBlockState state) {
      return false;
   }

   public void addCollisionBoxToList(IBlockState state, World worldIn, BlockPos pos, AxisAlignedBB entityBox, List<AxisAlignedBB> collidingBoxes, Entity entityIn, boolean p_185477_7_) {
      switch((EnumFacing)state.getValue(FACING)) {
      case NORTH:
         addCollisionBoxToList(pos, entityBox, collidingBoxes, HANDRAIL1_X);
         addCollisionBoxToList(pos, entityBox, collidingBoxes, HANDRAIL2_X);
         addCollisionBoxToList(pos, entityBox, collidingBoxes, PART_BASE_Z);
         break;
      case SOUTH:
         addCollisionBoxToList(pos, entityBox, collidingBoxes, HANDRAIL1_X);
         addCollisionBoxToList(pos, entityBox, collidingBoxes, HANDRAIL2_X);
         addCollisionBoxToList(pos, entityBox, collidingBoxes, PART_BASE_Z);
         break;
      case EAST:
         addCollisionBoxToList(pos, entityBox, collidingBoxes, HANDRAIL1_Z);
         addCollisionBoxToList(pos, entityBox, collidingBoxes, HANDRAIL2_Z);
         addCollisionBoxToList(pos, entityBox, collidingBoxes, PART_BASE_X);
         break;
      case WEST:
         addCollisionBoxToList(pos, entityBox, collidingBoxes, HANDRAIL1_Z);
         addCollisionBoxToList(pos, entityBox, collidingBoxes, HANDRAIL2_Z);
         addCollisionBoxToList(pos, entityBox, collidingBoxes, PART_BASE_X);
      }

   }

   public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
      switch((EnumFacing)state.getValue(FACING)) {
      case NORTH:
         return PART_BASE_Z;
      case SOUTH:
      default:
         return PART_BASE_Z;
      case EAST:
         return PART_BASE_X;
      case WEST:
         return PART_BASE_X;
      }
   }
}
