package knightminer.inspirations.utility.block;

import java.util.ArrayList;
import java.util.List;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import knightminer.inspirations.Inspirations;
import knightminer.inspirations.common.Config;
import knightminer.inspirations.library.Util;
import knightminer.inspirations.utility.InspirationsUtility;
import knightminer.inspirations.utility.tileentity.TilePipe;
import net.minecraft.block.Block;
import net.minecraft.block.BlockDirectional;
import net.minecraft.block.BlockDropper;
import net.minecraft.block.BlockHopper;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.Mirror;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import slimeknights.mantle.block.BlockInventory;

public class BlockPipe extends BlockInventory {
   public static final PropertyDirection FACING = BlockDirectional.FACING;
   public static final PropertyBool NORTH = PropertyBool.create("north");
   public static final PropertyBool EAST = PropertyBool.create("east");
   public static final PropertyBool SOUTH = PropertyBool.create("south");
   public static final PropertyBool WEST = PropertyBool.create("west");
   public static final PropertyBool UP = PropertyBool.create("up");
   public static final PropertyBool DOWN = PropertyBool.create("down");
   public static final PropertyBool HOPPER = PropertyBool.create("hopper");
   private static final AxisAlignedBB[] FULL_BOUNDS = new AxisAlignedBB[64];
   private static final AxisAlignedBB BOUNDS_CENTER;
   private static final AxisAlignedBB BOUNDS_DOWN;
   private static final AxisAlignedBB BOUNDS_UP;
   private static final AxisAlignedBB BOUNDS_NORTH;
   private static final AxisAlignedBB BOUNDS_SOUTH;
   private static final AxisAlignedBB BOUNDS_WEST;
   private static final AxisAlignedBB BOUNDS_EAST;
   private static final AxisAlignedBB BOUNDS_DOWN_CONNECT;
   private static final AxisAlignedBB BOUNDS_UP_CONNECT;
   private static final AxisAlignedBB BOUNDS_NORTH_CONNECT;
   private static final AxisAlignedBB BOUNDS_SOUTH_CONNECT;
   private static final AxisAlignedBB BOUNDS_WEST_CONNECT;
   private static final AxisAlignedBB BOUNDS_EAST_CONNECT;
   private static final AxisAlignedBB[] BOUNDS_SIDES;

   public BlockPipe() {
      super(Material.IRON);
      this.setHardness(3.0F);
      this.setResistance(8.0F);
      this.setSoundType(SoundType.METAL);
      this.setDefaultState(this.blockState.getBaseState().withProperty(FACING, EnumFacing.NORTH).withProperty(NORTH, Boolean.valueOf(false)).withProperty(EAST, Boolean.valueOf(false)).withProperty(SOUTH, Boolean.valueOf(false)).withProperty(WEST, Boolean.valueOf(false)).withProperty(UP, Boolean.valueOf(false)).withProperty(DOWN, Boolean.valueOf(false)).withProperty(HOPPER, Boolean.valueOf(false)));
      this.setCreativeTab(CreativeTabs.REDSTONE);
   }

   protected BlockStateContainer createBlockState() {
      return new BlockStateContainer(this, new IProperty[]{FACING, NORTH, EAST, SOUTH, WEST, UP, DOWN, HOPPER});
   }

   /** @deprecated */
   @Deprecated
   public IBlockState getStateFromMeta(int meta) {
      return this.getDefaultState().withProperty(FACING, EnumFacing.getFront(meta & 7));
   }

   public int getMetaFromState(IBlockState state) {
      return ((EnumFacing)state.getValue(FACING)).getIndex();
   }

   /** @deprecated */
   @Deprecated
   public IBlockState withRotation(IBlockState state, Rotation rot) {
      return state.withProperty(FACING, rot.rotate((EnumFacing)state.getValue(FACING)));
   }

   /** @deprecated */
   @Deprecated
   public IBlockState withMirror(IBlockState state, Mirror mirror) {
      return state.withRotation(mirror.toRotation((EnumFacing)state.getValue(FACING)));
   }

   public int damageDropped(IBlockState state) {
      return 0;
   }

   public IBlockState getStateForPlacement(World worldIn, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer) {
      facing = facing.getOpposite();
      if (!Config.pipeUpwards && facing == EnumFacing.UP) {
         facing = EnumFacing.DOWN;
      }

      return this.getDefaultState().withProperty(FACING, facing);
   }

