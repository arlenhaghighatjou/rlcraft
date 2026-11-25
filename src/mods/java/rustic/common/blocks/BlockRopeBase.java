package rustic.common.blocks;

import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.Rotation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.EnumFacing.Axis;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import rustic.common.blocks.crops.BlockGrapeLeaves;

public abstract class BlockRopeBase extends BlockBase {
   public static final PropertyEnum<Axis> AXIS = PropertyEnum.create("axis", Axis.class);
   public static final PropertyBool DANGLE = PropertyBool.create("dangle");
   protected static final AxisAlignedBB Y_AABB = new AxisAlignedBB(0.4375D, 0.0D, 0.4375D, 0.5625D, 1.0D, 0.5625D);
   protected static final AxisAlignedBB X_AABB = new AxisAlignedBB(0.0D, 0.4375D, 0.4375D, 1.0D, 0.5625D, 0.5625D);
   protected static final AxisAlignedBB Z_AABB = new AxisAlignedBB(0.4375D, 0.4375D, 0.0D, 0.5625D, 0.5625D, 1.0D);
   protected static final AxisAlignedBB X_DANGLE_AABB = new AxisAlignedBB(0.0D, 0.0D, 0.4375D, 1.0D, 0.5625D, 0.5625D);
   protected static final AxisAlignedBB Z_DANGLE_AABB = new AxisAlignedBB(0.4375D, 0.0D, 0.0D, 0.5625D, 0.5625D, 1.0D);

   public BlockRopeBase(Material mat, String name, boolean register) {
      super(mat, name, register);
   }

