package knightminer.inspirations.utility.block;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMap.Builder;
import java.util.Locale;
import java.util.Random;
import javax.annotation.Nullable;
import knightminer.inspirations.library.Util;
import net.minecraft.block.BlockButton;
import net.minecraft.block.BlockHorizontal;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.Mirror;
import net.minecraft.util.Rotation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import slimeknights.mantle.block.EnumBlock;
import slimeknights.mantle.block.EnumBlock.IEnumMeta;

public class BlockBricksButton extends EnumBlock<BlockBricksButton.BrickType> {
   public static final PropertyEnum<BlockBricksButton.BrickType> TYPE = PropertyEnum.create("type", BlockBricksButton.BrickType.class);
   public static final PropertyDirection FACING = BlockHorizontal.FACING;
   public static final PropertyBool POWERED = BlockButton.POWERED;
   private static final ImmutableMap<EnumFacing, AxisAlignedBB> BRICK_BUTTON;
   private static final ImmutableMap<EnumFacing, AxisAlignedBB> NETHER_BUTTON;

   public BlockBricksButton() {
      super(Material.ROCK, TYPE, BlockBricksButton.BrickType.class);
      this.setTickRandomly(true);
      this.setCreativeTab(CreativeTabs.REDSTONE);
      this.setHardness(1.5F);
      this.setResistance(10.0F);
      this.setSoundType(SoundType.STONE);
      this.setDefaultState(this.blockState.getBaseState().withProperty(TYPE, BlockBricksButton.BrickType.BRICKS).withProperty(FACING, EnumFacing.NORTH).withProperty(POWERED, Boolean.valueOf(false)));
   }

   protected BlockStateContainer createBlockState() {
      return new BlockStateContainer(this, new IProperty[]{TYPE, FACING, POWERED});
   }

   public IBlockState getStateFromMeta(int meta) {
      return this.getDefaultState().withProperty(TYPE, BlockBricksButton.BrickType.fromMeta(meta & 1)).withProperty(FACING, EnumFacing.getHorizontal(meta >> 1)).withProperty(POWERED, Boolean.valueOf((meta & 8) > 0));
   }

   public int getMetaFromState(IBlockState state) {
      return ((BlockBricksButton.BrickType)state.getValue(TYPE)).getMeta() | ((EnumFacing)state.getValue(FACING)).getHorizontalIndex() << 1 | (((Boolean)state.getValue(POWERED)).booleanValue() ? 8 : 0);
   }

   /** @deprecated */
   @Deprecated
   public IBlockState getStateForPlacement(World worldIn, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer) {
      return this.getStateFromMeta(meta).withProperty(FACING, placer.getHorizontalFacing().getOpposite());
   }

   /** @deprecated */
   @Deprecated
   public IBlockState withRotation(IBlockState state, Rotation rot) {
      return state.withProperty(FACING, rot.rotate((EnumFacing)state.getValue(FACING)));
   }

   /** @deprecated */
   @Deprecated
   public IBlockState withMirror(IBlockState state, Mirror mirrorIn) {
      return state.withRotation(mirrorIn.toRotation((EnumFacing)state.getValue(FACING)));
   }

   public int damageDropped(IBlockState state) {
      return ((BlockBricksButton.BrickType)state.getValue(TYPE)).getMeta();
   }

   protected ItemStack getSilkTouchDrop(IBlockState state) {
      return new ItemStack(this, 1, ((BlockBricksButton.BrickType)state.getValue(TYPE)).getMeta());
   }

   public int tickRate(World worldIn) {
      return 20;
   }

