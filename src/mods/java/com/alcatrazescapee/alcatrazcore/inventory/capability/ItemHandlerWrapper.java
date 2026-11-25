package com.alcatrazescapee.alcatrazcore.inventory.capability;

import com.alcatrazescapee.alcatrazcore.tile.TileInventory;
import javax.annotation.Nonnull;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.items.ItemStackHandler;

public class ItemHandlerWrapper extends ItemStackHandler {
   private final ItemStackHandler internalInventory;
   private final TileInventory tile;
   private boolean[] extractSlots;
   private boolean[] insertSlots;

   public ItemHandlerWrapper(ItemStackHandler inventory, TileInventory tile) {
      this.internalInventory = inventory;
      this.tile = tile;
      this.extractSlots = new boolean[inventory.getSlots()];
      this.insertSlots = new boolean[inventory.getSlots()];
   }

   public ItemHandlerWrapper addInsertSlot(int... slots) {
      for(int s : slots) {
         if (s >= 0 && s < this.insertSlots.length) {
            this.insertSlots[s] = true;
         }
      }

      return this;
   }

   public ItemHandlerWrapper addExtractSlot(int... slots) {
      for(int s : slots) {
         if (s >= 0 && s < this.extractSlots.length) {
            this.extractSlots[s] = true;
         }
      }

      return this;
   }

   public void setStackInSlot(int slot, @Nonnull ItemStack stack) {
      this.internalInventory.setStackInSlot(slot, stack);
   }

   public int getSlots() {
      return this.internalInventory.getSlots();
   }

   @Nonnull
   public ItemStack getStackInSlot(int slot) {
      return this.internalInventory.getStackInSlot(slot);
   }

   @Nonnull
   public ItemStack insertItem(int slot, @Nonnull ItemStack stack, boolean simulate) {
      return this.insertSlots[slot] && this.tile.isItemValid(slot, stack) ? this.internalInventory.insertItem(slot, stack, simulate) : stack;
   }

   @Nonnull
   public ItemStack extractItem(int slot, int amount, boolean simulate) {
      return this.extractSlots[slot] ? this.internalInventory.extractItem(slot, amount, simulate) : ItemStack.EMPTY;
   }

   public int getSlotLimit(int slot) {
      return this.internalInventory.getSlotLimit(slot);
   }

   public NBTTagCompound serializeNBT() {
      return this.internalInventory.serializeNBT();
   }

   public void deserializeNBT(NBTTagCompound nbt) {
      this.internalInventory.deserializeNBT(nbt);
   }
}
