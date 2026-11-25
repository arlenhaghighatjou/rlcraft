package ivorius.reccomplex.gui.loot;

import ivorius.reccomplex.gui.InventoryWatcher;
import ivorius.reccomplex.world.storage.loot.GenericLootTable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.ITextComponent;

public class InventoryGenericLootTable_Single implements IInventory {
   public List<GenericLootTable.RandomizedItemStack> weightedRandomChestContents;
   private List<InventoryWatcher> watchers = new ArrayList();

   public InventoryGenericLootTable_Single(List<GenericLootTable.RandomizedItemStack> weightedRandomChestContents) {
      this.weightedRandomChestContents = weightedRandomChestContents;
   }

   public void addWatcher(InventoryWatcher watcher) {
      this.watchers.add(watcher);
   }

   public void removeWatcher(InventoryWatcher watcher) {
      this.watchers.remove(watcher);
   }

   public List<InventoryWatcher> watchers() {
      return Collections.unmodifiableList(this.watchers);
   }

   public int getSizeInventory() {
      return this.weightedRandomChestContents.size() + 1;
   }

   public boolean isEmpty() {
      for(GenericLootTable.RandomizedItemStack randomized : this.weightedRandomChestContents) {
         if (!randomized.itemStack.isEmpty()) {
            return false;
         }
      }

      return true;
   }

   public ItemStack getStackInSlot(int var1) {
      return var1 < this.weightedRandomChestContents.size() ? ((GenericLootTable.RandomizedItemStack)this.weightedRandomChestContents.get(var1)).itemStack : ItemStack.EMPTY;
   }

   public ItemStack decrStackSize(int slot, int dec) {
      if (slot < this.weightedRandomChestContents.size()) {
         ItemStack stack = ((GenericLootTable.RandomizedItemStack)this.weightedRandomChestContents.get(slot)).itemStack;
         if (!stack.isEmpty()) {
            if (stack.getCount() <= dec) {
               this.weightedRandomChestContents.remove(slot);
               this.markDirty();
               return stack;
            }

            ItemStack itemstack = stack.splitStack(dec);
            if (stack.getCount() == 0) {
               this.weightedRandomChestContents.remove(slot);
            }

            this.markDirty();
            return itemstack;
         }
      }

      return ItemStack.EMPTY;
   }

   public ItemStack removeStackFromSlot(int var1) {
      return ItemStack.EMPTY;
   }

   public void setInventorySlotContents(int slot, ItemStack stack) {
      if (slot < this.weightedRandomChestContents.size()) {
         if (!stack.isEmpty()) {
            ((GenericLootTable.RandomizedItemStack)this.weightedRandomChestContents.get(slot)).itemStack = stack;
         } else {
            this.weightedRandomChestContents.remove(slot);
         }
      } else if (!stack.isEmpty()) {
         this.weightedRandomChestContents.add(new GenericLootTable.RandomizedItemStack(stack, 1, stack.getMaxStackSize(), 1.0D));
      }

      if (!stack.isEmpty() && stack.getCount() > this.getInventoryStackLimit()) {
         stack.setCount(this.getInventoryStackLimit());
      }

      this.markDirty();
   }

   public int getInventoryStackLimit() {
      return 1;
   }

   public void markDirty() {
      for(InventoryWatcher watcher : this.watchers) {
         watcher.inventoryChanged(this);
      }

   }

   public boolean isUsableByPlayer(EntityPlayer var1) {
      return true;
   }

   public void openInventory(EntityPlayer player) {
   }

   public void closeInventory(EntityPlayer player) {
   }

   public boolean isItemValidForSlot(int var1, ItemStack var2) {
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
   }

   public String getName() {
      return null;
   }

   public boolean hasCustomName() {
      return false;
   }

   public ITextComponent getDisplayName() {
      return null;
   }
}
