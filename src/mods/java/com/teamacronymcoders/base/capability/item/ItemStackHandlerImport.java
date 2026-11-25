package com.teamacronymcoders.base.capability.item;

import javax.annotation.Nonnull;
import net.minecraft.item.ItemStack;
import net.minecraftforge.items.ItemStackHandler;

public class ItemStackHandlerImport extends ItemStackHandler {
   public ItemStackHandlerImport() {
      this(1);
   }

   public ItemStackHandlerImport(int size) {
      super(size);
   }

   @Nonnull
   public ItemStack extractItem(int slot, int amount, boolean simulate) {
      return ItemStack.EMPTY;
   }
}
