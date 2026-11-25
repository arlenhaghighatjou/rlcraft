package com.teamacronymcoders.base.capability.item;

import javax.annotation.Nonnull;
import net.minecraft.item.ItemStack;
import net.minecraftforge.items.ItemStackHandler;

public class ItemStackHandlerExport extends ItemStackHandler {
   public ItemStackHandlerExport() {
      this(1);
   }

   public ItemStackHandlerExport(int size) {
      super(size);
   }

   @Nonnull
   public ItemStack insertItem(int slot, @Nonnull ItemStack stack, boolean simulate) {
      return stack;
   }
}
