package com.alcatrazescapee.alcatrazcore.inventory.container;

import javax.annotation.Nonnull;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.ClickType;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;

public abstract class ContainerItemStack extends Container {
   protected final ItemStack stack;
   protected final EntityPlayer player;
   protected int itemIndex;

   public ContainerItemStack(InventoryPlayer playerInv, ItemStack stack, int itemIndexOffset) {
      this.player = playerInv.player;
      this.stack = stack;
      if (stack == this.player.getHeldItemMainhand()) {
         this.itemIndex = playerInv.currentItem + 27 + itemIndexOffset;
      } else {
         this.itemIndex = -100;
      }

      this.addContainerSlots();
      this.addPlayerInventorySlots(playerInv);
   }

   @Nonnull
   public ItemStack transferStackInSlot(EntityPlayer player, int index) {
      Slot slot = (Slot)this.inventorySlots.get(index);
      if (slot != null && slot.getHasStack()) {
         if (index == this.itemIndex) {
            return ItemStack.EMPTY;
         } else {
            ItemStack itemstack1 = slot.getStack();
            ItemStack itemstack = itemstack1.copy();
            int containerSlots = this.inventorySlots.size() - player.inventory.mainInventory.size();
            if (index < containerSlots) {
               if (!this.mergeItemStack(itemstack1, containerSlots, this.inventorySlots.size(), true)) {
                  return ItemStack.EMPTY;
               }
            } else if (!this.mergeItemStack(itemstack1, 0, containerSlots, false)) {
               return ItemStack.EMPTY;
            }

            if (itemstack1.getCount() == 0) {
               slot.putStack(ItemStack.EMPTY);
            } else {
               slot.onSlotChanged();
            }

            if (itemstack1.getCount() == itemstack.getCount()) {
               return ItemStack.EMPTY;
            } else {
               slot.onTake(player, itemstack1);
               return itemstack;
            }
         }
      } else {
         return ItemStack.EMPTY;
      }
   }

   @Nonnull
   public ItemStack slotClick(int slotID, int dragType, ClickType clickType, EntityPlayer player) {
      return (clickType == ClickType.QUICK_MOVE || clickType == ClickType.PICKUP || clickType == ClickType.SWAP) && slotID == this.itemIndex ? ItemStack.EMPTY : super.slotClick(slotID, dragType, clickType, player);
   }

   public void onContainerClosed(EntityPlayer player) {
      IItemHandler cap = (IItemHandler)this.stack.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, (EnumFacing)null);
      if (cap instanceof ItemStackHandler) {
         this.stack.setTagCompound(((ItemStackHandler)cap).serializeNBT());
      }

      super.onContainerClosed(player);
   }

   public boolean canInteractWith(@Nonnull EntityPlayer player) {
      return true;
   }

   protected abstract void addContainerSlots();

   protected void addPlayerInventorySlots(InventoryPlayer playerInv) {
      for(int i = 0; i < 3; ++i) {
         for(int j = 0; j < 9; ++j) {
            this.addSlotToContainer(new Slot(playerInv, j + i * 9 + 9, 8 + j * 18, 84 + i * 18));
         }
      }

      for(int k = 0; k < 9; ++k) {
         this.addSlotToContainer(new Slot(playerInv, k, 8 + k * 18, 142));
      }

   }
}
