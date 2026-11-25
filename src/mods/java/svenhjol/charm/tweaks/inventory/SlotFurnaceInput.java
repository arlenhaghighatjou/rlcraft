package svenhjol.charm.tweaks.inventory;

import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import svenhjol.charm.Charm;
import svenhjol.charm.tweaks.feature.RestrictFurnaceInput;

public class SlotFurnaceInput extends Slot {
   public SlotFurnaceInput(IInventory inventoryIn, int slotIndex, int xPosition, int yPosition) {
      super(inventoryIn, slotIndex, xPosition, yPosition);
   }

   public boolean isItemValid(ItemStack stack) {
      return Charm.hasFeature(RestrictFurnaceInput.class) ? RestrictFurnaceInput.canSmelt(stack) : true;
   }
}
