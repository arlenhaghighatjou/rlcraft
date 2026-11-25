package com.dhanantry.scapeandrunparasites.block;

import com.dhanantry.scapeandrunparasites.SRPMain;
import com.dhanantry.scapeandrunparasites.init.SRPBlocks;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemLead;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.Mirror;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class BlockParasiteThin extends Block {
   public static final PropertyBool NORTH = PropertyBool.create("north");
   public static final PropertyBool EAST = PropertyBool.create("east");
   public static final PropertyBool SOUTH = PropertyBool.create("south");
   public static final PropertyBool WEST = PropertyBool.create("west");
   public static final PropertyBool UP = PropertyBool.create("up");
   public static final PropertyBool DOWN = PropertyBool.create("down");
   protected static final AxisAlignedBB[] BOUNDING_BOXES = new AxisAlignedBB[]{new AxisAlignedBB(0.375D, 0.0D, 0.375D, 0.625D, 1.0D, 0.625D), new AxisAlignedBB(0.375D, 0.0D, 0.375D, 0.625D, 1.0D, 1.0D), new AxisAlignedBB(0.0D, 0.0D, 0.375D, 0.625D, 1.0D, 0.625D), new AxisAlignedBB(0.0D, 0.0D, 0.375D, 0.625D, 1.0D, 1.0D), new AxisAlignedBB(0.375D, 0.0D, 0.0D, 0.625D, 1.0D, 0.625D), new AxisAlignedBB(0.375D, 0.0D, 0.0D, 0.625D, 1.0D, 1.0D), new AxisAlignedBB(0.0D, 0.0D, 0.0D, 0.625D, 1.0D, 0.625D), new AxisAlignedBB(0.0D, 0.0D, 0.0D, 0.625D, 1.0D, 1.0D), new AxisAlignedBB(0.375D, 0.0D, 0.375D, 1.0D, 1.0D, 0.625D), new AxisAlignedBB(0.375D, 0.0D, 0.375D, 1.0D, 1.0D, 1.0D), new AxisAlignedBB(0.0D, 0.0D, 0.375D, 1.0D, 1.0D, 0.625D), new AxisAlignedBB(0.0D, 0.0D, 0.375D, 1.0D, 1.0D, 1.0D), new AxisAlignedBB(0.375D, 0.0D, 0.0D, 1.0D, 1.0D, 0.625D), new AxisAlignedBB(0.375D, 0.0D, 0.0D, 1.0D, 1.0D, 1.0D), new AxisAlignedBB(0.0D, 0.0D, 0.0D, 1.0D, 1.0D, 0.625D), new AxisAlignedBB(0.0D, 0.0D, 0.0D, 1.0D, 1.0D, 1.0D)};
   public static final AxisAlignedBB PILLAR_AABB = new AxisAlignedBB(0.375D, 0.5D, 0.375D, 0.625D, 1.5D, 0.625D);
   public static final AxisAlignedBB SOUTH_AABB = new AxisAlignedBB(0.375D, 0.0D, 0.625D, 0.625D, 1.5D, 1.0D);
   public static final AxisAlignedBB WEST_AABB = new AxisAlignedBB(0.0D, 0.0D, 0.375D, 0.375D, 1.5D, 0.625D);
   public static final AxisAlignedBB NORTH_AABB = new AxisAlignedBB(0.375D, 0.0D, 0.0D, 0.625D, 1.5D, 0.375D);
   public static final AxisAlignedBB EAST_AABB = new AxisAlignedBB(0.625D, 0.0D, 0.375D, 1.0D, 1.5D, 0.625D);

   public BlockParasiteThin(String name, float hardness) {
      super(Material.WOOD);
      this.setHarvestLevel("axe", 0);
      this.setRegistryName(name);
      this.setUnlocalizedName("srparasites." + name);
      this.setCreativeTab(SRPMain.SRP_CREATIVETAB);
      this.setHardness(hardness);
      this.setSoundType(SoundType.WOOD);
   }

   public void addCollisionBoxToList(IBlockState state, World worldIn, BlockPos pos, AxisAlignedBB entityBox, List<AxisAlignedBB> collidingBoxes, @Nullable Entity entityIn, boolean isActualState) {
      if (!isActualState) {
         state = state.getActualState(worldIn, pos);
      }

      addCollisionBoxToList(pos, entityBox, collidingBoxes, PILLAR_AABB);
      if (((Boolean)state.getValue(NORTH)).booleanValue()) {
         addCollisionBoxToList(pos, entityBox, collidingBoxes, NORTH_AABB);
      }

      if (((Boolean)state.getValue(EAST)).booleanValue()) {
         addCollisionBoxToList(pos, entityBox, collidingBoxes, EAST_AABB);
      }

      if (((Boolean)state.getValue(SOUTH)).booleanValue()) {
         addCollisionBoxToList(pos, entityBox, collidingBoxes, SOUTH_AABB);
      }

      if (((Boolean)state.getValue(WEST)).booleanValue()) {
         addCollisionBoxToList(pos, entityBox, collidingBoxes, WEST_AABB);
      }

   }

   public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
      state = this.getActualState(state, source, pos);
      return BOUNDING_BOXES[getBoundingBoxIdx(state)];
   }

   private static int getBoundingBoxIdx(IBlockState state) {
      int i = 0;
      if (((Boolean)state.getValue(NORTH)).booleanValue()) {
         i |= 1 << EnumFacing.NORTH.getHorizontalIndex();
      }

      if (((Boolean)state.getValue(EAST)).booleanValue()) {
         i |= 1 << EnumFacing.EAST.getHorizontalIndex();
      }

      if (((Boolean)state.getValue(SOUTH)).booleanValue()) {
         i |= 1 << EnumFacing.SOUTH.getHorizontalIndex();
      }

      if (((Boolean)state.getValue(WEST)).booleanValue()) {
         i |= 1 << EnumFacing.WEST.getHorizontalIndex();
      }

      return i;
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

   public boolean canConnectTo(IBlockAccess worldIn, BlockPos pos, EnumFacing facing) {
      IBlockState iblockstate = worldIn.getBlockState(pos);
      BlockFaceShape blockfaceshape = iblockstate.getBlockFaceShape(worldIn, pos, facing);
      Block block = iblockstate.getBlock();
      boolean flag = blockfaceshape == BlockFaceShape.MIDDLE_POLE && iblockstate.getMaterial() == this.blockMaterial;
      boolean flag2 = block == SRPBlocks.ParasiteThin;
      boolean flag3 = blockfaceshape == BlockFaceShape.SOLID && (facing == EnumFacing.UP || iblockstate.isFullCube());
      return !isExcepBlockForAttachWithPiston(block) && flag3 || flag || flag2;
   }

   protected static boolean isExcepBlockForAttachWithPiston(Block p_194142_0_) {
      return Block.isExceptBlockForAttachWithPiston(p_194142_0_) || p_194142_0_ == Blocks.BARRIER || p_194142_0_ == Blocks.MELON_BLOCK || p_194142_0_ == Blocks.PUMPKIN || p_194142_0_ == Blocks.LIT_PUMPKIN;
   }

   @SideOnly(Side.CLIENT)
   public boolean shouldSideBeRendered(IBlockState blockState, IBlockAccess blockAccess, BlockPos pos, EnumFacing side) {
      return true;
   }

   public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
      if (!worldIn.isRemote) {
         return ItemLead.attachToFence(playerIn, worldIn, pos);
      } else {
         ItemStack itemstack = playerIn.getHeldItem(hand);
         return itemstack.getItem() == Items.LEAD || itemstack.isEmpty();
      }
   }

   public int getMetaFromState(IBlockState state) {
      return 0;
   }

   public IBlockState getActualState(IBlockState state, IBlockAccess worldIn, BlockPos pos) {
      return state.withProperty(NORTH, Boolean.valueOf(this.canFenceConnectTo(worldIn, pos, EnumFacing.NORTH))).withProperty(EAST, Boolean.valueOf(this.canFenceConnectTo(worldIn, pos, EnumFacing.EAST))).withProperty(SOUTH, Boolean.valueOf(this.canFenceConnectTo(worldIn, pos, EnumFacing.SOUTH))).withProperty(WEST, Boolean.valueOf(this.canFenceConnectTo(worldIn, pos, EnumFacing.WEST))).withProperty(UP, Boolean.valueOf(this.canFenceConnectTo(worldIn, pos, EnumFacing.UP))).withProperty(DOWN, Boolean.valueOf(this.canFenceConnectTo(worldIn, pos, EnumFacing.DOWN)));
   }

   public IBlockState withRotation(IBlockState state, Rotation rot) {
      switch(rot) {
      case CLOCKWISE_180:
         return state.withProperty(NORTH, state.getValue(SOUTH)).withProperty(EAST, state.getValue(WEST)).withProperty(SOUTH, state.getValue(NORTH)).withProperty(WEST, state.getValue(EAST)).withProperty(UP, state.getValue(UP)).withProperty(DOWN, state.getValue(DOWN));
      case COUNTERCLOCKWISE_90:
         return state.withProperty(NORTH, state.getValue(EAST)).withProperty(EAST, state.getValue(SOUTH)).withProperty(SOUTH, state.getValue(WEST)).withProperty(WEST, state.getValue(NORTH)).withProperty(UP, state.getValue(UP)).withProperty(DOWN, state.getValue(DOWN));
      case CLOCKWISE_90:
         return state.withProperty(NORTH, state.getValue(WEST)).withProperty(EAST, state.getValue(NORTH)).withProperty(SOUTH, state.getValue(EAST)).withProperty(WEST, state.getValue(SOUTH)).withProperty(UP, state.getValue(UP)).withProperty(DOWN, state.getValue(DOWN));
      default:
         return state;
      }
   }

   public IBlockState withMirror(IBlockState state, Mirror mirrorIn) {
      switch(mirrorIn) {
      case LEFT_RIGHT:
         return state.withProperty(NORTH, state.getValue(SOUTH)).withProperty(SOUTH, state.getValue(NORTH));
      case FRONT_BACK:
         return state.withProperty(EAST, state.getValue(WEST)).withProperty(WEST, state.getValue(EAST));
      default:
         return super.withMirror(state, mirrorIn);
      }
   }

   protected BlockStateContainer createBlockState() {
      return new BlockStateContainer(this, new IProperty[]{NORTH, EAST, WEST, SOUTH, UP, DOWN});
   }

   public boolean canBeConnectedTo(IBlockAccess world, BlockPos pos, EnumFacing facing) {
      return this.canConnectTo(world, pos.offset(facing), facing.getOpposite());
   }

   private boolean canFenceConnectTo(IBlockAccess world, BlockPos pos, EnumFacing facing) {
      BlockPos other = pos.offset(facing);
      Block block = world.getBlockState(other).getBlock();
      return this.canConnectTo(world, other, facing.getOpposite());
   }

   public BlockFaceShape getBlockFaceShape(IBlockAccess worldIn, IBlockState state, BlockPos pos, EnumFacing face) {
      return face != EnumFacing.UP && face != EnumFacing.DOWN ? BlockFaceShape.MIDDLE_POLE : BlockFaceShape.CENTER;
   }
}
