package com.tmtravlr.qualitytools.reforging;

import com.tmtravlr.qualitytools.QualityToolsHelper;
import javax.annotation.Nullable;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IContainerListener;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

public class ContainerReforgingStation extends Container {
   private final IInventory tileReforgingStation;
   private final Slot toolSlot;
   private final Slot materialSlot;

   public ContainerReforgingStation(InventoryPlayer playerInventory, IInventory tileReforgingStationIn) {
      this.tileReforgingStation = tileReforgingStationIn;
      this.toolSlot = this.addSlotToContainer(new ContainerReforgingStation.SlotTool(this.tileReforgingStation, 0, 80, 19));
      this.materialSlot = this.addSlotToContainer(new Slot(this.tileReforgingStation, 1, 80, 59));

      for(int i = 0; i < 3; ++i) {
         for(int j = 0; j < 9; ++j) {
            this.addSlotToContainer(new Slot(playerInventory, j + i * 9 + 9, 8 + j * 18, 84 + i * 18));
         }
      }

      for(int k = 0; k < 9; ++k) {
         this.addSlotToContainer(new Slot(playerInventory, k, 8 + k * 18, 142));
      }

   }

   public void addListener(IContainerListener listener) {
      super.addListener(listener);
      listener.sendAllWindowProperties(this, this.tileReforgingStation);
   }

   public boolean canInteractWith(EntityPlayer playerIn) {
      return this.tileReforgingStation.isUsableByPlayer(playerIn);
   }

   @Nullable
   public ItemStack transferStackInSlot(EntityPlayer playerIn, int index) {
      ItemStack oldStack = ItemStack.EMPTY;
      Slot slot = (Slot)this.inventorySlots.get(index);
      if (slot != null && slot.getHasStack()) {
         ItemStack newStack = slot.getStack();
         oldStack = newStack.copy();
         if (index == 0) {
            if (!this.mergeItemStack(newStack, 2, 38, false)) {
               return ItemStack.EMPTY;
            }
         } else if (index == 1) {
            if (!this.mergeItemStack(newStack, 2, 38, false)) {
               return ItemStack.EMPTY;
            }
         } else if (!this.toolSlot.getHasStack() && this.toolSlot.isItemValid(newStack)) {
            if (!this.mergeItemStack(newStack, 0, 1, true)) {
               return ItemStack.EMPTY;
            }
         } else if (!this.mergeItemStack(newStack, 1, 2, true)) {
            return ItemStack.EMPTY;
         }

         if (newStack.getCount() == 0) {
            slot.putStack(ItemStack.EMPTY);
         } else {
            slot.onSlotChanged();
         }

         if (newStack.getCount() == oldStack.getCount()) {
            return ItemStack.EMPTY;
         }

         slot.onTake(playerIn, newStack);
      }

      return oldStack;
   }

   static class SlotTool extends Slot {
      public SlotTool(IInventory inventoryIn, int index, int xPosition, int yPosition) {
         super(inventoryIn, index, xPosition, yPosition);
      }

      public boolean isItemValid(ItemStack stack) {
         return QualityToolsHelper.hasQualityTag(stack);
      }

      public int getSlotStackLimit() {
         return 1;
      }
   }
}
