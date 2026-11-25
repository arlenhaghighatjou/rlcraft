package com.teamacronymcoders.base.capability.item;

import com.teamacronymcoders.base.capability.QueueFoundation;
import com.teamacronymcoders.base.util.ItemStackUtils;
import javax.annotation.Nonnull;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.items.IItemHandler;

public class ItemStackQueue extends QueueFoundation<ItemStack> implements IItemHandler {
   public ItemStackQueue() {
      super(5);
   }

   protected ItemStack addToBack(ItemStack value) {
      ItemStack remaining = value;
      if (this.getEndOfQueue().isPresent()) {
         remaining = ItemStackUtils.mergeStacks((ItemStack)this.getEndOfQueue().get(), value);
      }

      if (this.anyRemainingValue(remaining) && this.getLength() < this.getQueueSize()) {
         this.push(remaining);
         remaining = ItemStack.EMPTY;
      }

      return remaining;
   }

   protected boolean anyRemainingValue(ItemStack value) {
      return !value.isEmpty();
   }

   public NBTTagCompound serializeValue(ItemStack value) {
      return value.serializeNBT();
   }

   public ItemStack deserializeValue(NBTTagCompound nbtTagCompound) {
      return new ItemStack(nbtTagCompound);
   }

   public int getSlots() {
      return 2;
   }

   @Nonnull
   public ItemStack getStackInSlot(int slot) {
      switch(slot) {
      case 0:
         return (ItemStack)this.peek().orElse(ItemStack.EMPTY);
      case 1:
         return (ItemStack)this.getEndOfQueue().orElse(ItemStack.EMPTY);
      default:
         throw new RuntimeException("Slot " + slot + " not in valid range - [0,1]");
      }
   }

   @Nonnull
   public ItemStack insertItem(int slot, @Nonnull ItemStack stack, boolean simulate) {
      if (slot == 1) {
         if (simulate) {
            return this.getLength() < this.getQueueSize() ? ItemStack.EMPTY : stack;
         } else {
            return ((ItemStack)this.offer(stack)).copy();
         }
      } else {
         return stack;
      }
   }

   @Nonnull
   public ItemStack extractItem(int slot, int amount, boolean simulate) {
      if (slot == 0) {
         return simulate ? (ItemStack)this.peek().orElse(ItemStack.EMPTY) : (ItemStack)this.pull().orElse(ItemStack.EMPTY);
      } else {
         return ItemStack.EMPTY;
      }
   }

   public int getSlotLimit(int slot) {
      return this.getItemStack(slot).getMaxStackSize();
   }

   @Nonnull
   private ItemStack getItemStack(int slot) {
      if (slot >= 0 && slot <= 1) {
         return slot == 0 ? (ItemStack)this.peek().orElse(ItemStack.EMPTY) : (ItemStack)this.getEndOfQueue().orElse(ItemStack.EMPTY);
      } else {
         throw new RuntimeException("Slot " + slot + " not in valid range - [0,1]");
      }
   }
}
