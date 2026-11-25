package vazkii.quark.world.block;

import java.util.Locale;
import java.util.Random;
import javax.annotation.Nonnull;
import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import vazkii.arl.block.BlockMod;
import vazkii.quark.base.block.IQuarkBlock;

public class BlockSpeleothem extends BlockMod implements IQuarkBlock {
   public static final PropertyEnum<BlockSpeleothem.EnumSize> SIZE = PropertyEnum.create("size", BlockSpeleothem.EnumSize.class);

   public BlockSpeleothem(String name) {
      super(name + "_speleothem", Material.ROCK, new String[0]);
      this.setHardness(1.5F);
      this.setSoundType(SoundType.STONE);
      this.setCreativeTab(CreativeTabs.DECORATIONS);
      this.setDefaultState(this.blockState.getBaseState().withProperty(SIZE, BlockSpeleothem.EnumSize.MEDIUM));
   }

   public BlockSpeleothem setNetherrack() {
      this.setHardness(0.4F);
      return this;
   }

   public boolean canPlaceBlockAt(World worldIn, @Nonnull BlockPos pos) {
      return this.getBearing(worldIn, pos) > 0;
   }

   public void onBlockPlacedBy(World worldIn, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack) {
      BlockSpeleothem.EnumSize size = BlockSpeleothem.EnumSize.values()[Math.max(0, this.getBearing(worldIn, pos) - 1)];
      worldIn.setBlockState(pos, state.withProperty(SIZE, size));
   }

   public void neighborChanged(IBlockState state, World worldIn, BlockPos pos, Block blockIn, BlockPos fromPos) {
      int size = ((BlockSpeleothem.EnumSize)state.getValue(SIZE)).strength;
      if (this.getBearing(worldIn, pos) < size + 1) {
         worldIn.playEvent(2001, pos, Block.getStateId(worldIn.getBlockState(pos)));
         this.dropBlockAsItem(worldIn, pos, state, 0);
         worldIn.setBlockToAir(pos);
      }

   }

   public int quantityDropped(Random random) {
      return 0;
   }

   public boolean canSilkHarvest(World world, BlockPos pos, @Nonnull IBlockState state, EntityPlayer player) {
      return true;
   }

   public boolean isPassable(IBlockAccess worldIn, BlockPos pos) {
      return false;
   }

   private int getBearing(IBlockAccess world, BlockPos pos) {
      return Math.max(this.getStrength(world, pos.down()), this.getStrength(world, pos.up()));
   }

   private int getStrength(IBlockAccess world, BlockPos pos) {
      IBlockState state = world.getBlockState(pos);
      if (state.isFullBlock()) {
         return 3;
      } else {
         return state.getPropertyKeys().contains(SIZE) ? ((BlockSpeleothem.EnumSize)state.getValue(SIZE)).strength : 0;
      }
   }

   @Nonnull
   public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
      return ((BlockSpeleothem.EnumSize)state.getValue(SIZE)).aabb;
   }

   public AxisAlignedBB getCollisionBoundingBox(IBlockState blockState, @Nonnull IBlockAccess worldIn, @Nonnull BlockPos pos) {
      return this.getBoundingBox(blockState, worldIn, pos);
   }

   public boolean isFullCube(IBlockState state) {
      return false;
   }

   public boolean isFullBlock(IBlockState state) {
      return false;
   }

   public boolean isOpaqueCube(IBlockState state) {
      return false;
   }

   @Nonnull
   public BlockFaceShape getBlockFaceShape(IBlockAccess worldIn, IBlockState state, BlockPos blockPos, EnumFacing face) {
      return BlockFaceShape.UNDEFINED;
   }

   public boolean canPlaceTorchOnTop(@Nonnull IBlockState state, @Nonnull IBlockAccess world, @Nonnull BlockPos pos) {
      return true;
   }

   @Nonnull
   protected BlockStateContainer createBlockState() {
      return new BlockStateContainer(this, new IProperty[]{SIZE});
   }

   public int getMetaFromState(IBlockState state) {
      return ((BlockSpeleothem.EnumSize)state.getValue(SIZE)).ordinal();
   }

   @Nonnull
   public IBlockState getStateFromMeta(int meta) {
      return this.getDefaultState().withProperty(SIZE, BlockSpeleothem.EnumSize.values()[Math.min(BlockSpeleothem.EnumSize.values().length - 1, meta)]);
   }

   public static enum EnumSize implements IStringSerializable {
      SMALL(0, 2),
      MEDIUM(1, 4),
      BIG(2, 8);

      public final int strength;
      public final AxisAlignedBB aabb;

      private EnumSize(int strength, int width) {
         this.strength = strength;
         float pad = (float)(16 - width) / 2.0F / 16.0F;
         this.aabb = new AxisAlignedBB((double)pad, 0.0D, (double)pad, (double)(1.0F - pad), 1.0D, (double)(1.0F - pad));
      }

      public String getName() {
         return this.name().toLowerCase(Locale.ROOT);
      }
   }
}
