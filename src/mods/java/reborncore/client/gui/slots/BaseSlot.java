package reborncore.client.gui.slots;

import java.util.function.Predicate;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

public class BaseSlot extends Slot {
   private Predicate<ItemStack> filter = (stack) -> {
      return true;
   };

   public BaseSlot(IInventory inventoryIn, int index, int xPosition, int yPosition) {
      super(inventoryIn, index, xPosition, yPosition);
   }

   public BaseSlot(IInventory inventoryIn, int index, int xPosition, int yPosition, Predicate<ItemStack> filter) {
      super(inventoryIn, index, xPosition, yPosition);
      this.filter = filter;
   }

   public boolean canWorldBlockRemove() {
      return true;
   }

   public boolean isItemValid(ItemStack stack) {
      return this.filter.test(stack);
   }

   public boolean canWorldBlockInsert() {
      return true;
   }
}
