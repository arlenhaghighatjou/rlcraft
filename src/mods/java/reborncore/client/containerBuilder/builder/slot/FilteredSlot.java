package reborncore.client.containerBuilder.builder.slot;

import java.util.function.Predicate;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

public class FilteredSlot extends Slot {
   private Predicate<ItemStack> filter;
   private int stackLimit = 64;

   public FilteredSlot(IInventory inventory, int index, int xPosition, int yPosition) {
      super(inventory, index, xPosition, yPosition);
   }

   public FilteredSlot(IInventory inventory, int index, int xPosition, int yPosition, int stackLimit) {
      super(inventory, index, xPosition, yPosition);
      this.stackLimit = stackLimit;
   }

   public FilteredSlot setFilter(Predicate<ItemStack> filter) {
      this.filter = filter;
      return this;
   }

   public boolean isItemValid(ItemStack stack) {
      try {
         return this.filter.test(stack);
      } catch (NullPointerException var3) {
         return true;
      }
   }

   public int getSlotStackLimit() {
      return this.stackLimit;
   }
}
