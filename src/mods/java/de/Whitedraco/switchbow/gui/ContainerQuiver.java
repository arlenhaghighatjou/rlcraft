package de.Whitedraco.switchbow.gui;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.inventory.ClickType;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IContainerListener;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemArrow;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.NonNullList;

public class ContainerQuiver extends Container {
   private IInventory inventoryPlayer;
   private InventoryItems inventoryQuiver;
   private ItemStack HoldItem;

   public ContainerQuiver(EntityPlayer Player, ItemStack item) {
      this.inventoryQuiver = new InventoryItems(item);
      this.HoldItem = item;
      this.inventoryPlayer = Player.inventory;

      for(int i = 0; i < 3; ++i) {
         for(int j = 0; j < 3; ++j) {
            this.addSlotToContainer(new ArrowSlot(this.inventoryQuiver, j + i * 3, 62 + j * 18, 17 + i * 18));
         }
      }

      for(int var5 = 0; var5 < 3; ++var5) {
         for(int j = 0; j < 9; ++j) {
            this.addSlotToContainer(new Slot(this.inventoryPlayer, j + var5 * 9 + 9, 8 + j * 18, 84 + var5 * 18));
         }
      }

      for(int var6 = 0; var6 < 9; ++var6) {
         this.addSlotToContainer(new Slot(this.inventoryPlayer, var6, 8 + var6 * 18, 142));
      }

   }

   public boolean canInteractWith(EntityPlayer player) {
      return this.inventoryQuiver.isUsableByPlayer(player);
   }

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
         } else if (itemstack1.getItem() instanceof ItemArrow && !this.mergeItemStack(itemstack1, 0, 9, false)) {
            return ItemStack.EMPTY;
         }

         if (itemstack1.isEmpty()) {
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

   public ItemStack slotClick(int slotID, int clickedButton, ClickType clickType, EntityPlayer player) {
      return slotID >= 0 && this.getSlot(slotID) != null && this.getSlot(slotID).getStack() == this.HoldItem ? ItemStack.EMPTY : super.slotClick(slotID, clickedButton, clickType, player);
   }

   public void onContainerClosed(EntityPlayer player) {
      if (!player.world.isRemote) {
         NBTTagList nbttaglist = new NBTTagList();

         for(int i = 0; i < this.inventoryQuiver.getSizeInventory(); ++i) {
            ItemStack stack = this.inventoryQuiver.getStackInSlot(i);
            if (stack != ItemStack.EMPTY && stack.getItem() != Items.AIR) {
               NBTTagCompound nbttagcompound1 = new NBTTagCompound();
               nbttagcompound1.setByte("Slot", (byte)i);
               stack.writeToNBT(nbttagcompound1);
               nbttaglist.appendTag(nbttagcompound1);
            }
         }

         this.HoldItem.getTagCompound().setTag("ItemInventory", nbttaglist);
      }
   }

   public NonNullList<ItemStack> getInventory() {
      NonNullList<ItemStack> nonnulllist = NonNullList.create();

      for(int i = 0; i < this.inventoryQuiver.getSizeInventory(); ++i) {
         if (this.inventoryQuiver.getStackInSlot(i) != null && this.inventoryQuiver.getStackInSlot(i) != ItemStack.EMPTY) {
            nonnulllist.add(((Slot)this.inventorySlots.get(i)).getStack());
         } else {
            nonnulllist.add(ItemStack.EMPTY);
         }
      }

      return nonnulllist;
   }

   public void detectAndSendChanges() {
      for(int i = 0; i < this.inventorySlots.size(); ++i) {
         ItemStack itemstack = ((Slot)this.inventorySlots.get(i)).getStack();
         ItemStack itemstack1 = (ItemStack)this.inventoryItemStacks.get(i);
         if (!ItemStack.areItemStacksEqual(itemstack1, itemstack)) {
            itemstack1 = itemstack.isEmpty() ? ItemStack.EMPTY : itemstack.copy();
            this.inventoryItemStacks.set(i, itemstack1);

            for(int j = 0; j < this.listeners.size(); ++j) {
               ((IContainerListener)this.listeners.get(j)).sendSlotContents(this, i, itemstack1);
            }
         }
      }

   }
}
