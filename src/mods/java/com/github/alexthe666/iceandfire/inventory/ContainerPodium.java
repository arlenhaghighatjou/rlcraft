package com.github.alexthe666.iceandfire.inventory;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

public class ContainerPodium extends Container {
   private final IInventory podium;

   public ContainerPodium(InventoryPlayer playerInventory, IInventory podiumIn, EntityPlayer player) {
      this.podium = podiumIn;
      podiumIn.openInventory(player);
      byte b0 = 51;
      this.addSlotToContainer(new Slot(podiumIn, 0, 80, 20));

      for(int i = 0; i < 3; ++i) {
         for(int j = 0; j < 9; ++j) {
            this.addSlotToContainer(new Slot(playerInventory, j + i * 9 + 9, 8 + j * 18, i * 18 + b0));
         }
      }

      for(int var7 = 0; var7 < 9; ++var7) {
         this.addSlotToContainer(new Slot(playerInventory, var7, 8 + var7 * 18, 58 + b0));
      }

   }

   public boolean canInteractWith(EntityPlayer playerIn) {
      return this.podium.isUsableByPlayer(playerIn);
   }

   public ItemStack transferStackInSlot(EntityPlayer playerIn, int index) {
      ItemStack itemstack = ItemStack.EMPTY;
      Slot slot = (Slot)this.inventorySlots.get(index);
      if (slot != null && slot.getHasStack()) {
         ItemStack itemstack1 = slot.getStack();
         itemstack = itemstack1.copy();
         if (index < this.podium.getSizeInventory()) {
            if (!this.mergeItemStack(itemstack1, this.podium.getSizeInventory(), this.inventorySlots.size(), true)) {
               return ItemStack.EMPTY;
            }
         } else if (!this.mergeItemStack(itemstack1, 0, this.podium.getSizeInventory(), false)) {
            return ItemStack.EMPTY;
         }

         if (itemstack1.isEmpty()) {
            slot.putStack(ItemStack.EMPTY);
         } else {
            slot.onSlotChanged();
         }
      }

      return itemstack;
   }

   public void onContainerClosed(EntityPlayer playerIn) {
      super.onContainerClosed(playerIn);
      this.podium.closeInventory(playerIn);
   }
}
