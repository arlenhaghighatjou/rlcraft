package com.creativemd.creativecore.common.slots;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

public class SlotOutput extends Slot {
   public ItemStack stack;

   public SlotOutput(IInventory inventory, int index, int x, int y) {
      super(inventory, index, x, y);
   }

   public boolean canTakeStack(EntityPlayer player) {
      if (this.getHasStack()) {
         this.stack = this.getStack().copy();
      } else {
         this.stack = null;
      }

      return true;
   }

   public boolean isItemValid(ItemStack stack) {
      return false;
   }
}
