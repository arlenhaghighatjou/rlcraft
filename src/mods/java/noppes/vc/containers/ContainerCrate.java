package noppes.vc.containers;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

public class ContainerCrate extends Container {
   public IInventory lowerChestInventory;
   public IInventory upperChestInventory;
   private int numRows;

   public ContainerCrate(IInventory par1IInventory, IInventory par2IInventory) {
      this.lowerChestInventory = par2IInventory;
      this.upperChestInventory = par1IInventory;
      this.numRows = par2IInventory.getSizeInventory() / 9;
      par2IInventory.openInventory((EntityPlayer)null);
      int i = (this.numRows - 4) * 18;

      for(int j = 0; j < this.numRows; ++j) {
         for(int k = 0; k < 9; ++k) {
            this.addSlotToContainer(new Slot(par2IInventory, k + j * 9, 8 + k * 18, 18 + j * 18));
         }
      }

      for(int var6 = 0; var6 < 3; ++var6) {
         for(int k = 0; k < 9; ++k) {
            this.addSlotToContainer(new Slot(par1IInventory, k + var6 * 9 + 9, 8 + k * 18, 103 + var6 * 18 + i));
         }
      }

      for(int var7 = 0; var7 < 9; ++var7) {
         this.addSlotToContainer(new Slot(par1IInventory, var7, 8 + var7 * 18, 161 + i));
      }

   }

   public boolean canInteractWith(EntityPlayer par1EntityPlayer) {
      return this.lowerChestInventory.isUsableByPlayer(par1EntityPlayer);
   }

   public ItemStack transferStackInSlot(EntityPlayer par1EntityPlayer, int par2) {
      ItemStack itemstack = ItemStack.EMPTY;
      Slot slot = (Slot)this.inventorySlots.get(par2);
      if (slot != null && slot.getHasStack()) {
         ItemStack itemstack1 = slot.getStack();
         itemstack = itemstack1.copy();
         if (par2 < this.numRows * 9) {
            if (!this.mergeItemStack(itemstack1, this.numRows * 9, this.inventorySlots.size(), true)) {
               return ItemStack.EMPTY;
            }
         } else if (!this.mergeItemStack(itemstack1, 0, this.numRows * 9, false)) {
            return ItemStack.EMPTY;
         }

         if (itemstack1.getCount() == 0) {
            slot.putStack(ItemStack.EMPTY);
         } else {
            slot.onSlotChanged();
         }
      }

      return itemstack;
   }

   public void onContainerClosed(EntityPlayer par1EntityPlayer) {
      super.onContainerClosed(par1EntityPlayer);
      this.lowerChestInventory.closeInventory(par1EntityPlayer);
   }

   public IInventory getLowerChestInventory() {
      return this.lowerChestInventory;
   }
}
