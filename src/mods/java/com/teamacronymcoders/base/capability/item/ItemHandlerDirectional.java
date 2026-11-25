package com.teamacronymcoders.base.capability.item;

import javax.annotation.Nonnull;
import net.minecraft.item.ItemStack;
import net.minecraftforge.items.IItemHandler;

public class ItemHandlerDirectional implements IItemHandler {
   private final IItemHandler itemHandler;
   private final boolean input;

   public ItemHandlerDirectional(IItemHandler itemHandler, boolean input) {
      this.itemHandler = itemHandler;
      this.input = input;
   }

   public int getSlots() {
      return this.itemHandler.getSlots();
   }

   @Nonnull
   public ItemStack getStackInSlot(int slot) {
      return this.itemHandler.getStackInSlot(slot);
   }

   @Nonnull
   public ItemStack insertItem(int slot, @Nonnull ItemStack stack, boolean simulate) {
      return this.input ? this.itemHandler.insertItem(slot, stack, simulate) : stack;
   }

   @Nonnull
   public ItemStack extractItem(int slot, int amount, boolean simulate) {
      return !this.input ? this.itemHandler.extractItem(slot, amount, simulate) : ItemStack.EMPTY;
   }

   public int getSlotLimit(int slot) {
      return this.itemHandler.getSlotLimit(slot);
   }
}
