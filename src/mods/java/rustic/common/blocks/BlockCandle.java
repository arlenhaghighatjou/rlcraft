package rustic.common.blocks;

import com.google.common.base.Predicate;
import net.lax1dude.eaglercraft.v1_8.EaglercraftRandom;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.Mirror;
import net.minecraft.util.Rotation;
import net.minecraft.util.EnumFacing.Axis;
import net.minecraft.util.EnumFacing.Plane;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class BlockCandle extends BlockBase {
   public static final PropertyDirection FACING = PropertyDirection.create("facing", new Predicate<EnumFacing>() {
      public boolean apply(@Nullable EnumFacing p_apply_1_) {
         return p_apply_1_ != EnumFacing.DOWN;
      }
   });
   protected static final AxisAlignedBB STANDING_AABB = new AxisAlignedBB(0.4D, 0.0D, 0.4D, 0.6D, 0.9375D, 0.6D);
   protected static final AxisAlignedBB CANDLE_NORTH_AABB = new AxisAlignedBB(0.35D, 0.0D, 0.7D, 0.65D, 0.8D, 1.0D);
   protected static final AxisAlignedBB CANDLE_SOUTH_AABB = new AxisAlignedBB(0.35D, 0.0D, 0.0D, 0.65D, 0.8D, 0.3D);
   protected static final AxisAlignedBB CANDLE_WEST_AABB = new AxisAlignedBB(0.7D, 0.0D, 0.35D, 1.0D, 0.8D, 0.65D);
   protected static final AxisAlignedBB CANDLE_EAST_AABB = new AxisAlignedBB(0.0D, 0.0D, 0.35D, 0.3D, 0.8D, 0.65D);

   public BlockCandle() {
      this("candle");
   }

   public BlockCandle(String name) {
      this(Material.CIRCUITS, name, true);
   }

   public BlockCandle(Material mat, String name, boolean register) {
      super(mat, name, register);
      this.setHardness(1.0F);
      this.setLightLevel(1.0F);
      this.setDefaultState(this.blockState.getBaseState().withProperty(FACING, EnumFacing.UP));
      this.setSoundType(SoundType.METAL);
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

   private boolean canPlaceOn(World worldIn, BlockPos pos) {
      IBlockState state = worldIn.getBlockState(pos);
      BlockFaceShape faceShape = state.getBlockFaceShape(worldIn, pos, EnumFacing.UP);
      return faceShape != BlockFaceShape.SOLID && faceShape != BlockFaceShape.CENTER && faceShape != BlockFaceShape.CENTER_BIG && faceShape != BlockFaceShape.CENTER_SMALL ? state.getBlock().canPlaceTorchOnTop(state, worldIn, pos) : true;
   }

   public boolean canPlaceBlockAt(World worldIn, BlockPos pos) {
      for(EnumFacing enumfacing : FACING.getAllowedValues()) {
         if (this.canPlaceAt(worldIn, pos, enumfacing)) {
            return true;
         }
      }

      return false;
   }

   protected boolean canPlaceAt(World worldIn, BlockPos pos, EnumFacing facing) {
      BlockPos blockpos = pos.offset(facing.getOpposite());
      boolean flag = facing.getAxis().isHorizontal();
      return flag && worldIn.isSideSolid(blockpos, facing, true) || facing.equals(EnumFacing.UP) && this.canPlaceOn(worldIn, blockpos);
   }

   public IBlockState getStateForPlacement(World worldIn, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer) {
      if (this.canPlaceAt(worldIn, pos, facing)) {
         return this.getDefaultState().withProperty(FACING, facing);
      } else {
         for(EnumFacing enumfacing : Plane.HORIZONTAL) {
            if (worldIn.isSideSolid(pos.offset(enumfacing.getOpposite()), enumfacing, true)) {
               return this.getDefaultState().withProperty(FACING, enumfacing);
            }
         }

         return this.getDefaultState();
      }
   }

   public void onBlockAdded(World worldIn, BlockPos pos, IBlockState state) {
      this.checkForDrop(worldIn, pos, state);
   }

   public void neighborChanged(IBlockState state, World worldIn, BlockPos pos, Block blockIn, BlockPos fromPos) {
      this.onNeighborChangeInternal(worldIn, pos, state);
   }

   protected boolean onNeighborChangeInternal(World worldIn, BlockPos pos, IBlockState state) {
      if (!this.checkForDrop(worldIn, pos, state)) {
         return true;
      } else {
         EnumFacing enumfacing = (EnumFacing)state.getValue(FACING);
         Axis enumfacing$axis = enumfacing.getAxis();
         EnumFacing enumfacing1 = enumfacing.getOpposite();
         boolean flag = false;
         if (enumfacing$axis.isHorizontal() && !worldIn.isSideSolid(pos.offset(enumfacing1), enumfacing, true)) {
            flag = true;
         } else if (enumfacing$axis.isVertical() && !this.canPlaceOn(worldIn, pos.offset(enumfacing1))) {
            flag = true;
         }

         if (flag) {
            this.dropBlockAsItem(worldIn, pos, state, 0);
            worldIn.setBlockToAir(pos);
            return true;
         } else {
            return false;
         }
      }
   }

   protected boolean checkForDrop(World worldIn, BlockPos pos, IBlockState state) {
      if (state.getBlock() == this && this.canPlaceAt(worldIn, pos, (EnumFacing)state.getValue(FACING))) {
         return true;
      } else {
         if (worldIn.getBlockState(pos).getBlock() == this) {
            this.dropBlockAsItem(worldIn, pos, state, 0);
            worldIn.setBlockToAir(pos);
         }

         return false;
      }
   }

   public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
      switch((EnumFacing)state.getValue(FACING)) {
      case EAST:
         return CANDLE_EAST_AABB;
      case WEST:
         return CANDLE_WEST_AABB;
      case SOUTH:
         return CANDLE_SOUTH_AABB;
      case NORTH:
         return CANDLE_NORTH_AABB;
      default:
         return STANDING_AABB;
      }
   }

   @SideOnly(Side.CLIENT)
   public void randomDisplayTick(IBlockState stateIn, World worldIn, BlockPos pos, EaglercraftRandom rand) {
      EnumFacing enumfacing = (EnumFacing)stateIn.getValue(FACING);
      double d0 = (double)pos.getX() + 0.5D;
      double d1 = (double)pos.getY() + 0.7D;
      double d2 = (double)pos.getZ() + 0.5D;
      if (enumfacing.getAxis().isHorizontal()) {
         EnumFacing enumfacing1 = enumfacing.getOpposite();
         worldIn.spawnParticle(EnumParticleTypes.SMOKE_NORMAL, d0 + 0.27D * (double)enumfacing1.getFrontOffsetX(), d1 + 0.25D, d2 + 0.27D * (double)enumfacing1.getFrontOffsetZ(), 0.0D, 0.0D, 0.0D, new int[0]);
         worldIn.spawnParticle(EnumParticleTypes.FLAME, d0 + 0.27D * (double)enumfacing1.getFrontOffsetX(), d1 + 0.25D, d2 + 0.27D * (double)enumfacing1.getFrontOffsetZ(), 0.0D, 0.0D, 0.0D, new int[0]);
      } else {
         worldIn.spawnParticle(EnumParticleTypes.SMOKE_NORMAL, d0, d1 + 0.33D, d2, 0.0D, 0.0D, 0.0D, new int[0]);
         worldIn.spawnParticle(EnumParticleTypes.FLAME, d0, d1 + 0.33D, d2, 0.0D, 0.0D, 0.0D, new int[0]);
      }

   }

   public IBlockState getStateFromMeta(int meta) {
      IBlockState iblockstate = this.getDefaultState();
      switch(meta) {
      case 1:
         iblockstate = iblockstate.withProperty(FACING, EnumFacing.EAST);
         break;
      case 2:
         iblockstate = iblockstate.withProperty(FACING, EnumFacing.WEST);
         break;
      case 3:
         iblockstate = iblockstate.withProperty(FACING, EnumFacing.SOUTH);
         break;
      case 4:
         iblockstate = iblockstate.withProperty(FACING, EnumFacing.NORTH);
         break;
      case 5:
      default:
         iblockstate = iblockstate.withProperty(FACING, EnumFacing.UP);
      }

      return iblockstate;
   }

   @SideOnly(Side.CLIENT)
   public BlockRenderLayer getBlockLayer() {
      return BlockRenderLayer.CUTOUT;
   }

   public int getMetaFromState(IBlockState state) {
      int i = 0;
      switch((EnumFacing)state.getValue(FACING)) {
      case EAST:
         i = i | 1;
         break;
      case WEST:
         i = i | 2;
         break;
      case SOUTH:
         i = i | 3;
         break;
      case NORTH:
         i = i | 4;
         break;
      case DOWN:
      case UP:
      default:
         i = i | 5;
      }

      return i;
   }

   public IBlockState withRotation(IBlockState state, Rotation rot) {
      return state.withProperty(FACING, rot.rotate((EnumFacing)state.getValue(FACING)));
   }

   public IBlockState withMirror(IBlockState state, Mirror mirrorIn) {
      return state.withRotation(mirrorIn.toRotation((EnumFacing)state.getValue(FACING)));
   }

   protected BlockStateContainer createBlockState() {
      return new BlockStateContainer(this, new IProperty[]{FACING});
   }

   public BlockFaceShape getBlockFaceShape(IBlockAccess world, IBlockState state, BlockPos pos, EnumFacing side) {
      return side == EnumFacing.DOWN ? BlockFaceShape.CENTER_SMALL : BlockFaceShape.UNDEFINED;
   }
}
