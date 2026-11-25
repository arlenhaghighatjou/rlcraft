package paulevs.betternether.blocks;

import java.util.Random;
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

public class BlockRedLargeMushroom extends Block {
   private static final BlockRedLargeMushroom.EnumShape[] SHAPES = BlockRedLargeMushroom.EnumShape.values();
   public static final PropertyEnum<BlockRedLargeMushroom.EnumShape> SHAPE = PropertyEnum.create("shape", BlockRedLargeMushroom.EnumShape.class);
   private static final AxisAlignedBB BOTTOM_AABB = new AxisAlignedBB(0.3125D, 0.0D, 0.3125D, 0.6875D, 1.0D, 0.6875D);
   private static final AxisAlignedBB TOP_AABB = new AxisAlignedBB(0.0D, 0.0D, 0.0D, 1.0D, 1.0D, 1.0D);

   public BlockRedLargeMushroom() {
      super(Material.WOOD, MapColor.RED_STAINED_HARDENED_CLAY);
      this.setRegistryName("red_large_mushroom");
      this.setUnlocalizedName("red_large_mushroom");
      this.setHardness(0.25F);
      this.setSoundType(SoundType.WOOD);
      this.setDefaultState(this.blockState.getBaseState().withProperty(SHAPE, BlockRedLargeMushroom.EnumShape.BOTTOM));
   }

   public IBlockState getStateFromMeta(int meta) {
      return this.getDefaultState().withProperty(SHAPE, SHAPES[meta]);
   }

   public int getMetaFromState(IBlockState state) {
      return ((BlockRedLargeMushroom.EnumShape)state.getValue(SHAPE)).getIndex();
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
      return state.getValue(SHAPE) == BlockRedLargeMushroom.EnumShape.TOP ? TOP_AABB : BOTTOM_AABB;
   }

   public BlockFaceShape getBlockFaceShape(IBlockAccess worldIn, IBlockState state, BlockPos pos, EnumFacing face) {
      return face != EnumFacing.DOWN && face != EnumFacing.UP ? BlockFaceShape.UNDEFINED : BlockFaceShape.MIDDLE_POLE;
   }

   public Item getItemDropped(IBlockState state, Random rand, int fortune) {
      return Item.getItemFromBlock(Blocks.RED_MUSHROOM);
   }

   public int quantityDropped(Random random) {
      return random.nextInt(3);
   }

   public ItemStack getItem(World worldIn, BlockPos pos, IBlockState state) {
      return new ItemStack(Blocks.RED_MUSHROOM);
   }

   @SideOnly(Side.CLIENT)
   public BlockRenderLayer getBlockLayer() {
      return BlockRenderLayer.CUTOUT;
   }

   public static enum EnumShape implements IStringSerializable {
      TOP("top"),
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
