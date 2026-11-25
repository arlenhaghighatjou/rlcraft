package de.Whitedraco.switchbow.gui;

import javax.annotation.Nullable;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

public class ContainerArrowDispenser extends Container {
   private final IInventory dispenserInventory;

   public ContainerArrowDispenser(IInventory playerInventory, IInventory dispenserInventoryIn) {
      this.dispenserInventory = dispenserInventoryIn;

      for(int i = 0; i < 3; ++i) {
         for(int j = 0; j < 3; ++j) {
            this.addSlotToContainer(new ArrowSlotDispenser(dispenserInventoryIn, j + i * 3, 116 + j * 18, 21 + i * 18));
         }
      }

      for(int k = 0; k < 3; ++k) {
         for(int i1 = 0; i1 < 9; ++i1) {
            this.addSlotToContainer(new Slot(playerInventory, i1 + k * 9 + 9, 8 + i1 * 18, 88 + k * 18));
         }
      }

      for(int l = 0; l < 9; ++l) {
         this.addSlotToContainer(new Slot(playerInventory, l, 8 + l * 18, 146));
      }

   }

   public boolean canInteractWith(EntityPlayer playerIn) {
      return this.dispenserInventory.isUsableByPlayer(playerIn);
   }

   @Nullable
   public ItemStack transferStackInSlot(EntityPlayer playerIn, int index) {
      ItemStack itemstack = ItemStack.EMPTY;
      Slot slot = (Slot)this.inventorySlots.get(index);
      if (slot != null && slot.getHasStack()) {
         ItemStack itemstack1 = slot.getStack();
         itemstack = itemstack1.copy();
         if (index < 9) {
            if (!this.mergeItemStack(itemstack1, 9, 45, true)) {
               return ItemStack.EMPTY;
            }
         } else if (!this.mergeItemStack(itemstack1, 0, 9, false)) {
            return ItemStack.EMPTY;
         }

         if (itemstack1.getCount() == 0) {
            slot.putStack(ItemStack.EMPTY);
         } else {
            slot.onSlotChanged();
         }

         if (itemstack1.getCount() == itemstack.getCount()) {
            return ItemStack.EMPTY;
         }

         slot.onTake(playerIn, itemstack1);
      }

      return itemstack;
   }
}
