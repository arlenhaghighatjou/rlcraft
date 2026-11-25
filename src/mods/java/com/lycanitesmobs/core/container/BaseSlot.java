package com.lycanitesmobs.core.container;

import com.lycanitesmobs.core.inventory.InventoryCreature;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

public class BaseSlot extends Slot {
   public BaseSlot(IInventory inventory, int slotIndex, int x, int y) {
      super(inventory, slotIndex, x, y);
   }

   public boolean isItemValid(ItemStack itemStack) {
      return this.inventory == null ? true : this.inventory.isItemValidForSlot(this.getSlotIndex(), itemStack);
   }

   public int getSlotStackLimit() {
      return this.inventory instanceof InventoryCreature && ((InventoryCreature)this.inventory).getTypeFromSlot(this.getSlotIndex()) != null ? 1 : this.inventory.getInventoryStackLimit();
   }
}
