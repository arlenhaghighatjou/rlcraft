package mezz.jei.api;

import com.google.common.collect.ImmutableList;
import java.util.Collection;
import javax.annotation.Nullable;
import net.minecraft.item.ItemStack;

/** @deprecated */
@Deprecated
public interface IItemListOverlay {
   /** @deprecated */
   @Deprecated
   boolean hasKeyboardFocus();

   /** @deprecated */
   @Nullable
   @Deprecated
   ItemStack getStackUnderMouse();

   /** @deprecated */
   @Deprecated
   void setFilterText(String var1);

   /** @deprecated */
   @Deprecated
   String getFilterText();

   /** @deprecated */
   @Deprecated
   ImmutableList<ItemStack> getFilteredStacks();

   /** @deprecated */
   @Deprecated
   ImmutableList<ItemStack> getVisibleStacks();

   /** @deprecated */
   @Deprecated
   void highlightStacks(Collection<ItemStack> var1);
}
