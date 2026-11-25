package shadows.placebo.block;

import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.block.state.BlockStateContainer.Builder;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraftforge.client.event.ModelRegistryEvent;
import shadows.placebo.Placebo;
import shadows.placebo.interfaces.IPropertyEnum;
import shadows.placebo.itemblock.ItemBlockEnum;
import shadows.placebo.registry.RegistryInformation;
import shadows.placebo.util.PlaceboUtil;

public abstract class BlockEnum<E extends Enum<E> & IPropertyEnum> extends BlockBasic implements IEnumBlock<E> {
   protected final List<E> types;
   protected final Predicate<E> valueFilter;
   protected final PropertyEnum<E> property;
   protected final BlockStateContainer realStateContainer;

   public BlockEnum(String name, Material material, SoundType sound, float hardness, float resistance, Class<E> enumClass, String propName, Predicate<E> valueFilter, RegistryInformation info) {
      super(name, material, hardness, resistance, info);
      this.types = new ArrayList();
      this.setSoundType(sound);
      this.valueFilter = valueFilter;
      this.property = PropertyEnum.create(propName, enumClass, valueFilter);
      this.types.addAll(this.property.getAllowedValues());
      this.realStateContainer = this.createStateContainer();
      this.setDefaultState(this.getBlockState().getBaseState());

      for(E e : this.types) {
         ((IPropertyEnum)e).set(this);
      }

   }

   public BlockEnum(String name, Material material, SoundType sound, float hardness, float resistance, Class<E> enumClass, String propName, RegistryInformation info) {
      this(name, material, sound, hardness, resistance, enumClass, propName, Predicates.alwaysTrue(), info);
   }

   public BlockEnum(String name, Material material, SoundType sound, float hardness, float resistance, Class<E> enumClass, RegistryInformation info) {
      this(name, material, sound, hardness, resistance, enumClass, "type", info);
   }

   public ItemBlock createItemBlock() {
      return new ItemBlockEnum(this);
   }

   public void initModels(ModelRegistryEvent e) {
      for(int i = 0; i < this.types.size(); ++i) {
         PlaceboUtil.sMRL("blocks", this, i, "type=" + ((IPropertyEnum)((Enum)this.types.get(i))).getName());
      }

      Placebo.PROXY.useRenamedMapper(this, "blocks");
   }

   public final BlockStateContainer createBlockState() {
      return (new Builder(this)).build();
   }

   public final BlockStateContainer getBlockState() {
      return this.realStateContainer;
   }

   public IBlockState getStateFromMeta(int meta) {
      return this.getDefaultState().withProperty(this.property, (Comparable)this.types.get(meta));
   }

   public int getMetaFromState(IBlockState state) {
      return ((Enum)state.getValue(this.property)).ordinal();
   }

   public int damageDropped(IBlockState state) {
      return this.getMetaFromState(state);
   }

   public void getSubBlocks(CreativeTabs tab, NonNullList<ItemStack> list) {
      for(int i = 0; i < this.types.size(); ++i) {
         list.add(new ItemStack(this, 1, i));
      }

   }

   public BlockStateContainer createStateContainer() {
      return new BlockStateContainer(this, new IProperty[]{this.property});
   }

   public List<E> getTypes() {
      return this.types;
   }

   public PropertyEnum<E> getProperty() {
      return this.property;
   }

   public BlockStateContainer getRealStateContainer() {
      return this.realStateContainer;
   }

   public IBlockState getStateFor(E e) {
      return this.getDefaultState().withProperty(this.property, e);
   }
}
