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

public class BlockWoodLantern extends BlockBase {
   public static final PropertyDirection FACING = PropertyDirection.create("facing", new Predicate<EnumFacing>() {
      public boolean apply(@Nullable EnumFacing p_apply_1_) {
         return p_apply_1_ != EnumFacing.DOWN;
      }
   });
   protected static final AxisAlignedBB LANTERN_AABB = new AxisAlignedBB(0.25D, 0.0D, 0.25D, 0.75D, 1.0D, 0.75D);
   protected static final AxisAlignedBB LANTERN_NORTH_AABB = new AxisAlignedBB(0.25D, 0.0D, 0.25D, 0.75D, 1.0D, 1.0D);
   protected static final AxisAlignedBB LANTERN_SOUTH_AABB = new AxisAlignedBB(0.25D, 0.0D, 0.0D, 0.75D, 1.0D, 0.75D);
   protected static final AxisAlignedBB LANTERN_WEST_AABB = new AxisAlignedBB(0.25D, 0.0D, 0.25D, 1.0D, 1.0D, 0.75D);
   protected static final AxisAlignedBB LANTERN_EAST_AABB = new AxisAlignedBB(0.0D, 0.0D, 0.25D, 0.75D, 1.0D, 0.75D);

   public BlockWoodLantern() {
      this("lantern_wood");
   }

   public BlockWoodLantern(String name) {
      this(Material.WOOD, name, true);
   }

   public BlockWoodLantern(Material mat, String name, boolean register) {
      super(mat, name, register);
      this.setHardness(1.0F);
      this.setLightLevel(1.0F);
      this.setDefaultState(this.blockState.getBaseState().withProperty(FACING, EnumFacing.UP));
      this.setSoundType(SoundType.WOOD);
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

   public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
      switch((EnumFacing)state.getValue(FACING)) {
      case EAST:
         return LANTERN_EAST_AABB;
      case WEST:
         return LANTERN_WEST_AABB;
      case SOUTH:
         return LANTERN_SOUTH_AABB;
      case NORTH:
         return LANTERN_NORTH_AABB;
      default:
         return LANTERN_AABB;
      }
   }

   public boolean canPlaceBlockAt(World worldIn, BlockPos pos) {
      for(EnumFacing facing : EnumFacing.VALUES) {
         if (this.canPlaceAt(worldIn, pos, facing)) {
            return true;
         }
      }

      return false;
   }

   private boolean canPlaceAt(World worldIn, BlockPos pos, EnumFacing facing) {
      BlockPos blockpos = pos.offset(facing.getOpposite());
      if (facing.getAxis().isHorizontal()) {
         return worldIn.isSideSolid(blockpos, facing, true);
      } else {
         IBlockState state = worldIn.getBlockState(blockpos);
         BlockFaceShape faceShape = state.getBlockFaceShape(worldIn, blockpos, facing);
         if (faceShape != BlockFaceShape.SOLID && faceShape != BlockFaceShape.CENTER && faceShape != BlockFaceShape.CENTER_BIG && faceShape != BlockFaceShape.CENTER_SMALL) {
            return facing == EnumFacing.UP ? state.getBlock().canPlaceTorchOnTop(state, worldIn, blockpos) : false;
         } else {
            return true;
         }
      }
   }

   public IBlockState getStateForPlacement(World worldIn, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer) {
      if (this.canPlaceAt(worldIn, pos, facing)) {
         return facing == EnumFacing.DOWN ? this.getDefaultState().withProperty(FACING, EnumFacing.UP) : this.getDefaultState().withProperty(FACING, facing);
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
         Axis axis = enumfacing.getAxis();
         boolean flag = false;
         if (axis.isHorizontal()) {
            if (!this.canPlaceAt(worldIn, pos, enumfacing)) {
               flag = true;
            }
         } else if (!this.canPlaceAt(worldIn, pos, EnumFacing.UP) && !this.canPlaceAt(worldIn, pos, EnumFacing.DOWN)) {
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
      if (state.getBlock() == this) {
         EnumFacing facing = (EnumFacing)state.getValue(FACING);
         if (this.canPlaceAt(worldIn, pos, facing)) {
            return true;
         }

         if (facing == EnumFacing.UP && this.canPlaceAt(worldIn, pos, EnumFacing.DOWN)) {
            return true;
         }
      }

      if (worldIn.getBlockState(pos).getBlock() == this) {
         this.dropBlockAsItem(worldIn, pos, state, 0);
         worldIn.setBlockToAir(pos);
      }

      return false;
   }

   @SideOnly(Side.CLIENT)
   public void randomDisplayTick(IBlockState stateIn, World worldIn, BlockPos pos, EaglercraftRandom rand) {
      EnumFacing enumfacing = (EnumFacing)stateIn.getValue(FACING);
      double d0 = (double)pos.getX() + 0.5D;
      double d1 = (double)pos.getY() + 0.5D + (double)rand.nextFloat() * 0.375D;
      double d2 = (double)pos.getZ() + 0.5D;
      double rx = (double)(rand.nextFloat() * 0.6F - 0.3F);
      double rz = (double)(rand.nextFloat() * 0.6F - 0.3F);
      worldIn.spawnParticle(EnumParticleTypes.SMOKE_NORMAL, d0 + rx, d1, d2 + rz, 0.0D, 0.0D, 0.0D, new int[0]);
      worldIn.spawnParticle(EnumParticleTypes.FLAME, d0 + rx, d1, d2 + rz, 0.0D, 0.0D, 0.0D, new int[0]);
   }

   @SideOnly(Side.CLIENT)
   public BlockRenderLayer getBlockLayer() {
      return BlockRenderLayer.CUTOUT;
   }

   public IBlockState withRotation(IBlockState state, Rotation rot) {
      EnumFacing facing = rot.rotate((EnumFacing)state.getValue(FACING));
      if (facing == EnumFacing.DOWN) {
         facing = EnumFacing.UP;
      }

      return state.withProperty(FACING, facing);
   }

   public IBlockState withMirror(IBlockState state, Mirror mirrorIn) {
      return state.withRotation(mirrorIn.toRotation((EnumFacing)state.getValue(FACING)));
   }

   public IBlockState getStateFromMeta(int meta) {
      IBlockState iblockstate = this.getDefaultState();
      switch(meta) {
      case 1:
         return iblockstate.withProperty(FACING, EnumFacing.EAST);
      case 2:
         return iblockstate.withProperty(FACING, EnumFacing.WEST);
      case 3:
         return iblockstate.withProperty(FACING, EnumFacing.SOUTH);
      case 4:
         return iblockstate.withProperty(FACING, EnumFacing.NORTH);
      case 5:
      default:
         return iblockstate;
      }
   }

   public int getMetaFromState(IBlockState state) {
      switch((EnumFacing)state.getValue(FACING)) {
      case EAST:
         return 1;
      case WEST:
         return 2;
      case SOUTH:
         return 3;
      case NORTH:
         return 4;
      case DOWN:
      case UP:
      default:
         return 0;
      }
   }

   protected BlockStateContainer createBlockState() {
      return new BlockStateContainer(this, new IProperty[]{FACING});
   }

   public BlockFaceShape getBlockFaceShape(IBlockAccess world, IBlockState state, BlockPos pos, EnumFacing side) {
      return side.getAxis().isVertical() ? BlockFaceShape.CENTER_BIG : BlockFaceShape.UNDEFINED;
   }
}