   public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing side, float clickX, float clickY, float clickZ) {
      Item item = player.getHeldItem(hand).getItem();
      return item != Item.getItemFromBlock(InspirationsUtility.pipe) && !(Block.getBlockFromItem(item) instanceof BlockHopper) ? super.onBlockActivated(world, pos, state, player, hand, side, clickX, clickY, clickZ) : false;
   }

   /** @deprecated */
   @Deprecated
   public IBlockState getActualState(IBlockState state, IBlockAccess world, BlockPos pos) {
      EnumFacing facing = (EnumFacing)state.getValue(FACING);
      IBlockState offsetState = world.getBlockState(pos.offset(facing));
      return state.withProperty(HOPPER, Boolean.valueOf(offsetState.getBlock() instanceof BlockHopper && offsetState.getValue(BlockHopper.FACING) != facing.getOpposite())).withProperty(UP, Boolean.valueOf(canConnectTo(world, pos, facing, EnumFacing.UP))).withProperty(DOWN, Boolean.valueOf(canConnectTo(world, pos, facing, EnumFacing.DOWN))).withProperty(NORTH, Boolean.valueOf(canConnectTo(world, pos, facing, EnumFacing.NORTH))).withProperty(EAST, Boolean.valueOf(canConnectTo(world, pos, facing, EnumFacing.EAST))).withProperty(SOUTH, Boolean.valueOf(canConnectTo(world, pos, facing, EnumFacing.SOUTH))).withProperty(WEST, Boolean.valueOf(canConnectTo(world, pos, facing, EnumFacing.WEST)));
   }

   private static boolean canConnectTo(IBlockAccess world, BlockPos pos, EnumFacing facing, EnumFacing side) {
      if (facing == side) {
         return false;
      } else {
         IBlockState state = world.getBlockState(pos.offset(side));
         Block block = state.getBlock();
         EnumFacing opposite = side.getOpposite();
         if ((block instanceof BlockPipe || block instanceof BlockDropper) && state.getValue(FACING) == opposite) {
            return true;
         } else {
            return side != EnumFacing.DOWN && block instanceof BlockHopper && state.getValue(BlockHopper.FACING) == opposite;
         }
      }
   }

   public TileEntity createNewTileEntity(World worldIn, int meta) {
      return new TilePipe();
   }

   protected boolean openGui(EntityPlayer player, World world, BlockPos pos) {
      player.openGui(Inspirations.instance, 0, world, pos.getX(), pos.getY(), pos.getZ());
      return true;
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

   /** @deprecated */
   @Deprecated
   @Nonnull
   public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
      state = state.getActualState(source, pos);
      int index = 1 << ((EnumFacing)state.getValue(FACING)).getIndex() | (((Boolean)state.getValue(EAST)).booleanValue() ? 32 : 0) | (((Boolean)state.getValue(WEST)).booleanValue() ? 16 : 0) | (((Boolean)state.getValue(SOUTH)).booleanValue() ? 8 : 0) | (((Boolean)state.getValue(NORTH)).booleanValue() ? 4 : 0) | (((Boolean)state.getValue(UP)).booleanValue() ? 2 : 0) | (((Boolean)state.getValue(DOWN)).booleanValue() ? 1 : 0);
      return FULL_BOUNDS[index];
   }

   /** @deprecated */
   @Deprecated
   public void addCollisionBoxToList(IBlockState state, World world, BlockPos pos, AxisAlignedBB entityBox, List<AxisAlignedBB> collidingBoxes, @Nullable Entity entity, boolean p_185477_7_) {
      state = state.getActualState(world, pos);
      addCollisionBoxToList(pos, entityBox, collidingBoxes, BOUNDS_CENTER);
      addCollisionBoxToList(pos, entityBox, collidingBoxes, BOUNDS_SIDES[((EnumFacing)state.getValue(FACING)).getIndex()]);
      if (((Boolean)state.getValue(UP)).booleanValue()) {
         addCollisionBoxToList(pos, entityBox, collidingBoxes, BOUNDS_UP);
      }

      if (((Boolean)state.getValue(DOWN)).booleanValue()) {
         addCollisionBoxToList(pos, entityBox, collidingBoxes, BOUNDS_DOWN);
      }

      if (((Boolean)state.getValue(NORTH)).booleanValue()) {
         addCollisionBoxToList(pos, entityBox, collidingBoxes, BOUNDS_NORTH);
      }

      if (((Boolean)state.getValue(SOUTH)).booleanValue()) {
         addCollisionBoxToList(pos, entityBox, collidingBoxes, BOUNDS_SOUTH);
      }

      if (((Boolean)state.getValue(WEST)).booleanValue()) {
         addCollisionBoxToList(pos, entityBox, collidingBoxes, BOUNDS_WEST);
      }

      if (((Boolean)state.getValue(EAST)).booleanValue()) {
         addCollisionBoxToList(pos, entityBox, collidingBoxes, BOUNDS_EAST);
      }

   }

   /** @deprecated */
   @Deprecated
   public RayTraceResult collisionRayTrace(IBlockState state, @Nonnull World world, @Nonnull BlockPos pos, @Nonnull Vec3d start, @Nonnull Vec3d end) {
      state = state.getActualState(world, pos);
      List<RayTraceResult> list = new ArrayList(8);
      list.add(this.rayTrace(pos, start, end, BOUNDS_CENTER));
      list.add(this.rayTrace(pos, start, end, BOUNDS_SIDES[((EnumFacing)state.getValue(FACING)).getIndex()]));
      if (((Boolean)state.getValue(UP)).booleanValue()) {
         list.add(this.rayTrace(pos, start, end, BOUNDS_UP));
         list.add(this.rayTrace(pos, start, end, BOUNDS_UP_CONNECT));
      }

      if (((Boolean)state.getValue(DOWN)).booleanValue()) {
         list.add(this.rayTrace(pos, start, end, BOUNDS_DOWN));
         list.add(this.rayTrace(pos, start, end, BOUNDS_DOWN_CONNECT));
      }

      if (((Boolean)state.getValue(NORTH)).booleanValue()) {
         list.add(this.rayTrace(pos, start, end, BOUNDS_NORTH));
         list.add(this.rayTrace(pos, start, end, BOUNDS_NORTH_CONNECT));
      }

      if (((Boolean)state.getValue(SOUTH)).booleanValue()) {
         list.add(this.rayTrace(pos, start, end, BOUNDS_SOUTH));
         list.add(this.rayTrace(pos, start, end, BOUNDS_SOUTH_CONNECT));
      }

      if (((Boolean)state.getValue(WEST)).booleanValue()) {
         list.add(this.rayTrace(pos, start, end, BOUNDS_WEST));
         list.add(this.rayTrace(pos, start, end, BOUNDS_WEST_CONNECT));
      }

      if (((Boolean)state.getValue(EAST)).booleanValue()) {
         list.add(this.rayTrace(pos, start, end, BOUNDS_EAST));
         list.add(this.rayTrace(pos, start, end, BOUNDS_EAST_CONNECT));
      }

      return Util.closestResult(list, end);
   }

   static {
      for(int i = 0; i < 64; ++i) {
         FULL_BOUNDS[i] = new AxisAlignedBB((i & 16) > 0 ? 0.0D : 0.34375D, (i & 1) > 0 ? 0.0D : 0.21875D, (i & 4) > 0 ? 0.0D : 0.34375D, (i & 32) > 0 ? 1.0D : 0.65625D, (i & 2) > 0 ? 1.0D : 0.53125D, (i & 8) > 0 ? 1.0D : 0.65625D);
      }

      BOUNDS_CENTER = new AxisAlignedBB(0.375D, 0.25D, 0.375D, 0.625D, 0.5D, 0.625D);
      BOUNDS_DOWN = new AxisAlignedBB(0.375D, 0.0D, 0.375D, 0.625D, 0.25D, 0.625D);
      BOUNDS_UP = new AxisAlignedBB(0.375D, 0.5D, 0.375D, 0.625D, 1.0D, 0.625D);
      BOUNDS_NORTH = new AxisAlignedBB(0.375D, 0.25D, 0.0D, 0.625D, 0.5D, 0.375D);
      BOUNDS_SOUTH = new AxisAlignedBB(0.375D, 0.25D, 0.625D, 0.625D, 0.5D, 1.0D);
      BOUNDS_WEST = new AxisAlignedBB(0.0D, 0.25D, 0.375D, 0.375D, 0.5D, 0.625D);
      BOUNDS_EAST = new AxisAlignedBB(0.625D, 0.25D, 0.375D, 1.0D, 0.5D, 0.625D);
      BOUNDS_DOWN_CONNECT = new AxisAlignedBB(0.34375D, 0.0D, 0.34375D, 0.65625D, 0.0625D, 0.65625D);
      BOUNDS_UP_CONNECT = new AxisAlignedBB(0.34375D, 0.9375D, 0.34375D, 0.59375D, 1.0D, 0.65625D);
      BOUNDS_NORTH_CONNECT = new AxisAlignedBB(0.34375D, 0.21875D, 0.0D, 0.65625D, 0.53125D, 0.0625D);
      BOUNDS_SOUTH_CONNECT = new AxisAlignedBB(0.34375D, 0.21875D, 0.9375D, 0.65625D, 0.53125D, 1.0D);
      BOUNDS_WEST_CONNECT = new AxisAlignedBB(0.0D, 0.21875D, 0.34375D, 0.0625D, 0.53125D, 0.65625D);
      BOUNDS_EAST_CONNECT = new AxisAlignedBB(0.9375D, 0.21875D, 0.34375D, 1.0D, 0.53125D, 0.65625D);
      BOUNDS_SIDES = new AxisAlignedBB[]{BOUNDS_DOWN, BOUNDS_UP, BOUNDS_NORTH, BOUNDS_SOUTH, BOUNDS_WEST, BOUNDS_EAST};
   }
}
