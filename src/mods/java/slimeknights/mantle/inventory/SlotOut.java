package slimeknights.mantle.inventory;

import javax.annotation.Nonnull;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

public class SlotOut extends Slot {
   public SlotOut(IInventory inventoryIn, int index, int xPosition, int yPosition) {
      super(inventoryIn, index, xPosition, yPosition);
   }

   public boolean isItemValid(@Nonnull ItemStack stack) {
      return false;
   }
}
