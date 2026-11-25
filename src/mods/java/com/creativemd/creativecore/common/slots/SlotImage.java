package com.creativemd.creativecore.common.slots;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

public class SlotImage extends Slot {
   public SlotImage(IInventory inventory, int index, int x, int y) {
      super(inventory, index, x, y);
   }

   public boolean isItemValid(ItemStack stack) {
      this.putStack(stack.copy());
      return false;
   }

   public boolean canTakeStack(EntityPlayer player) {
      this.putStack((ItemStack)null);
      return false;
   }
}
