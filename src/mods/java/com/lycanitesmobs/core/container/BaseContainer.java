package com.lycanitesmobs.core.container;

import java.util.HashMap;
import java.util.Map;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

public class BaseContainer extends Container {
   public Map<Integer, String> specialSlots = new HashMap();
   public int playerInventoryStart = -1;
   public int playerInventoryFinish = -1;
   public int inventoryStart = -1;
   public int inventoryFinish = -1;
   public int specialStart = -1;
   public int specialFinish = -1;

   public BaseContainer() {
   }

   public Slot addSlot(IInventory inventory, int slotIndex, int x, int y) {
      return this.addSlotToContainer(new BaseSlot(inventory, slotIndex, x, y));
   }

   public void addPlayerSlots(InventoryPlayer playerInventory, int offsetX, int offsetY) {
      this.playerInventoryStart = this.inventorySlots.size();
      this.addSlotGrid(playerInventory, 8, 142, 1, 0, 8);
      this.addSlotGrid(playerInventory, 8, 84, 3, 9, 35);
      this.playerInventoryFinish = this.inventorySlots.size() - 1;
   }

   public void addSlotGrid(IInventory inventory, int x, int y, int totalRows, int start, int finish) {
      int totalSlots = finish - start + 1;
      if (totalSlots >= 1) {
         int excessSlots = totalSlots % totalRows;
         int totalColumns = (totalSlots - excessSlots) / totalRows;
         int slot = 0;

         int row;
         for(row = 0; row < totalRows; ++row) {
            for(int column = 0; column < totalColumns; ++column) {
               this.addSlot(inventory, start + slot, x + column * 18, y + row * 18);
               ++slot;
            }
         }

         ++row;

         for(int column = 0; column < excessSlots; ++column) {
            this.addSlot(inventory, start + slot, x + column * 18, y + row * 18);
            ++slot;
         }

      }
   }

   public void addSlotGrid(IInventory inventory, int x, int y, int totalRows) {
      this.addSlotGrid(inventory, x, y, totalRows, 0);
   }

   public void addSlotGrid(IInventory inventory, int x, int y, int totalRows, int start) {
      this.addSlotGrid(inventory, x, y, totalRows, start, inventory.getSizeInventory() - 1);
   }

   public void addSlotsByColumn(IInventory inventory, int x, int y, int columnMax, int start, int finish) {
      int row = 0;
      int column = 0;

      for(int slot = 0; slot <= finish; ++slot) {
         if (column >= columnMax) {
            ++row;
            column = 0;
         }

         this.addSlot(inventory, start + slot, x + column * 18, y + row * 18);
         ++column;
      }

   }

   public ItemStack transferStackInSlot(EntityPlayer player, int slotID) {
      ItemStack itemStack = ItemStack.EMPTY;
      Slot slot = (Slot)this.inventorySlots.get(slotID);
      if (slot != null && slot.getHasStack()) {
         ItemStack slotStack = slot.getStack();
         itemStack = slotStack.copy();
         if (this.isPlayerSlot(slotID)) {
            if (this.specialStart >= 0 && this.specialFinish >= 0) {
               for(int i = this.specialStart; i <= this.specialFinish; ++i) {
                  if (i < this.inventorySlots.size()) {
                     Slot targetSlot = (Slot)this.inventorySlots.get(i);
                     if (targetSlot.isItemValid(itemStack) && !this.mergeItemStack(slotStack, i, i + 1, false)) {
                        return ItemStack.EMPTY;
                     }
                  }
               }
            }

            if (this.inventoryStart >= 0 && this.inventoryFinish >= 0 && this.inventoryStart < this.inventoryFinish && !this.mergeItemStack(slotStack, this.inventoryStart, this.inventoryFinish + 1, false)) {
               return ItemStack.EMPTY;
            }
         } else if (this.playerInventoryFinish >= 0 && !this.mergeItemStack(slotStack, this.playerInventoryStart, this.playerInventoryFinish, true)) {
            return ItemStack.EMPTY;
         }

         if (slotStack.getCount() == 0) {
            slot.putStack(ItemStack.EMPTY);
         } else {
            slot.onSlotChanged();
         }

         return ItemStack.EMPTY;
      } else {
         return itemStack;
      }
   }

   public boolean isPlayerSlot(int slotID) {
      return slotID >= this.playerInventoryStart && slotID <= this.playerInventoryFinish;
   }

   public boolean sortToSpecialSlots(ItemStack slotStack) {
      return true;
   }

   public String getSpecialSlot(int slotID) {
      return !this.specialSlots.containsKey(Integer.valueOf(slotID)) ? null : (String)this.specialSlots.get(Integer.valueOf(slotID));
   }

   public boolean canInteractWith(EntityPlayer entityplayer) {
      return true;
   }
}
