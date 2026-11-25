package reborncore.client.gui.slots;

import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import reborncore.api.power.IEnergyItemInfo;

public class SlotCharge extends BaseSlot {
   public SlotCharge(IInventory inventoryIn, int index, int xPosition, int yPosition) {
      super(inventoryIn, index, xPosition, yPosition);
   }

   public boolean isItemValid(ItemStack stack) {
      return stack.getItem() instanceof IEnergyItemInfo;
   }

   public boolean canWorldBlockRemove() {
      return false;
   }
}
