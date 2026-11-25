package rustic.common.blocks.crops;

import com.google.common.base.Predicate;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.IGrowable;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.properties.PropertyInteger;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.color.IBlockColor;
import net.minecraft.client.renderer.color.IItemColor;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.Rotation;
import net.minecraft.util.EnumFacing.Axis;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.ColorizerFoliage;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeColorHelper;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import rustic.common.blocks.BlockLattice;
import rustic.common.blocks.BlockRope;
import rustic.common.blocks.BlockRopeBase;
import rustic.common.blocks.IColoredBlock;
import rustic.common.blocks.ModBlocks;
import rustic.common.items.ModItems;
import rustic.core.ClientProxy;
import rustic.core.Rustic;

public class BlockGrapeLeaves extends BlockRopeBase implements IGrowable, IColoredBlock {
   public static final PropertyBool GRAPES = PropertyBool.create("grapes");
   public static final PropertyInteger DIST = PropertyInteger.create("distance", 0, 1);
   public static final PropertyEnum<Axis> AXIS = PropertyEnum.create("axis", Axis.class, new Predicate<Axis>() {
      public boolean apply(@Nullable Axis p_apply_1_) {
         return p_apply_1_ != Axis.Y;
      }
   });
   public static final AxisAlignedBB BRANCH_Z_AABB = new AxisAlignedBB(0.1875D, 0.1875D, 0.0D, 0.8125D, 0.8125D, 1.0D);
   public static final AxisAlignedBB BRANCH_X_AABB = new AxisAlignedBB(0.0D, 0.1875D, 0.1875D, 1.0D, 0.8125D, 0.8125D);

   public BlockGrapeLeaves() {
      super(Material.LEAVES, "grape_leaves", false);
      GameRegistry.findRegistry(Block.class).register(this);
      this.setTickRandomly(true);
      this.setCreativeTab(Rustic.farmingTab);
      this.setSoundType(SoundType.PLANT);
      this.setDefaultState(this.blockState.getBaseState().withProperty(AXIS, Axis.X).withProperty(GRAPES, Boolean.valueOf(false)).withProperty(DIST, Integer.valueOf(0)));
      Blocks.FIRE.setFireInfo(this, 30, 60);
   }

   public List<ItemStack> getDrops(IBlockAccess world, BlockPos pos, IBlockState state, int fortune) {
      List<ItemStack> stacks = new ArrayList();
      if (((Boolean)state.getValue(GRAPES)).booleanValue()) {
         stacks.add(new ItemStack(ModItems.GRAPES));
      }

      return stacks;
   }

   public void breakBlock(World world, BlockPos pos, IBlockState state) {
      Axis axis = (Axis)state.getValue(AXIS);
      super.breakBlock(world, pos, state);
      world.setBlockState(pos, ModBlocks.ROPE.getDefaultState().withProperty(BlockRope.AXIS, axis), 3);
   }

   public int damageDropped(IBlockState state) {
      return 0;
   }

