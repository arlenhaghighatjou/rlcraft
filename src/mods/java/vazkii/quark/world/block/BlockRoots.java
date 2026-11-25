package vazkii.quark.world.block;

import com.google.common.collect.Lists;
import java.util.List;
import java.util.Random;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.BlockVine;
import net.minecraft.block.IGrowable;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.stats.StatList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.Mirror;
import net.minecraft.util.NonNullList;
import net.minecraft.util.Rotation;
import net.minecraft.util.EnumFacing.Plane;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.IShearable;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import vazkii.arl.block.BlockMod;
import vazkii.quark.base.block.BlockQuarkPane;
import vazkii.quark.base.block.IQuarkBlock;
import vazkii.quark.world.feature.CaveRoots;

public class BlockRoots extends BlockMod implements IQuarkBlock, IShearable, IGrowable {
   public static final PropertyBool UP = BlockVine.UP;
   public static final PropertyBool NORTH = BlockVine.NORTH;
   public static final PropertyBool EAST = BlockVine.EAST;
   public static final PropertyBool SOUTH = BlockVine.SOUTH;
   public static final PropertyBool WEST = BlockVine.WEST;
   public static final PropertyBool[] ALL_FACES = BlockVine.ALL_FACES;
   protected static final AxisAlignedBB UP_AABB = new AxisAlignedBB(0.0D, 0.9375D, 0.0D, 1.0D, 1.0D, 1.0D);
   protected static final AxisAlignedBB WEST_AABB = new AxisAlignedBB(0.0D, 0.0D, 0.0D, 0.0625D, 1.0D, 1.0D);
   protected static final AxisAlignedBB EAST_AABB = new AxisAlignedBB(0.9375D, 0.0D, 0.0D, 1.0D, 1.0D, 1.0D);
   protected static final AxisAlignedBB NORTH_AABB = new AxisAlignedBB(0.0D, 0.0D, 0.0D, 1.0D, 1.0D, 0.0625D);
   protected static final AxisAlignedBB SOUTH_AABB = new AxisAlignedBB(0.0D, 0.0D, 0.9375D, 1.0D, 1.0D, 1.0D);
   private final Random rng;

   public BlockRoots(String name) {
      super(name, Material.VINE, new String[0]);
      this.setDefaultState(this.blockState.getBaseState().withProperty(UP, Boolean.valueOf(false)).withProperty(NORTH, Boolean.valueOf(false)).withProperty(EAST, Boolean.valueOf(false)).withProperty(SOUTH, Boolean.valueOf(false)).withProperty(WEST, Boolean.valueOf(false)));
      this.setTickRandomly(true);
      this.setCreativeTab(CreativeTabs.DECORATIONS);
      this.rng = new Random();
   }

   public BlockRoots() {
      this("roots");
   }

   public void updateTick(World worldIn, BlockPos pos, IBlockState state, Random rand) {
      if (!worldIn.isRemote && worldIn.rand.nextInt(2) == 0) {
         this.grow(worldIn, rand, pos, state);
      }

   }

   public static void growMany(World world, BlockPos pos, IBlockState state, float stopChance, boolean avoidCascade) {
      BlockPos next = pos;

      while(true) {
         next = growAndReturnLastPos(world, next, state, avoidCascade);
         if (next == null || world.rand.nextFloat() < stopChance) {
            break;
         }
      }

   }

   public static BlockPos growAndReturnLastPos(World world, BlockPos pos, IBlockState state, boolean avoidCascade) {
      BlockPos down = pos.down();
      EnumFacing[] var5 = EnumFacing.HORIZONTALS;
      int var6 = var5.length;
      int var7 = 0;

      while(true) {
         if (var7 < var6) {
            EnumFacing facing = var5[var7];
            PropertyBool prop = getPropertyFor(facing);
            if (!((Boolean)state.getValue(prop)).booleanValue()) {
               ++var7;
               continue;
            }

            BlockPos ret = growInFacing(world, down, facing);
            if (ret != null && (!avoidCascade || world.isBlockLoaded(ret))) {
               IBlockState setState = nextState(world.rand).withProperty(prop, Boolean.valueOf(true));
               world.setBlockState(ret, setState);
               return ret;
            }
         }

         return null;
      }
   }

   public static BlockPos growInFacing(World world, BlockPos pos, EnumFacing facing) {
      if (!world.isAirBlock(pos)) {
         return null;
      } else {
         BlockPos check = pos.offset(facing);
         if (isAcceptableNeighbor(world, check, facing.getOpposite())) {
            return pos;
         } else {
            pos = check;
            if (!world.isAirBlock(check)) {
               return null;
            } else {
               check = check.offset(facing);
               return isAcceptableNeighbor(world, check, facing.getOpposite()) ? pos : null;
            }
         }
      }
   }