   public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing side, float hitX, float hitY, float hitZ) {
      ItemStack stack = player.getHeldItem(hand);
      if (!this.canPlaceBlockOnSide(world, pos.offset(side), side) && stack.getItem() == Item.getItemFromBlock(this)) {
         if (!this.isBlockSupported(world, pos, state)) {
            this.dropBlock(world, pos, state);
            return true;
         }

         int yOffset;
         for(yOffset = 1; yOffset < 64 && world.getBlockState(pos.down(yOffset)).getBlock() == this; ++yOffset) {
            if (world.getBlockState(pos.down(yOffset)).getValue(AXIS) != Axis.Y) {
               return false;
            }
         }

         if (this.canPlaceBlockAt(world, pos.down(yOffset))) {
            world.setBlockState(pos.down(yOffset), state.withProperty(AXIS, Axis.Y), 3);
            if (!player.capabilities.isCreativeMode) {
               player.getHeldItem(hand).shrink(1);
            }

            SoundType soundType = this.getSoundType(state, world, pos, player);
            world.playSound((double)pos.getX(), (double)(pos.getY() - yOffset), (double)pos.getZ(), soundType.getPlaceSound(), SoundCategory.BLOCKS, (soundType.getVolume() + 1.0F) / 2.0F, soundType.getPitch() * 0.8F, false);
            return true;
         }
      }

      return false;
   }

   public boolean isLadder(IBlockState state, IBlockAccess world, BlockPos pos, EntityLivingBase entity) {
      return true;
   }

   public int damageDropped(IBlockState state) {
      return 0;
   }

   public boolean canPlaceBlockOnSide(World world, BlockPos pos, EnumFacing side) {
      IBlockState testState = world.getBlockState(pos.offset(side.getOpposite()));
      if (side == EnumFacing.UP) {
         return this.canPlaceBlockOnSide(world, pos, EnumFacing.DOWN);
      } else {
         boolean isThis = testState.getBlock() == this && testState.getValue(AXIS) == side.getAxis();
         boolean isSideSolid = world.isSideSolid(pos.offset(side.getOpposite()), side, false);
         return isThis || isSideSolid;
      }
   }

   protected void dropBlock(World worldIn, BlockPos pos, IBlockState state) {
      this.dropBlockAsItem(worldIn, pos, state, 0);
      worldIn.setBlockToAir(pos);
      SoundType soundType = this.getSoundType(state, worldIn, pos, (Entity)null);
      worldIn.playSound((double)pos.getX(), (double)pos.getY(), (double)pos.getZ(), soundType.getBreakSound(), SoundCategory.BLOCKS, (soundType.getVolume() + 1.0F) / 2.0F, soundType.getPitch() * 0.8F, true);
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

      if (dir != null && state.getValue(AXIS) == dir.getAxis() && !this.isSideSupported(worldIn, pos, state, dir)) {
         if (dir == EnumFacing.UP) {
            this.dropBlock(worldIn, pos, state);
         } else if (!this.isBlockSupported(worldIn, pos, state)) {
            this.dropBlock(worldIn, pos, state);
         }
      }

   }

   public boolean isSideSupported(World world, BlockPos pos, IBlockState state, EnumFacing facing) {
      IBlockState testState = world.getBlockState(pos.offset(facing));
      if (facing == EnumFacing.DOWN) {
         return false;
      } else {
         boolean isSame = testState.getBlock() == state.getBlock() && (state.getValue(AXIS) == Axis.Y && facing.getAxis() == Axis.Y || testState.getValue(AXIS) == state.getValue(AXIS));
         boolean isSideSolid = world.isSideSolid(pos.offset(facing), facing.getOpposite(), false);
         return isSame || isSideSolid;
      }
   }

   public boolean isBlockSupported(World world, BlockPos pos, IBlockState state) {
      if (state.getValue(AXIS) == Axis.X) {
         return this.isSideSupported(world, pos, state, EnumFacing.WEST) && this.isSideSupported(world, pos, state, EnumFacing.EAST);
      } else if (state.getValue(AXIS) == Axis.Y) {
         return this.isSideSupported(world, pos, state, EnumFacing.UP);
      } else if (state.getValue(AXIS) != Axis.Z) {
         return false;
      } else {
         return this.isSideSupported(world, pos, state, EnumFacing.NORTH) && this.isSideSupported(world, pos, state, EnumFacing.SOUTH);
      }
   }

   protected BlockStateContainer createBlockState() {
      return new BlockStateContainer(this, new IProperty[]{AXIS, DANGLE});
   }

   public IBlockState getStateFromMeta(int meta) {
      Axis enumfacing$axis = Axis.Y;
      int i = meta & 3;
      if (i == 0) {
         enumfacing$axis = Axis.Y;
      } else if (i == 1) {
         enumfacing$axis = Axis.X;
      } else if (i == 2) {
         enumfacing$axis = Axis.Z;
      }

      return this.getDefaultState().withProperty(AXIS, enumfacing$axis);
   }

   public int getMetaFromState(IBlockState state) {
      int i = 0;
      Axis enumfacing$axis = (Axis)state.getValue(AXIS);
      if (enumfacing$axis == Axis.X) {
         i = 1;
      } else if (enumfacing$axis == Axis.Z) {
         i = 2;
      }

      return i;
   }

   public IBlockState getActualState(IBlockState state, IBlockAccess world, BlockPos pos) {
      return state.getValue(AXIS) != Axis.Y && world.getBlockState(pos.down()).getBlock() instanceof BlockRopeBase && !(world.getBlockState(pos.down()).getBlock() instanceof BlockGrapeLeaves) && world.getBlockState(pos.down()).getValue(BlockRope.AXIS) == Axis.Y ? state.withProperty(DANGLE, Boolean.valueOf(true)) : state.withProperty(DANGLE, Boolean.valueOf(false));
   }

   public IBlockState getStateForPlacement(World worldIn, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer) {
      return super.getStateForPlacement(worldIn, pos, facing, hitX, hitY, hitZ, meta, placer).withProperty(AXIS, facing.getAxis());
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

   public boolean isOpaqueCube(IBlockState state) {
      return false;
   }

   public boolean isFullCube(IBlockState state) {
      return false;
   }

   public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
      state = this.getActualState(state, source, pos);
      switch((Axis)state.getValue(AXIS)) {
      case X:
         if (((Boolean)state.getValue(DANGLE)).booleanValue()) {
            return X_DANGLE_AABB;
         }

         return X_AABB;
      case Z:
         if (((Boolean)state.getValue(DANGLE)).booleanValue()) {
            return Z_DANGLE_AABB;
         }

         return Z_AABB;
      case Y:
         return Y_AABB;
      default:
         return Y_AABB;
      }
   }

   public boolean isPassable(IBlockAccess worldIn, BlockPos pos) {
      return false;
   }
}