   public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing side, float hitX, float hitY, float hitZ) {
      if (((Boolean)state.getValue(GRAPES)).booleanValue()) {
         world.setBlockState(pos, state.withProperty(GRAPES, Boolean.valueOf(false)), 3);
         Block.spawnAsEntity(world, pos.offset(side), new ItemStack(ModItems.GRAPES, world.rand.nextInt(2) + 1));
         return true;
      } else {
         return false;
      }
   }

   public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
      if (((Integer)state.getValue(DIST)).intValue() < 1) {
         return Block.FULL_BLOCK_AABB;
      } else {
         return state.getValue(AXIS) == Axis.Z ? BRANCH_Z_AABB : BRANCH_X_AABB;
      }
   }

   public boolean causesSuffocation(IBlockState state) {
      return false;
   }

   public boolean isLadder(IBlockState state, IBlockAccess world, BlockPos pos, EntityLivingBase entity) {
      return false;
   }

   public boolean isLeaves(IBlockState state, IBlockAccess world, BlockPos pos) {
      return true;
   }

   public boolean isOpaqueCube(IBlockState state) {
      return false;
   }

   public boolean isFullCube(IBlockState state) {
      return false;
   }

   public ItemStack getPickBlock(IBlockState state, RayTraceResult target, World world, BlockPos pos, EntityPlayer player) {
      return new ItemStack(ModItems.GRAPES);
   }

   public void neighborChanged(IBlockState state, World worldIn, BlockPos pos, Block blockIn, BlockPos fromPos) {
      EnumFacing dir = null;
      if (fromPos.getX() != pos.getX()) {
         dir = fromPos.getX() - pos.getX() < 0 ? EnumFacing.WEST : EnumFacing.EAST;
      } else if (fromPos.getY() != pos.getY()) {
         dir = fromPos.getY() - pos.getY() < 0 ? EnumFacing.DOWN : EnumFacing.UP;
      } else if (fromPos.getZ() != pos.getZ()) {
         dir = fromPos.getZ() - pos.getZ() < 0 ? EnumFacing.NORTH : EnumFacing.SOUTH;
      }

      if (dir != null && state.getValue(AXIS) == dir.getAxis() && worldIn.isAirBlock(fromPos)) {
         if (dir == EnumFacing.DOWN) {
            this.dropBlock(worldIn, pos, state);
         } else if (!this.isBlockSupported(worldIn, pos, state)) {
            this.dropBlock(worldIn, pos, state);
         }
      }

   }

   public boolean isSideSupported(World world, BlockPos pos, IBlockState state, EnumFacing facing) {
      IBlockState testState = world.getBlockState(pos.offset(facing));
      boolean isSame = testState.getBlock() == state.getBlock() && testState.getValue(AXIS) == state.getValue(AXIS);
      boolean isRope = testState.getBlock() == ModBlocks.ROPE && state.getValue(AXIS) == testState.getValue(BlockRope.AXIS);
      boolean isSideSolid = world.isSideSolid(pos.offset(facing), facing.getOpposite(), false);
      boolean isTiedStake = testState.getBlock() == ModBlocks.STAKE_TIED;
      boolean isLattice = testState.getBlock() instanceof BlockLattice;
      return isSame || isRope || isSideSolid || isTiedStake || isLattice;
   }

   public boolean isBlockSupported(World world, BlockPos pos, IBlockState state) {
      if (state.getValue(AXIS) == Axis.X) {
         return this.isSideSupported(world, pos, state, EnumFacing.WEST) && this.isSideSupported(world, pos, state, EnumFacing.EAST);
      } else if (state.getValue(AXIS) != Axis.Z) {
         return false;
      } else {
         return this.isSideSupported(world, pos, state, EnumFacing.NORTH) && this.isSideSupported(world, pos, state, EnumFacing.SOUTH);
      }
   }

   public void updateTick(World worldIn, BlockPos pos, IBlockState state, Random rand) {
      super.updateTick(worldIn, pos, state, rand);
      if (!this.isBlockSupported(worldIn, pos, state)) {
         this.dropBlock(worldIn, pos, state);
      }

      if (worldIn.getLightFromNeighbors(pos.up()) >= 9) {
         int i = ((Integer)state.getValue(DIST)).intValue();
         if (i > 0 && !((Boolean)state.getValue(GRAPES)).booleanValue() && worldIn.isAirBlock(pos.down())) {
            float f = getGrowthChance(this, worldIn, pos);
            if (ForgeHooks.onCropsGrowPre(worldIn, pos, state, rand.nextInt((int)(25.0F / f) + 1) == 0)) {
               worldIn.setBlockState(pos, state.withProperty(GRAPES, Boolean.valueOf(true)), 3);
               ForgeHooks.onCropsGrowPost(worldIn, pos, state, worldIn.getBlockState(pos));
            }
         } else if (i < 1 && this.canSpread(worldIn, pos, state)) {
            float f = getGrowthChance(this, worldIn, pos);
            if (ForgeHooks.onCropsGrowPre(worldIn, pos, state, rand.nextInt((int)(25.0F / f) + 1) == 0)) {
               this.spread(worldIn, pos, state);
               ForgeHooks.onCropsGrowPost(worldIn, pos, state, worldIn.getBlockState(pos));
            }
         }
      }

   }

   protected static float getGrowthChance(Block blockIn, World worldIn, BlockPos pos) {
      return 3.0F;
   }

   public boolean canGrow(World worldIn, BlockPos pos, IBlockState state, boolean isClient) {
      if (((Integer)state.getValue(DIST)).intValue() <= 0) {
         return this.canSpread(worldIn, pos, state);
      } else {
         return !((Boolean)state.getValue(GRAPES)).booleanValue() && worldIn.isAirBlock(pos.down());
      }
   }

   public boolean canUseBonemeal(World worldIn, Random rand, BlockPos pos, IBlockState state) {
      return true;
   }

   public void grow(World worldIn, Random rand, BlockPos pos, IBlockState state) {
      if (((Integer)state.getValue(DIST)).intValue() > 0) {
         worldIn.setBlockState(pos, state.withProperty(GRAPES, Boolean.valueOf(true)), 3);
      } else {
         this.spread(worldIn, pos, state);
      }

   }

   public boolean canSpread(World world, BlockPos pos, IBlockState state) {
      if (((Integer)state.getValue(DIST)).intValue() == 0) {
         switch((Axis)state.getValue(AXIS)) {
         case X:
            return world.getBlockState(pos.west()).getBlock() == ModBlocks.ROPE && world.getBlockState(pos.west()).getValue(BlockRope.AXIS) == state.getValue(AXIS) || world.getBlockState(pos.east()).getBlock() == ModBlocks.ROPE && world.getBlockState(pos.east()).getValue(BlockRope.AXIS) == state.getValue(AXIS);
         case Z:
            return world.getBlockState(pos.north()).getBlock() == ModBlocks.ROPE && world.getBlockState(pos.north()).getValue(BlockRope.AXIS) == state.getValue(AXIS) || world.getBlockState(pos.south()).getBlock() == ModBlocks.ROPE && world.getBlockState(pos.south()).getValue(BlockRope.AXIS) == state.getValue(AXIS);
         }
      }

      return false;
   }

   public void spread(World world, BlockPos pos, IBlockState state) {
      if (((Integer)state.getValue(DIST)).intValue() < 1) {
         switch((Axis)state.getValue(AXIS)) {
         case X:
            boolean westRope = world.getBlockState(pos.west()).getBlock() == ModBlocks.ROPE && world.getBlockState(pos.west()).getValue(BlockRope.AXIS) == state.getValue(AXIS);
            boolean eastRope = world.getBlockState(pos.east()).getBlock() == ModBlocks.ROPE && world.getBlockState(pos.east()).getValue(BlockRope.AXIS) == state.getValue(AXIS);
            if (westRope && eastRope) {
               this.spreadToValidRope(world, pos, (double)world.rand.nextFloat() < 0.5D ? pos.west() : pos.east(), state);
            } else if (westRope) {
               this.spreadToValidRope(world, pos, pos.west(), state);
            } else if (eastRope) {
               this.spreadToValidRope(world, pos, pos.east(), state);
            }
            break;
         case Z:
            boolean northRope = world.getBlockState(pos.north()).getBlock() == ModBlocks.ROPE && world.getBlockState(pos.north()).getValue(BlockRope.AXIS) == state.getValue(AXIS);
            boolean southRope = world.getBlockState(pos.south()).getBlock() == ModBlocks.ROPE && world.getBlockState(pos.south()).getValue(BlockRope.AXIS) == state.getValue(AXIS);
            if (northRope && southRope) {
               this.spreadToValidRope(world, pos, (double)world.rand.nextFloat() < 0.5D ? pos.north() : pos.south(), state);
            } else if (northRope) {
               this.spreadToValidRope(world, pos, pos.north(), state);
            } else if (southRope) {
               this.spreadToValidRope(world, pos, pos.south(), state);
            }
         }
      }

   }

   private void spreadToValidRope(World world, BlockPos origPos, BlockPos newPos, IBlockState state) {
      Axis axis = (Axis)world.getBlockState(newPos).getValue(BlockRope.AXIS);
      world.setBlockState(newPos, this.getDefaultState().withProperty(AXIS, axis).withProperty(DIST, Integer.valueOf(1)), 3);
   }

   @SideOnly(Side.CLIENT)
   public BlockRenderLayer getBlockLayer() {
      return BlockRenderLayer.CUTOUT;
   }

   protected BlockStateContainer createBlockState() {
      return new BlockStateContainer(this, new IProperty[]{AXIS, DANGLE, DIST, GRAPES});
   }

   public IBlockState getActualState(IBlockState state, IBlockAccess world, BlockPos pos) {
      return world.getBlockState(pos.down()).getBlock() instanceof BlockRope && world.getBlockState(pos.down()).getValue(BlockRope.AXIS) == Axis.Y ? state.withProperty(DANGLE, Boolean.valueOf(true)) : state.withProperty(DANGLE, Boolean.valueOf(false));
   }

   public IBlockState getStateForPlacement(World worldIn, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer) {
      return this.getDefaultState();
   }

   public boolean canPlaceBlockOnSide(World world, BlockPos pos, EnumFacing side) {
      return false;
   }

   public IBlockState getStateFromMeta(int meta) {
      Axis enumfacing$axis = Axis.X;
      int dist = 0;
      boolean grapes = false;
      int i = meta & 1;
      if (i == 0) {
         enumfacing$axis = Axis.X;
      } else if (i == 1) {
         enumfacing$axis = Axis.Z;
      }

      i = meta & 2;
      if (i > 0) {
         dist = 1;
      }

      i = meta & 8;
      if (i > 0) {
         grapes = true;
      }

      return this.getDefaultState().withProperty(AXIS, enumfacing$axis).withProperty(DIST, Integer.valueOf(dist)).withProperty(GRAPES, Boolean.valueOf(grapes));
   }

   public int getMetaFromState(IBlockState state) {
      int i = 0;
      Axis enumfacing$axis = (Axis)state.getValue(AXIS);
      if (enumfacing$axis == Axis.X) {
         i = 0;
      } else if (enumfacing$axis == Axis.Z) {
         i = 1;
      }

      if (((Integer)state.getValue(DIST)).intValue() == 1) {
         i |= 2;
      }

      if (((Boolean)state.getValue(GRAPES)).booleanValue()) {
         i |= 8;
      }

      return i;
   }

   public IBlockState withRotation(IBlockState state, Rotation rot) {
      switch(rot) {
      case COUNTERCLOCKWISE_90:
      case CLOCKWISE_90:
         switch((Axis)state.getValue(AXIS)) {
         case X:
            return state.withProperty(AXIS, Axis.Z);
         case Z:
            return state.withProperty(AXIS, Axis.X);
         default:
            return state;
         }
      default:
         return state;
      }
   }

   @SideOnly(Side.CLIENT)
   public IBlockColor getBlockColor() {
      return new IBlockColor() {
         public int colorMultiplier(IBlockState state, IBlockAccess worldIn, BlockPos pos, int tintIndex) {
            return worldIn != null && pos != null ? BiomeColorHelper.getFoliageColorAtPos(worldIn, pos) : ColorizerFoliage.getFoliageColorBasic();
         }
      };
   }

   @SideOnly(Side.CLIENT)
   public IItemColor getItemColor() {
      return new IItemColor() {
         public int colorMultiplier(ItemStack stack, int tintIndex) {
            if (!(stack.getItem() instanceof ItemBlock)) {
               return 16777215;
            } else {
               IBlockState state = ((ItemBlock)stack.getItem()).getBlock().getStateFromMeta(stack.getMetadata());
               IBlockColor blockColor = ((IColoredBlock)state.getBlock()).getBlockColor();
               return blockColor == null ? 16777215 : blockColor.colorMultiplier(state, (IBlockAccess)null, (BlockPos)null, tintIndex);
            }
         }
      };
   }

   @SideOnly(Side.CLIENT)
   public void initModel() {
      ClientProxy.addColoredBlock(this);
   }
}