   public boolean canGrow(@Nonnull World worldIn, @Nonnull BlockPos pos, @Nonnull IBlockState state, boolean isClient) {
      return worldIn.getLight(pos) < 7;
   }

   public boolean canUseBonemeal(@Nonnull World worldIn, @Nonnull Random rand, @Nonnull BlockPos pos, @Nonnull IBlockState state) {
      return (double)rand.nextFloat() < 0.4D;
   }

   public void grow(@Nonnull World worldIn, @Nonnull Random rand, @Nonnull BlockPos pos, @Nonnull IBlockState state) {
      growAndReturnLastPos(worldIn, pos, state, false);
   }

   private static IBlockState nextState(Random rand) {
      if (CaveRoots.enableFlowers && rand.nextFloat() <= CaveRoots.flowerChance) {
         switch(rand.nextInt(3)) {
         case 0:
            return CaveRoots.roots_blue_flower.getDefaultState();
         case 1:
            return CaveRoots.roots_black_flower.getDefaultState();
         default:
            return CaveRoots.roots_white_flower.getDefaultState();
         }
      } else {
         return CaveRoots.roots.getDefaultState();
      }
   }

   protected ItemStack getRootDrop() {
      return new ItemStack(CaveRoots.root);
   }

   protected float getDropChance() {
      return CaveRoots.rootDropChance;
   }

   @Nullable
   public AxisAlignedBB getCollisionBoundingBox(IBlockState blockState, @Nonnull IBlockAccess worldIn, @Nonnull BlockPos pos) {
      return NULL_AABB;
   }

   @Nonnull
   public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
      state = state.getActualState(source, pos);
      int i = 0;
      AxisAlignedBB axisalignedbb = FULL_BLOCK_AABB;
      if (((Boolean)state.getValue(NORTH)).booleanValue()) {
         axisalignedbb = NORTH_AABB;
         ++i;
      }

      if (((Boolean)state.getValue(EAST)).booleanValue()) {
         axisalignedbb = EAST_AABB;
         ++i;
      }

      if (((Boolean)state.getValue(SOUTH)).booleanValue()) {
         axisalignedbb = SOUTH_AABB;
         ++i;
      }

      if (((Boolean)state.getValue(WEST)).booleanValue()) {
         axisalignedbb = WEST_AABB;
         ++i;
      }

