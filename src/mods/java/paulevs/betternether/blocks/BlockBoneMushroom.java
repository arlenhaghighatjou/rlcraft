package paulevs.betternether.blocks;

import com.google.common.base.Predicate;
import net.lax1dude.eaglercraft.v1_8.EaglercraftRandom;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.BlockBone;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.properties.PropertyInteger;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.Mirror;
import net.minecraft.util.Rotation;
import net.minecraft.util.EnumFacing.Plane;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import paulevs.betternether.BetterNether;

public class BlockBoneMushroom extends Block {
   public static final PropertyDirection FACING = PropertyDirection.create("facing", new Predicate<EnumFacing>() {
      public boolean apply(@Nullable EnumFacing p_apply_1_) {
         return p_apply_1_ != EnumFacing.DOWN;
      }
   });
   public static final PropertyInteger AGE = PropertyInteger.create("age", 0, 2);

   public BlockBoneMushroom() {
      super(Material.PLANTS, MapColor.BLACK);
      this.setDefaultState(this.blockState.getBaseState().withProperty(FACING, EnumFacing.UP).withProperty(AGE, Integer.valueOf(0)));
      this.setCreativeTab(BetterNether.BN_TAB);
      this.setSoundType(SoundType.PLANT);
      this.setRegistryName("bone_mushroom");
      this.setUnlocalizedName("bone_mushroom");
      this.setTickRandomly(true);
   }

   @Nullable
   public AxisAlignedBB getCollisionBoundingBox(IBlockState blockState, IBlockAccess worldIn, BlockPos pos) {
      return NULL_AABB;
   }

   public boolean isOpaqueCube(IBlockState state) {
      return false;
   }

   public boolean isFullCube(IBlockState state) {
      return false;
   }

   public boolean canPlaceBlockAt(World worldIn, BlockPos pos) {
      for(EnumFacing enumfacing : FACING.getAllowedValues()) {
         if (this.canPlaceAt(worldIn, pos, enumfacing)) {
            return true;
         }
      }

      return false;
   }

   private boolean canPlaceAt(World worldIn, BlockPos pos, EnumFacing facing) {
      if (facing == EnumFacing.DOWN) {
         return false;
      } else {
         BlockPos blockpos = pos.offset(facing.getOpposite());
         Block block = worldIn.getBlockState(blockpos).getBlock();
         return block instanceof BNBlockBone || block instanceof BlockBone;
      }
   }

   public IBlockState getStateForPlacement(World worldIn, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer) {
      if (this.canPlaceAt(worldIn, pos, facing)) {
         return this.getDefaultState().withProperty(FACING, facing);
      } else {
         for(EnumFacing enumfacing : Plane.HORIZONTAL) {
            if (this.canPlaceAt(worldIn, pos, enumfacing)) {
               return this.getDefaultState().withProperty(FACING, enumfacing);
            }
         }

         return this.getDefaultState();
      }
   }

   public IBlockState getStateFromMeta(int meta) {
      IBlockState iblockstate = this.getDefaultState();
      switch(meta % 5) {
      case 0:
      default:
         iblockstate = iblockstate.withProperty(FACING, EnumFacing.UP);
         break;
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
      }

      return iblockstate.withProperty(AGE, Integer.valueOf(meta / 5));
   }

   @SideOnly(Side.CLIENT)
   public BlockRenderLayer getBlockLayer() {
      return BlockRenderLayer.CUTOUT;
   }

   public int getMetaFromState(IBlockState state) {
      int i = ((Integer)state.getValue(AGE)).intValue() * 5;
      switch((EnumFacing)state.getValue(FACING)) {
      case EAST:
         return i + 1;
      case WEST:
         return i + 2;
      case SOUTH:
         return i + 3;
      case NORTH:
         return i + 4;
      case DOWN:
      case UP:
      default:
         return i;
      }
   }

   public IBlockState withRotation(IBlockState state, Rotation rot) {
      return state.withProperty(FACING, rot.rotate((EnumFacing)state.getValue(FACING)));
   }

   public IBlockState withMirror(IBlockState state, Mirror mirrorIn) {
      return state.withRotation(mirrorIn.toRotation((EnumFacing)state.getValue(FACING)));
   }

   protected BlockStateContainer createBlockState() {
      return new BlockStateContainer(this, new IProperty[]{FACING, AGE});
   }

   public BlockFaceShape getBlockFaceShape(IBlockAccess worldIn, IBlockState state, BlockPos pos, EnumFacing face) {
      return BlockFaceShape.UNDEFINED;
   }

   public void neighborChanged(IBlockState state, World worldIn, BlockPos pos, Block blockIn, BlockPos fromPos) {
      BlockPos p = pos.offset(((EnumFacing)state.getValue(FACING)).getOpposite());
      Block block = worldIn.getBlockState(p).getBlock();
      if (!(block instanceof BNBlockBone) && !(block instanceof BlockBone)) {
         this.dropBlockAsItem(worldIn, pos, state, 0);
         worldIn.setBlockToAir(pos);
      }

   }

   public void updateTick(World worldIn, BlockPos pos, IBlockState state, EaglercraftRandom rand) {
      if (((Integer)state.getValue(AGE)).intValue() < 2 && rand.nextInt(32) == 0) {
         worldIn.setBlockState(pos, state.withProperty(AGE, Integer.valueOf(((Integer)state.getValue(AGE)).intValue() + 1)));
      }

   }

   private void spawnSeeds(World world, BlockPos pos, EaglercraftRandom random) {
      if (!world.isRemote) {
         ItemStack drop = new ItemStack(this, 2 + random.nextInt(3));
         EntityItem itemEntity = new EntityItem(world, (double)pos.getX() + 0.5D, (double)pos.getY() + 0.5D, (double)pos.getZ() + 0.5D, drop);
         world.spawnEntity(itemEntity);
         drop = new ItemStack(Items.DYE, 1 + random.nextInt(2), 15);
         itemEntity = new EntityItem(world, (double)pos.getX() + 0.5D, (double)pos.getY() + 0.5D, (double)pos.getZ() + 0.5D, drop);
         world.spawnEntity(itemEntity);
      }

   }

   public void onBlockDestroyedByPlayer(World worldIn, BlockPos pos, IBlockState state) {
      if (((Integer)state.getValue(AGE)).intValue() > 1) {
         this.spawnSeeds(worldIn, pos, worldIn.rand);
      }

      worldIn.destroyBlock(pos, true);
   }
}
