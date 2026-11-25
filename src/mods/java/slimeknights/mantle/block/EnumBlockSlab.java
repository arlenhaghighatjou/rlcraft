package slimeknights.mantle.block;

import javax.annotation.Nonnull;
import net.minecraft.block.BlockSlab;
import net.minecraft.block.BlockSlab.EnumBlockHalf;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.NonNullList;

public abstract class EnumBlockSlab<E extends Enum<E> & EnumBlock.IEnumMeta & IStringSerializable> extends BlockSlab {
   public final PropertyEnum<E> prop;
   private final E[] values;
   private static PropertyEnum<?> tmp;

   public EnumBlockSlab(Material material, PropertyEnum<E> prop, Class<E> clazz) {
      super(preInit(material, prop));
      this.prop = prop;
      this.values = clazz.getEnumConstants();
      this.setDefaultState(this.blockState.getBaseState().withProperty(BlockSlab.HALF, EnumBlockHalf.BOTTOM));
      this.useNeighborBrightness = true;
   }

   private static Material preInit(Material material, PropertyEnum<?> property) {
      tmp = property;
      return material;
   }

   public void getSubBlocks(CreativeTabs tab, NonNullList<ItemStack> list) {
      for(E type : this.values) {
         list.add(new ItemStack(this, 1, ((EnumBlock.IEnumMeta)type).getMeta()));
      }

   }

   @Nonnull
   protected BlockStateContainer createBlockState() {
      return this.prop == null ? new BlockStateContainer(this, new IProperty[]{HALF, tmp}) : new BlockStateContainer(this, new IProperty[]{HALF, this.prop});
   }

   @Nonnull
   public IBlockState getStateFromMeta(int meta) {
      return this.getDefaultState().withProperty(this.prop, this.fromMeta(meta & 7)).withProperty(HALF, (meta & 8) == 0 ? EnumBlockHalf.BOTTOM : EnumBlockHalf.TOP);
   }

   public int getMetaFromState(IBlockState state) {
      int i = 0;
      i = i | ((EnumBlock.IEnumMeta)((Enum)state.getValue(this.prop))).getMeta();
      if (state.getValue(HALF) == EnumBlockHalf.TOP) {
         i |= 8;
      }

      return i;
   }

   public int damageDropped(IBlockState state) {
      return ((EnumBlock.IEnumMeta)((Enum)state.getValue(this.prop))).getMeta();
   }

   protected E fromMeta(int meta) {
      if (meta < 0 || meta >= this.values.length) {
         meta = 0;
      }

      return (E)this.values[meta];
   }

   @Nonnull
   public IProperty<E> getVariantProperty() {
      return this.prop;
   }

   @Nonnull
   public Comparable<?> getTypeForItem(@Nonnull ItemStack stack) {
      return this.fromMeta(stack.getItemDamage() & 7);
   }

   @Nonnull
   public String getUnlocalizedName(int meta) {
      return super.getUnlocalizedName() + "." + ((IStringSerializable)this.fromMeta(meta & 7)).getName();
   }

   public abstract IBlockState getFullBlock(IBlockState var1);

   public boolean isDouble() {
      return false;
   }
}