   public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
      if (!Util.clickedAABB(getButtonBox(state), hitX, hitY, hitZ)) {
         return false;
      } else if (((Boolean)state.getValue(POWERED)).booleanValue()) {
         return true;
      } else {
         world.setBlockState(pos, state.withProperty(POWERED, Boolean.valueOf(true)), 3);
         world.markBlockRangeForRenderUpdate(pos, pos);
         world.playSound(player, pos, SoundEvents.BLOCK_STONE_BUTTON_CLICK_ON, SoundCategory.BLOCKS, 0.3F, 0.6F);
         world.notifyNeighborsOfStateChange(pos, this, false);
         world.scheduleUpdate(pos, this, this.tickRate(world));
         return true;
      }
   }

   public void randomTick(World worldIn, BlockPos pos, IBlockState state, Random random) {
   }

   public void updateTick(World world, BlockPos pos, IBlockState state, Random rand) {
      if (!world.isRemote) {
         if (((Boolean)state.getValue(POWERED)).booleanValue()) {
            world.setBlockState(pos, state.withProperty(POWERED, Boolean.valueOf(false)));
            world.notifyNeighborsOfStateChange(pos, this, false);
            world.playSound((EntityPlayer)null, pos, SoundEvents.BLOCK_STONE_BUTTON_CLICK_OFF, SoundCategory.BLOCKS, 0.3F, 0.5F);
            world.markBlockRangeForRenderUpdate(pos, pos);
         }

      }
   }

   private static AxisAlignedBB getButtonBox(IBlockState state) {
      EnumFacing facing = (EnumFacing)state.getValue(FACING);
      return state.getValue(TYPE) == BlockBricksButton.BrickType.BRICKS ? (AxisAlignedBB)BRICK_BUTTON.get(facing) : (AxisAlignedBB)NETHER_BUTTON.get(facing);
   }

   public void breakBlock(World world, BlockPos pos, IBlockState state) {
      if (((Boolean)state.getValue(POWERED)).booleanValue()) {
         world.notifyNeighborsOfStateChange(pos, this, false);
      }

      super.breakBlock(world, pos, state);
   }

   /** @deprecated */
   @Deprecated
   public int getWeakPower(IBlockState state, IBlockAccess blockAccess, BlockPos pos, EnumFacing side) {
      return ((Boolean)state.getValue(POWERED)).booleanValue() ? 15 : 0;
   }

   /** @deprecated */
   @Deprecated
   public int getStrongPower(IBlockState blockState, IBlockAccess blockAccess, BlockPos pos, EnumFacing side) {
      return 0;
   }

   /** @deprecated */
   @Deprecated
   public boolean canProvidePower(IBlockState state) {
      return true;
   }

   public boolean canConnectRedstone(IBlockState state, IBlockAccess world, BlockPos pos, @Nullable EnumFacing side) {
      return false;
   }

   static {
      Builder<EnumFacing, AxisAlignedBB> bounds = ImmutableMap.builder();
      bounds.put(EnumFacing.NORTH, new AxisAlignedBB(0.3125D, 0.3125D, 0.0D, 0.75D, 0.5D, 0.0625D));
      bounds.put(EnumFacing.SOUTH, new AxisAlignedBB(0.25D, 0.3125D, 0.9375D, 0.6875D, 0.5D, 1.0D));
      bounds.put(EnumFacing.WEST, new AxisAlignedBB(0.0D, 0.3125D, 0.25D, 0.0625D, 0.5D, 0.6875D));
      bounds.put(EnumFacing.EAST, new AxisAlignedBB(0.9375D, 0.3125D, 0.3125D, 1.0D, 0.5D, 0.75D));
      BRICK_BUTTON = bounds.build();
      bounds = ImmutableMap.builder();
      bounds.put(EnumFacing.NORTH, new AxisAlignedBB(0.375D, 0.5D, 0.0D, 0.8125D, 0.6875D, 0.0625D));
      bounds.put(EnumFacing.SOUTH, new AxisAlignedBB(0.1875D, 0.5D, 0.9375D, 0.625D, 0.6875D, 1.0D));
      bounds.put(EnumFacing.WEST, new AxisAlignedBB(0.0D, 0.5D, 0.1875D, 0.0625D, 0.6875D, 0.625D));
      bounds.put(EnumFacing.EAST, new AxisAlignedBB(0.9375D, 0.5D, 0.375D, 1.0D, 0.6875D, 0.8125D));
      NETHER_BUTTON = bounds.build();
   }

   public static enum BrickType implements IStringSerializable, IEnumMeta {
      BRICKS,
      NETHER;

      private int meta = this.ordinal();

      private BrickType() {
      }

      public int getMeta() {
         return this.meta;
      }

      public String getName() {
         return this.name().toLowerCase(Locale.US);
      }

      public static BlockBricksButton.BrickType fromMeta(int i) {
         if (i < 0 || i > values().length) {
            i = 0;
         }

         return values()[i];
      }
   }
}
