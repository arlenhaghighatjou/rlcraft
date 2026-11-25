package gigaherz.toolbelt.shadow.commons.state;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import net.minecraft.block.properties.IProperty;
import net.minecraft.item.ItemStack;

public interface IItemState {
   int getMetadata();

   ItemStack getStack();

   ItemStack getStack(int var1);

   <T extends Comparable<T>> IItemState withProperty(IProperty<T> var1, T var2);

   <T extends Comparable<T>> T getValue(IProperty<T> var1);

   ImmutableList<Comparable> getValues();

   ImmutableMap<IProperty<?>, Comparable<?>> getProperties();
}
