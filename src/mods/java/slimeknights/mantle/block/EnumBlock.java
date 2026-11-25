package slimeknights.mantle.block;

import javax.annotation.Nonnull;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.NonNullList;

public class EnumBlock<E extends Enum<E> & EnumBlock.IEnumMeta & IStringSerializable> extends Block {
   public final PropertyEnum<E> prop;
   private final E[] values;
   protected static PropertyEnum<?> tmp;

   public EnumBlock(Material material, PropertyEnum<E> prop, Class<E> clazz) {
      super(preInit(material, prop));
      this.prop = prop;
      this.values = clazz.getEnumConstants();
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
      return this.prop == null ? new BlockStateContainer(this, new IProperty[]{tmp}) : new BlockStateContainer(this, new IProperty[]{this.prop});
   }

   @Nonnull
   public IBlockState getStateFromMeta(int meta) {
      return this.getDefaultState().withProperty(this.prop, this.fromMeta(meta));
   }

   public int getMetaFromState(IBlockState state) {
      return ((EnumBlock.IEnumMeta)((Enum)state.getValue(this.prop))).getMeta();
   }

   public int damageDropped(IBlockState state) {
      return this.getMetaFromState(state);
   }

   protected E fromMeta(int meta) {
      if (meta < 0 || meta >= this.values.length) {
         meta = 0;
      }

      return (E)this.values[meta];
   }

   public interface IEnumMeta {
      int getMeta();
   }
}
