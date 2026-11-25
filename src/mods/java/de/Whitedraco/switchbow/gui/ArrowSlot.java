package de.Whitedraco.switchbow.gui;

import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemArrow;
import net.minecraft.item.ItemStack;

public class ArrowSlot extends Slot {
   public ArrowSlot(IInventory inventory, int slotIndex, int x, int y) {
      super(inventory, slotIndex, x, y);
   }

   public boolean isItemValid(ItemStack itemstack) {
      return itemstack.getItem() instanceof ItemArrow;
   }

   public int getSlotStackLimit() {
      return 64;
   }
}
