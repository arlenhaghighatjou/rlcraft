package net.silentchaos512.lib.inventory;

import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

public class SlotOutputOnly extends Slot {
   public SlotOutputOnly(IInventory inventoryIn, int index, int xPosition, int yPosition) {
      super(inventoryIn, index, xPosition, yPosition);
   }

   public boolean isItemValid(ItemStack stack) {
      return false;
   }
}