      return i == 1 ? axisalignedbb : FULL_BLOCK_AABB;
   }

   @Nonnull
   public IBlockState getActualState(@Nonnull IBlockState state, IBlockAccess worldIn, BlockPos pos) {
      BlockPos blockpos = pos.up();
      return state.withProperty(UP, Boolean.valueOf(worldIn.getBlockState(blockpos).getBlockFaceShape(worldIn, blockpos, EnumFacing.DOWN) == BlockFaceShape.SOLID));
   }

   public boolean isOpaqueCube(IBlockState state) {
      return false;
   }

   public boolean isFullCube(IBlockState state) {
      return false;
   }

   public boolean isReplaceable(IBlockAccess worldIn, @Nonnull BlockPos pos) {
      return true;
   }

   public boolean canPlaceBlockOnSide(@Nonnull World worldIn, @Nonnull BlockPos pos, EnumFacing side) {
      return side != EnumFacing.DOWN && side != EnumFacing.UP && this.canAttachTo(worldIn, pos, side);
   }

   public boolean canAttachTo(World world, BlockPos pos, EnumFacing side) {
      Block block = world.getBlockState(pos.up()).getBlock();
      return isAcceptableNeighbor(world, pos.offset(side.getOpposite()), side) && (block == Blocks.AIR || block instanceof BlockRoots || isAcceptableNeighbor(world, pos.up(), EnumFacing.UP));
   }

   private static boolean isAcceptableNeighbor(World world, BlockPos pos, EnumFacing side) {
      IBlockState iblockstate = world.getBlockState(pos);
      return iblockstate.getBlockFaceShape(world, pos, side) == BlockFaceShape.SOLID && iblockstate.getMaterial() == Material.ROCK;
   }

   private boolean recheckGrownSides(World worldIn, BlockPos pos, IBlockState state) {
      IBlockState originalState = state;

      for(EnumFacing enumfacing : Plane.HORIZONTAL) {
         PropertyBool propertybool = getPropertyFor(enumfacing);
         if (((Boolean)state.getValue(propertybool)).booleanValue() && !this.canAttachTo(worldIn, pos, enumfacing.getOpposite())) {
            IBlockState stateAt = worldIn.getBlockState(pos.up());
            if (stateAt.getBlock() != this || !((Boolean)stateAt.getValue(propertybool)).booleanValue()) {
               state = state.withProperty(propertybool, Boolean.FALSE);
            }
         }
      }

      if (getNumGrownFaces(state) == 0) {
         return false;
      } else {
         if (originalState != state) {
            worldIn.setBlockState(pos, state, 2);
         }

         return true;
      }
   }

   public void neighborChanged(IBlockState state, World worldIn, BlockPos pos, Block blockIn, BlockPos fromPos) {
      if (!worldIn.isRemote && !this.recheckGrownSides(worldIn, pos, state)) {
         this.dropBlockAsItem(worldIn, pos, state, 0);
         worldIn.setBlockToAir(pos);
      }

   }

   @Nonnull
   public IBlockState getStateForPlacement(World worldIn, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer) {
      IBlockState iblockstate = this.getDefaultState().withProperty(UP, Boolean.valueOf(false)).withProperty(NORTH, Boolean.valueOf(false)).withProperty(EAST, Boolean.valueOf(false)).withProperty(SOUTH, Boolean.valueOf(false)).withProperty(WEST, Boolean.valueOf(false));
      return facing.getAxis().isHorizontal() ? iblockstate.withProperty(getPropertyFor(facing.getOpposite()), Boolean.valueOf(true)) : iblockstate;
   }

   @Nonnull
   public List<ItemStack> getDrops(@Nonnull IBlockAccess world, @Nonnull BlockPos pos, @Nonnull IBlockState state, int fortune) {
      return this.rng.nextFloat() < this.getDropChance() ? NonNullList.withSize(1, this.getRootDrop()) : NonNullList.withSize(0, ItemStack.EMPTY);
   }

   public void harvestBlock(@Nonnull World worldIn, EntityPlayer player, @Nonnull BlockPos pos, @Nonnull IBlockState state, @Nullable TileEntity te, ItemStack stack) {
      if (!worldIn.isRemote && stack.getItem() == Items.SHEARS) {
         player.addStat(StatList.getBlockStats(this));
         spawnAsEntity(worldIn, pos, new ItemStack(this, 1, 0));
      } else {
         super.harvestBlock(worldIn, player, pos, state, te, stack);
      }

   }

   @Nonnull
   public IBlockState getStateFromMeta(int meta) {
      return this.getDefaultState().withProperty(SOUTH, Boolean.valueOf((meta & 1) != 0)).withProperty(WEST, Boolean.valueOf((meta & 2) != 0)).withProperty(NORTH, Boolean.valueOf((meta & 4) != 0)).withProperty(EAST, Boolean.valueOf((meta & 8) != 0));
   }

   @Nonnull
   @SideOnly(Side.CLIENT)
   public BlockRenderLayer getBlockLayer() {
      return BlockRenderLayer.CUTOUT;
   }

   public int getMetaFromState(IBlockState state) {
      int i = 0;
      if (((Boolean)state.getValue(SOUTH)).booleanValue()) {
         i |= 1;
      }

      if (((Boolean)state.getValue(WEST)).booleanValue()) {
         i |= 2;
      }

      if (((Boolean)state.getValue(NORTH)).booleanValue()) {
         i |= 4;
      }

      if (((Boolean)state.getValue(EAST)).booleanValue()) {
         i |= 8;
      }

      return i;
   }

   @Nonnull
   protected BlockStateContainer createBlockState() {
      return new BlockStateContainer(this, new IProperty[]{UP, NORTH, EAST, SOUTH, WEST});
   }

   @Nonnull
   public IBlockState withRotation(@Nonnull IBlockState state, Rotation rot) {
      return BlockQuarkPane.rotate(state, rot, NORTH, SOUTH, EAST, WEST);
   }

   @Nonnull
   public IBlockState withMirror(@Nonnull IBlockState state, Mirror mirrorIn) {
      return BlockQuarkPane.mirror(state, mirrorIn, NORTH, SOUTH, EAST, WEST);
   }

   public static PropertyBool getPropertyFor(EnumFacing side) {
      switch(side) {
      case UP:
         return UP;
      case NORTH:
         return NORTH;
      case SOUTH:
         return SOUTH;
      case WEST:
         return WEST;
      case EAST:
         return EAST;
      default:
         throw new IllegalArgumentException(side + " is an invalid choice");
      }
   }

   public static int getNumGrownFaces(IBlockState state) {
      int i = 0;

      for(PropertyBool propertybool : ALL_FACES) {
         if (((Boolean)state.getValue(propertybool)).booleanValue()) {
            ++i;
         }
      }

      return i;
   }

   public boolean isShearable(@Nonnull ItemStack item, IBlockAccess world, BlockPos pos) {
      return true;
   }

   @Nonnull
   public List<ItemStack> onSheared(@Nonnull ItemStack item, IBlockAccess world, BlockPos pos, int fortune) {
      return Lists.newArrayList(new ItemStack[]{new ItemStack(this, 1)});
   }

   @Nonnull
   public BlockFaceShape getBlockFaceShape(IBlockAccess worldIn, IBlockState state, BlockPos pos, EnumFacing face) {
      return BlockFaceShape.UNDEFINED;
   }
}
