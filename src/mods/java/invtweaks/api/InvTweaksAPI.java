package invtweaks.api;

import invtweaks.api.container.ContainerSection;
import javax.annotation.Nonnull;
import net.minecraft.item.ItemStack;

public interface InvTweaksAPI {
   void addOnLoadListener(IItemTreeListener var1);

   boolean removeOnLoadListener(IItemTreeListener var1);

   void setSortKeyEnabled(boolean var1);

   void setTextboxMode(boolean var1);

   int compareItems(@Nonnull ItemStack var1, @Nonnull ItemStack var2);

   int compareItems(@Nonnull ItemStack var1, @Nonnull ItemStack var2, boolean var3);

   void sort(ContainerSection var1, SortingMethod var2);
}
