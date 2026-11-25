package de.Whitedraco.switchbow.gui;

import de.Whitedraco.switchbow.ItemInit;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemArrow;
import net.minecraft.item.ItemStack;

public class ArrowSlotDispenser extends Slot {
   public ArrowSlotDispenser(IInventory inventory, int slotIndex, int x, int y) {
      super(inventory, slotIndex, x, y);
   }

   public boolean isItemValid(ItemStack itemstack) {
      return itemstack.getItem() != ItemInit.ArrowTriple && itemstack.getItem() != ItemInit.ArrowVampier && itemstack.getItem() != ItemInit.ArrowEnderperle ? itemstack.getItem() instanceof ItemArrow : false;
   }

   public int getSlotStackLimit() {
      return 64;
   }
}
