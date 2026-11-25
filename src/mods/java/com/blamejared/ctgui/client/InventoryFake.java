package com.blamejared.ctgui.client;

import javax.annotation.Nullable;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ItemStackHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.util.text.ITextComponent;

public class InventoryFake implements IInventory {
   private final int size;
   private NonNullList<ItemStack> items;

   public InventoryFake(int size) {
      this.size = size;
      this.items = NonNullList.withSize(size, ItemStack.EMPTY);
   }

   public int getSizeInventory() {
      return this.size;
   }

   public boolean isEmpty() {
      for(ItemStack itemstack : this.items) {
         if (!itemstack.isEmpty()) {
            return false;
         }
      }

      return true;
   }

   @Nullable
   public ItemStack getStackInSlot(int index) {
      return index >= 0 && index < this.getSizeInventory() ? (ItemStack)this.items.get(index) : ItemStack.EMPTY;
   }

   @Nullable
   public ItemStack decrStackSize(int index, int count) {
      return ItemStackHelper.getAndSplit(this.items, index, count);
   }

   @Nullable
   public ItemStack removeStackFromSlot(int index) {
      return ItemStackHelper.getAndRemove(this.items, index);
   }

   public void setInventorySlotContents(int index, ItemStack stack) {
      this.items.set(index, stack);
   }

   public int getInventoryStackLimit() {
      return 64;
   }

   public void markDirty() {
   }

   public boolean isUsableByPlayer(EntityPlayer player) {
      return true;
   }

   public void openInventory(EntityPlayer player) {
   }

   public void closeInventory(EntityPlayer player) {
   }

   public boolean isItemValidForSlot(int index, ItemStack stack) {
      return true;
   }

   public int getField(int id) {
      return 0;
   }

   public void setField(int id, int value) {
   }

   public int getFieldCount() {
      return 0;
   }

   public void clear() {
      this.items.clear();
   }

   public String getName() {
      return "Fake Inventory";
   }

   public boolean hasCustomName() {
      return false;
   }

   public ITextComponent getDisplayName() {
      return null;
   }
}
