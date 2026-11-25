package paulevs.betternether.blocks;

import net.lax1dude.eaglercraft.v1_8.EaglercraftRandom;
import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class BlockBrownLargeMushroom extends Block {
   private static final BlockBrownLargeMushroom.EnumShape[] SHAPES = BlockBrownLargeMushroom.EnumShape.values();
   public static final PropertyEnum<BlockBrownLargeMushroom.EnumShape> SHAPE = PropertyEnum.create("shape", BlockBrownLargeMushroom.EnumShape.class);
   private static final AxisAlignedBB BOTTOM_AABB = new AxisAlignedBB(0.25D, 0.0D, 0.25D, 0.75D, 1.0D, 0.75D);
   private static final AxisAlignedBB TOP_CENTER_AABB = new AxisAlignedBB(0.0D, 0.0D, 0.0D, 1.0D, 1.0D, 1.0D);
   private static final AxisAlignedBB TOP_SIDE_AABB = new AxisAlignedBB(0.0D, 0.5D, 0.0D, 1.0D, 1.0D, 1.0D);

   public BlockBrownLargeMushroom() {
      super(Material.WOOD, MapColor.BROWN_STAINED_HARDENED_CLAY);
      this.setRegistryName("brown_large_mushroom");
      this.setUnlocalizedName("brown_large_mushroom");
      this.setHardness(0.25F);
      this.setSoundType(SoundType.WOOD);
      this.setDefaultState(this.blockState.getBaseState().withProperty(SHAPE, BlockBrownLargeMushroom.EnumShape.BOTTOM));
   }

   public IBlockState getStateFromMeta(int meta) {
      return this.getDefaultState().withProperty(SHAPE, SHAPES[meta]);
   }

   public int getMetaFromState(IBlockState state) {
      return ((BlockBrownLargeMushroom.EnumShape)state.getValue(SHAPE)).getIndex();
   }

   protected BlockStateContainer createBlockState() {
      return new BlockStateContainer(this, new IProperty[]{SHAPE});
   }

   public boolean isOpaqueCube(IBlockState state) {
      return false;
   }

   public boolean isFullCube(IBlockState state) {
      return false;
   }

   public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
      if (state.getValue(SHAPE) == BlockBrownLargeMushroom.EnumShape.TOP) {
         return TOP_CENTER_AABB;
      } else {
         return !((BlockBrownLargeMushroom.EnumShape)state.getValue(SHAPE)).name.contains("side") && !((BlockBrownLargeMushroom.EnumShape)state.getValue(SHAPE)).name.contains("corner") ? BOTTOM_AABB : TOP_SIDE_AABB;
      }
   }

   public BlockFaceShape getBlockFaceShape(IBlockAccess worldIn, IBlockState state, BlockPos pos, EnumFacing face) {
      if (state.getValue(SHAPE) != BlockBrownLargeMushroom.EnumShape.TOP && !((BlockBrownLargeMushroom.EnumShape)state.getValue(SHAPE)).name.contains("side") && !((BlockBrownLargeMushroom.EnumShape)state.getValue(SHAPE)).name.contains("corner")) {
         return face != EnumFacing.DOWN && face != EnumFacing.UP ? BlockFaceShape.UNDEFINED : BlockFaceShape.MIDDLE_POLE;
      } else {
         return face == EnumFacing.UP ? BlockFaceShape.SOLID : BlockFaceShape.UNDEFINED;
      }
   }

   public Item getItemDropped(IBlockState state, EaglercraftRandom rand, int fortune) {
      return Item.getItemFromBlock(Blocks.BROWN_MUSHROOM);
   }

   public int quantityDropped(EaglercraftRandom random) {
      return random.nextInt(3);
   }

   public ItemStack getItem(World worldIn, BlockPos pos, IBlockState state) {
      return new ItemStack(Blocks.BROWN_MUSHROOM);
   }

   @SideOnly(Side.CLIENT)
   public BlockRenderLayer getBlockLayer() {
      return BlockRenderLayer.SOLID;
   }

   public static enum EnumShape implements IStringSerializable {
      TOP("top"),
      SIDE_N("side_n"),
      SIDE_S("side_s"),
      SIDE_E("side_e"),
      SIDE_W("side_w"),
      CORNER_N("corner_n"),
      CORNER_S("corner_s"),
      CORNER_E("corner_e"),
      CORNER_W("corner_w"),
      MIDDLE("middle"),
      BOTTOM("bottom");

      private final String name;
      private final int index;

      private EnumShape(String name) {
         this.name = name;
         this.index = this.ordinal();
      }

      public int getIndex() {
         return this.index;
      }

      public String toString() {
         return this.name;
      }

      public String getName() {
         return this.name;
      }
   }
}
