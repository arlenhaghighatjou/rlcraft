package knightminer.inspirations.utility.block;

import net.lax1dude.eaglercraft.v1_8.EaglercraftRandom;
import net.minecraft.block.BlockLever;
import net.minecraft.block.BlockTorch;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.EnumFacing.Plane;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class BlockTorchLever extends BlockTorch {
   public static final PropertyBool POWERED = BlockLever.POWERED;
   public static final PropertyDirection SIDE = PropertyDirection.create("side", Plane.HORIZONTAL);

   public BlockTorchLever() {
      this.setDefaultState(this.blockState.getBaseState().withProperty(FACING, EnumFacing.UP).withProperty(SIDE, EnumFacing.NORTH).withProperty(POWERED, Boolean.valueOf(false)));
      this.setLightLevel(0.9375F);
      this.setCreativeTab(CreativeTabs.REDSTONE);
   }

   protected BlockStateContainer createBlockState() {
      return new BlockStateContainer(this, new IProperty[]{FACING, POWERED, SIDE});
   }

   @SideOnly(Side.CLIENT)
   public void randomDisplayTick(IBlockState state, World world, BlockPos pos, EaglercraftRandom rand) {
      EnumFacing facing = (EnumFacing)state.getValue(FACING);
      double x = (double)pos.getX() + 0.5D;
      double y = (double)pos.getY() + 0.7D;
      double z = (double)pos.getZ() + 0.5D;
      if (facing.getAxis().isHorizontal()) {
         EnumFacing opposite = facing.getOpposite();
         int offsetX = opposite.getFrontOffsetX();
         int offsetZ = opposite.getFrontOffsetZ();
         if (((Boolean)state.getValue(POWERED)).booleanValue()) {
            world.spawnParticle(EnumParticleTypes.SMOKE_NORMAL, x + 0.1D * (double)offsetX, y + 0.08D, z + 0.1D * (double)offsetZ, 0.0D, 0.0D, 0.0D, new int[0]);
            world.spawnParticle(EnumParticleTypes.FLAME, x + 0.1D * (double)offsetX, y + 0.08D, z + 0.1D * (double)offsetZ, 0.0D, 0.0D, 0.0D, new int[0]);
         } else {
            world.spawnParticle(EnumParticleTypes.SMOKE_NORMAL, x + 0.27D * (double)offsetX, y + 0.22D, z + 0.27D * (double)offsetZ, 0.0D, 0.0D, 0.0D, new int[0]);
            world.spawnParticle(EnumParticleTypes.FLAME, x + 0.27D * (double)offsetX, y + 0.22D, z + 0.27D * (double)offsetZ, 0.0D, 0.0D, 0.0D, new int[0]);
         }
      } else if (((Boolean)state.getValue(POWERED)).booleanValue()) {
         EnumFacing side = ((EnumFacing)state.getValue(SIDE)).getOpposite();
         int offsetX = side.getFrontOffsetX();
         int offsetZ = side.getFrontOffsetZ();
         world.spawnParticle(EnumParticleTypes.SMOKE_NORMAL, x - 0.23D * (double)offsetX, y - 0.05D, z - 0.23D * (double)offsetZ, 0.0D, 0.0D, 0.0D, new int[0]);
         world.spawnParticle(EnumParticleTypes.FLAME, x - 0.23D * (double)offsetX, y - 0.05D, z - 0.23D * (double)offsetZ, 0.0D, 0.0D, 0.0D, new int[0]);
      } else {
         world.spawnParticle(EnumParticleTypes.SMOKE_NORMAL, x, y, z, 0.0D, 0.0D, 0.0D, new int[0]);
         world.spawnParticle(EnumParticleTypes.FLAME, x, y, z, 0.0D, 0.0D, 0.0D, new int[0]);
      }

   }

   public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, EnumFacing sideHit, float hitX, float hitY, float hitZ) {
      if (world.isRemote) {
         return true;
      } else {
         if (sideHit.getAxis().isVertical()) {
            sideHit = playerIn.getHorizontalFacing();
         } else {
            sideHit = sideHit.getOpposite();
         }

         state = state.cycleProperty(POWERED).withProperty(SIDE, sideHit);
         world.setBlockState(pos, state, 3);
         float pitch = ((Boolean)state.getValue(POWERED)).booleanValue() ? 0.6F : 0.5F;
         world.playSound((EntityPlayer)null, pos, SoundEvents.BLOCK_LEVER_CLICK, SoundCategory.BLOCKS, 0.3F, pitch);
         world.notifyNeighborsOfStateChange(pos, this, false);
         world.notifyNeighborsOfStateChange(pos.offset(((EnumFacing)state.getValue(FACING)).getOpposite()), this, false);
         return true;
      }
   }

   public void breakBlock(World world, BlockPos pos, IBlockState state) {
      if (((Boolean)state.getValue(POWERED)).booleanValue()) {
         world.notifyNeighborsOfStateChange(pos, this, false);
         world.notifyNeighborsOfStateChange(pos.offset(((EnumFacing)state.getValue(FACING)).getOpposite()), this, false);
      }

      super.breakBlock(world, pos, state);
   }

   /** @deprecated */
   @Deprecated
   public int getWeakPower(IBlockState blockState, IBlockAccess blockAccess, BlockPos pos, EnumFacing side) {
      return ((Boolean)blockState.getValue(POWERED)).booleanValue() ? 15 : 0;
   }

   /** @deprecated */
   @Deprecated
   public int getStrongPower(IBlockState state, IBlockAccess blockAccess, BlockPos pos, EnumFacing side) {
      if (!((Boolean)state.getValue(POWERED)).booleanValue()) {
         return 0;
      } else {
         return state.getValue(FACING) == side ? 15 : 0;
      }
   }

   /** @deprecated */
   @Deprecated
   public boolean canProvidePower(IBlockState state) {
      return true;
   }

   public IBlockState getStateFromMeta(int meta) {
      EnumFacing facing = EnumFacing.getHorizontal(meta & 3);
      IBlockState state = this.getDefaultState().withProperty(POWERED, Boolean.valueOf((meta & 8) > 1)).withProperty(SIDE, facing);
      if ((meta & 4) > 0) {
         facing = EnumFacing.UP;
      }

      return state.withProperty(FACING, facing);
   }

   public int getMetaFromState(IBlockState state) {
      int i = 0;
      EnumFacing facing = (EnumFacing)state.getValue(FACING);
      if (facing == EnumFacing.UP) {
         i = i | 4 + ((EnumFacing)state.getValue(SIDE)).getHorizontalIndex();
      } else {
         i = i | facing.getHorizontalIndex();
      }

      if (((Boolean)state.getValue(POWERED)).booleanValue()) {
         i |= 8;
      }

      return i;
   }
}
