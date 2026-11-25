package slimeknights.mantle.inventory;

import javax.annotation.Nonnull;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class SlotRestrictedItem extends Slot {
   private final Item allowedItem;

   public SlotRestrictedItem(Item item, IInventory inventoryIn, int index, int xPosition, int yPosition) {
      super(inventoryIn, index, xPosition, yPosition);
      this.allowedItem = item;
   }

   public boolean isItemValid(@Nonnull ItemStack stack) {
      return !stack.isEmpty() && stack.getItem() == this.allowedItem;
   }
}
