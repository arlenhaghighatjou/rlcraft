package gigaherz.toolbelt.shadow.commons.state;

import com.google.common.collect.ImmutableList;
import javax.annotation.Nullable;
import net.minecraft.block.properties.IProperty;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public interface IItemStateManager {
   @Nullable
   IItemState get(int var1);

   Item getItem();

   ImmutableList<IProperty> getProperties();

   IItemState getDefaultState();

   void setDefaultState(IItemState var1);

   ImmutableList<IItemState> getStateTable();

   @Nullable
   static IItemState lookup(ItemStack stack) {
      Item item = stack.getItem();
      if (!(item instanceof StatefulItem)) {
         throw new IllegalArgumentException("The stack represents a non-stateful item");
      } else {
         IItemStateManager stateData = ((StatefulItem)item).getStateManager();
         return stateData.get(stack.getMetadata());
      }
   }
}
