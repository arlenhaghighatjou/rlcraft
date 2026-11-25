package de.Whitedraco.switchbow.gui;

import java.util.List;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.IInventoryChangedListener;
import net.minecraft.inventory.ItemStackHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.NonNullList;
import net.minecraft.util.text.ITextComponent;

public class InventoryItems implements IInventory {
   private String name = "Inventory Item";
   private final int slotsCount = 9;
   private final NonNullList<ItemStack> inventoryContents = NonNullList.withSize(9, ItemStack.EMPTY);
   private List<IInventoryChangedListener> changeListeners;
   private final ItemStack invItem;

   public InventoryItems(ItemStack stack) {
      this.invItem = stack;
      if (stack != ItemStack.EMPTY && !stack.hasTagCompound()) {
         stack.setTagCompound(new NBTTagCompound());
      }

      this.readFromNBT(stack.getTagCompound());
   }

   public String getName() {
      return this.name;
   }

   public boolean hasCustomName() {
      return this.name.length() > 0;
   }

   public ITextComponent getDisplayName() {
      return null;
   }

   public int getSizeInventory() {
      return 9;
   }

   public boolean isEmpty() {
      return false;
   }

   public ItemStack getStackInSlot(int index) {
      return index >= 0 && index < this.inventoryContents.size() ? (ItemStack)this.inventoryContents.get(index) : ItemStack.EMPTY;
   }

   public ItemStack decrStackSize(int index, int count) {
      ItemStack itemstack = ItemStackHelper.getAndSplit(this.inventoryContents, index, count);
      if (!itemstack.isEmpty()) {
         this.markDirty();
      }

      return itemstack;
   }

   public ItemStack removeStackFromSlot(int index) {
      ItemStack itemstack = (ItemStack)this.inventoryContents.get(index);
      if (itemstack.isEmpty()) {
         return ItemStack.EMPTY;
      } else {
         this.inventoryContents.set(index, ItemStack.EMPTY);
         return itemstack;
      }
   }

   public void setInventorySlotContents(int index, ItemStack stack) {
      this.inventoryContents.set(index, stack);
      if (!stack.isEmpty() && stack.getCount() > this.getInventoryStackLimit()) {
         stack.setCount(this.getInventoryStackLimit());
      }

      this.markDirty();
   }

   public int getInventoryStackLimit() {
      return 64;
   }

   public void markDirty() {
      if (this.changeListeners != null) {
         for(int i = 0; i < this.changeListeners.size(); ++i) {
            ((IInventoryChangedListener)this.changeListeners.get(i)).onInventoryChanged(this);
         }

         this.writeToNBT(this.invItem.getTagCompound());
      }

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
      this.inventoryContents.clear();
   }

   public void writeToNBT(NBTTagCompound tagcompound) {
      NBTTagList items = new NBTTagList();

      for(int i = 0; i < this.getSizeInventory(); ++i) {
         if (this.getStackInSlot(i) != ItemStack.EMPTY) {
            NBTTagCompound item = new NBTTagCompound();
            item.setInteger("Slot", i);
            this.getStackInSlot(i).writeToNBT(item);
            items.appendTag(item);
         }
      }

      tagcompound.setTag("ItemInventory", items);
   }

   public void readFromNBT(NBTTagCompound nbtTagCompound) {
      NBTTagList items = nbtTagCompound.getTagList("ItemInventory", 10);

      for(int i = 0; i < items.tagCount(); ++i) {
         NBTTagCompound item = items.getCompoundTagAt(i);
         byte slot = item.getByte("Slot");
         if (slot >= 0 && slot < this.getSizeInventory()) {
            this.inventoryContents.set(slot, new ItemStack(item));
         }
      }

   }
}
