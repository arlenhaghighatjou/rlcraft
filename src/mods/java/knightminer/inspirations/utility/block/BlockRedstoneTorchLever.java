package knightminer.inspirations.utility.block;

import net.lax1dude.eaglercraft.v1_8.EaglercraftRandom;
import knightminer.inspirations.utility.InspirationsUtility;
import net.minecraft.block.Block;
import net.minecraft.block.BlockLever;
import net.minecraft.block.BlockRedstoneWire;
import net.minecraft.block.BlockLever.EnumOrientation;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class BlockRedstoneTorchLever extends BlockLever {
   private boolean powered;

   public BlockRedstoneTorchLever(boolean powered) {
      this.setTickRandomly(true);
      this.powered = powered;
      if (powered) {
         this.setLightLevel(0.5F);
      }

   }

   public Item getItemDropped(IBlockState state, EaglercraftRandom rand, int fortune) {
      return Item.getItemFromBlock(InspirationsUtility.redstoneTorchLever);
   }

   public ItemStack getPickBlock(IBlockState state, RayTraceResult target, World world, BlockPos pos, EntityPlayer player) {
      return new ItemStack(InspirationsUtility.redstoneTorchLever);
   }

   public int getWeakPower(IBlockState state, IBlockAccess blockAccess, BlockPos pos, EnumFacing side) {
      return this.powered && ((EnumOrientation)state.getValue(FACING)).getFacing() == side ? 15 : 0;
   }

   public int getStrongPower(IBlockState blockState, IBlockAccess blockAccess, BlockPos pos, EnumFacing side) {
      return this.getWeakPower(blockState, blockAccess, pos, side);
   }

   public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, EnumFacing side, float hitX, float hitY, float hitZ) {
      if (world.isRemote) {
         return true;
      } else {
         state = this.togglePower(state);
         Block block = state.getBlock();
         world.setBlockState(pos, state, 3);
         world.playSound((EntityPlayer)null, pos, SoundEvents.BLOCK_LEVER_CLICK, SoundCategory.BLOCKS, 0.3F, block == InspirationsUtility.redstoneTorchLeverPowered ? 0.6F : 0.5F);
         world.notifyNeighborsOfStateChange(pos, block, false);
         world.notifyNeighborsOfStateChange(pos.offset(((EnumOrientation)state.getValue(FACING)).getFacing().getOpposite()), block, false);
         return true;
      }
   }

   public void breakBlock(World worldIn, BlockPos pos, IBlockState state) {
      if (this.powered) {
         worldIn.notifyNeighborsOfStateChange(pos, this, false);
         EnumFacing enumfacing = ((EnumOrientation)state.getValue(FACING)).getFacing();
         worldIn.notifyNeighborsOfStateChange(pos.offset(enumfacing.getOpposite()), this, false);
      }

      super.breakBlock(worldIn, pos, state);
   }

   public IBlockState getStateForPlacement(World world, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer) {
      IBlockState state = super.getStateForPlacement(world, pos, facing, hitX, hitY, hitZ, meta, placer);
      return state.withProperty(POWERED, Boolean.valueOf(this.isBlockPowered(state, world, pos)));
   }

   public void neighborChanged(IBlockState state, World world, BlockPos pos, Block block, BlockPos fromPos) {
      super.neighborChanged(state, world, pos, block, fromPos);
      if (world.getBlockState(pos).getBlock() == this) {
         boolean powered = this.isBlockPowered(state, world, pos);
         if (powered != ((Boolean)state.getValue(POWERED)).booleanValue()) {
            world.setBlockState(pos, state.withProperty(POWERED, Boolean.valueOf(powered)), 16);
            if (powered) {
               world.scheduleUpdate(pos, this, this.tickRate(world));
            }
         }

      }
   }

   public int tickRate(World worldIn) {
      return 2;
   }

   public void randomTick(World world, BlockPos pos, IBlockState state, EaglercraftRandom random) {
   }

   public void updateTick(World world, BlockPos pos, IBlockState state, EaglercraftRandom rand) {
      IBlockState newState = this.togglePower(state).withProperty(POWERED, Boolean.valueOf(this.isBlockPowered(state, world, pos)));
      world.setBlockState(pos, newState);
      EnumFacing enumfacing = ((EnumOrientation)state.getValue(FACING)).getFacing();
      world.notifyNeighborsOfStateChange(pos.offset(enumfacing.getOpposite()), this, false);
      world.playSound((EntityPlayer)null, pos, SoundEvents.BLOCK_LEVER_CLICK, SoundCategory.BLOCKS, 0.3F, newState.getBlock() == InspirationsUtility.redstoneTorchLeverPowered ? 0.6F : 0.5F);
   }

   @SideOnly(Side.CLIENT)
   public void randomDisplayTick(IBlockState state, World world, BlockPos pos, EaglercraftRandom rand) {
      if (this.powered) {
         EnumOrientation orientation = (EnumOrientation)state.getValue(FACING);
         EnumFacing facing = orientation.getFacing();
         double x = (double)pos.getX() + 0.5D;
         double y = (double)pos.getY() + 0.7D;
         double z = (double)pos.getZ() + 0.5D;
         if (facing.getAxis().isHorizontal()) {
            y = y - 0.4D;
            world.spawnParticle(EnumParticleTypes.REDSTONE, x, y, z, 0.0D, 0.0D, 0.0D, new int[0]);
         } else {
            switch(orientation) {
            case UP_X:
            case DOWN_X:
               x -= 0.4D;
               break;
            case UP_Z:
            case DOWN_Z:
               z -= 0.4D;
            }

            y = y - 0.05D * (double)facing.getFrontOffsetY();
            world.spawnParticle(EnumParticleTypes.REDSTONE, x, y, z, 0.0D, 0.0D, 0.0D, new int[0]);
         }

      }
   }

   @SideOnly(Side.CLIENT)
   public BlockRenderLayer getBlockLayer() {
      return BlockRenderLayer.CUTOUT;
   }

   protected IBlockState togglePower(IBlockState state) {
      EnumOrientation orientation = (EnumOrientation)state.getValue(FACING);
      boolean powered = ((Boolean)state.getValue(POWERED)).booleanValue();
      Block block = state.getBlock() == InspirationsUtility.redstoneTorchLever ? InspirationsUtility.redstoneTorchLeverPowered : InspirationsUtility.redstoneTorchLever;
      return block.getDefaultState().withProperty(FACING, orientation).withProperty(POWERED, Boolean.valueOf(powered));
   }

   protected boolean isBlockPowered(IBlockState state, World world, BlockPos pos) {
      EnumFacing facing = ((EnumOrientation)state.getValue(FACING)).getFacing().getOpposite();

      for(EnumFacing side : EnumFacing.values()) {
         if (side != facing) {
            BlockPos offset = pos.offset(side);
            IBlockState offsetState = world.getBlockState(offset);
            if (offsetState.getBlock() == Blocks.REDSTONE_WIRE) {
               if (((Integer)offsetState.getValue(BlockRedstoneWire.POWER)).intValue() > 0) {
                  return true;
               }
            } else if (world.getRedstonePower(offset, side) > 0) {
               return true;
            }
         }
      }

      return false;
   }
}
