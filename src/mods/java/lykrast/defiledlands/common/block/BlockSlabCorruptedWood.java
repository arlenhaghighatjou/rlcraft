package lykrast.defiledlands.common.block;

import java.util.Random;
import net.minecraft.block.SoundType;
import net.minecraft.block.BlockSlab.EnumBlockHalf;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public abstract class BlockSlabCorruptedWood extends BlockSlabCorrupted implements ICustomItemBlock {
   public static final PropertyEnum<BlockSlabCorruptedWood.Variant> VARIANT = PropertyEnum.create("variant", BlockSlabCorruptedWood.Variant.class);

   public BlockSlabCorruptedWood() {
      super(Material.WOOD);
      this.setSoundType(SoundType.WOOD);
      this.setHardness(2.0F);
      this.setResistance(10.0F);
      this.setHarvestLevel("axe", 0);
      IBlockState iblockstate = this.blockState.getBaseState();
      if (!this.isDouble()) {
         iblockstate = iblockstate.withProperty(HALF, EnumBlockHalf.BOTTOM);
      }

      this.setDefaultState(iblockstate.withProperty(VARIANT, BlockSlabCorruptedWood.Variant.TENEBRA));
   }

   public Item getItemDropped(IBlockState state, Random rand, int fortune) {
      return Item.getItemFromBlock(this);
   }

   public ItemStack getItem(World worldIn, BlockPos pos, IBlockState state) {
      return new ItemStack(this, 1, ((BlockSlabCorruptedWood.Variant)state.getValue(VARIANT)).ordinal());
   }

   public String getUnlocalizedName(int meta) {
      return super.getUnlocalizedName() + "." + BlockSlabCorruptedWood.Variant.values()[meta].getName();
   }

   public IProperty<?> getVariantProperty() {
      return VARIANT;
   }

   public Comparable<?> getTypeForItem(ItemStack stack) {
      return BlockSlabCorruptedWood.Variant.values()[stack.getMetadata() & 7];
   }

   public void getSubBlocks(CreativeTabs itemIn, NonNullList<ItemStack> items) {
      for(BlockSlabCorruptedWood.Variant v : BlockSlabCorruptedWood.Variant.values()) {
         items.add(new ItemStack(this, 1, v.ordinal()));
      }

   }

   public IBlockState getStateFromMeta(int meta) {
      IBlockState iblockstate = this.getDefaultState().withProperty(VARIANT, BlockSlabCorruptedWood.Variant.values()[meta & 7]);
      if (!this.isDouble()) {
         iblockstate = iblockstate.withProperty(HALF, (meta & 8) == 0 ? EnumBlockHalf.BOTTOM : EnumBlockHalf.TOP);
      }

      return iblockstate;
   }

   public int getMetaFromState(IBlockState state) {
      int i = 0;
      i = i | ((BlockSlabCorruptedWood.Variant)state.getValue(VARIANT)).ordinal();
      if (!this.isDouble() && state.getValue(HALF) == EnumBlockHalf.TOP) {
         i |= 8;
      }

      return i;
   }

   protected BlockStateContainer createBlockState() {
      return this.isDouble() ? new BlockStateContainer(this, new IProperty[]{VARIANT}) : new BlockStateContainer(this, new IProperty[]{HALF, VARIANT});
   }

   public int damageDropped(IBlockState state) {
      return ((BlockSlabCorruptedWood.Variant)state.getValue(VARIANT)).ordinal();
   }

   public static enum Variant implements IStringSerializable {
      TENEBRA;

      private Variant() {
      }

      public String getName() {
         return this.name().toLowerCase();
      }

      public String toString() {
         return this.getName();
      }
   }
}
