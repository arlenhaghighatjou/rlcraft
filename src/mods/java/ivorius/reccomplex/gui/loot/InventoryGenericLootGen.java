package ivorius.reccomplex.gui.loot;

import ivorius.reccomplex.gui.InventoryWatcher;
import ivorius.reccomplex.world.storage.loot.WeightedRandomChestContent;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class InventoryGenericLootGen implements IInventory {
   public List<WeightedRandomChestContent> chestContents;
   private List<ItemStack> cachedItemStacks = new ArrayList();
   private List<InventoryWatcher> watchers = new ArrayList();

   public InventoryGenericLootGen(List<WeightedRandomChestContent> chestContents) {
      this.chestContents = chestContents;
      this.buildCachedStacks();
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
      return this.cachedItemStacks.size();
   }

   public boolean isEmpty() {
      for(ItemStack itemstack : this.cachedItemStacks) {
         if (!itemstack.isEmpty()) {
            return false;
         }
      }

      return true;
   }

   public ItemStack getStackInSlot(int var1) {
      return var1 < this.cachedItemStacks.size() ? (ItemStack)this.cachedItemStacks.get(var1) : ItemStack.EMPTY;
   }

   public ItemStack decrStackSize(int var1, int var2) {
      int stackIndex = var1 / 2;
      if (stackIndex < this.chestContents.size()) {
         WeightedRandomChestContent chestContent = (WeightedRandomChestContent)this.chestContents.get(stackIndex);
         if (var1 % 2 == 0) {
            chestContent.minStackSize -= var2;
         } else {
            chestContent.maxStackSize -= var2;
         }

         validateMinMax(chestContent);
         ItemStack returnStack = ((ItemStack)this.cachedItemStacks.get(var1)).splitStack(var2);
         this.markDirtyFromLootGenerator();
         return returnStack;
      } else {
         return null;
      }
   }

   public ItemStack removeStackFromSlot(int var1) {
      return ItemStack.EMPTY;
   }

   public void setInventorySlotContents(int var1, ItemStack var2) {
      int stackIndex = var1 / 2;
      if (stackIndex < this.chestContents.size()) {
         if (!var2.isEmpty()) {
            WeightedRandomChestContent chestContent = (WeightedRandomChestContent)this.chestContents.get(stackIndex);
            chestContent.theItemId = var2;
            if (var1 % 2 == 0) {
               chestContent.minStackSize = var2.getCount();
            } else {
               chestContent.maxStackSize = var2.getCount();
            }

            validateMinMax(chestContent);
         } else {
            this.chestContents.remove(stackIndex);
         }
      } else if (!var2.isEmpty()) {
         int min = var1 % 2 == 0 ? var2.getCount() : 1;
         int max = var1 % 2 == 1 ? var2.getCount() : var2.getMaxStackSize();
         WeightedRandomChestContent weightedRandomChestContent = new WeightedRandomChestContent(var2, min, max, 100);
         this.chestContents.add(weightedRandomChestContent);
      }

      this.markDirtyFromLootGenerator();
   }

   private static void validateMinMax(WeightedRandomChestContent chestContent) {
      if (chestContent.maxStackSize < chestContent.minStackSize) {
         int tmp = chestContent.maxStackSize;
         chestContent.maxStackSize = chestContent.minStackSize;
         chestContent.minStackSize = tmp;
      }

   }

   public int getInventoryStackLimit() {
      return 64;
   }

   public void markDirty() {
      this.calculateMinMax();
      this.buildCachedStacks();

      for(InventoryWatcher watcher : this.watchers) {
         watcher.inventoryChanged(this);
      }

   }

   public void markDirtyFromLootGenerator() {
      this.buildCachedStacks();

      for(InventoryWatcher watcher : this.watchers) {
         watcher.inventoryChanged(this);
      }

   }

   private void calculateMinMax() {
      for(int i = 0; i < this.cachedItemStacks.size(); ++i) {
         ItemStack stack = (ItemStack)this.cachedItemStacks.get(i);
         int stackIndex = i / 2;
         if (stackIndex < this.chestContents.size()) {
            WeightedRandomChestContent chestContent = (WeightedRandomChestContent)this.chestContents.get(stackIndex);
            if (i % 2 == 0) {
               chestContent.minStackSize = stack.getCount();
            } else {
               chestContent.maxStackSize = stack.getCount();
            }
         }
      }

      this.chestContents.forEach(InventoryGenericLootGen::validateMinMax);
   }

   private void buildCachedStacks() {
      this.cachedItemStacks.clear();

      for(WeightedRandomChestContent chestContent : this.chestContents) {
         ItemStack stackLow = chestContent.theItemId.copy();
         stackLow.setCount(chestContent.minStackSize);
         ItemStack stackHigh = chestContent.theItemId.copy();
         stackHigh.setCount(chestContent.maxStackSize);
         this.cachedItemStacks.add(stackLow);
         this.cachedItemStacks.add(stackHigh);
      }

   }

   public boolean isUsableByPlayer(EntityPlayer player) {
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
