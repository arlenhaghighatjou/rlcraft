package com.creativemd.creativecore.common.recipe.entry;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

public class StackedEntry extends RecipeEntry {
   public Object item;
   public NBTTagCompound nbt;
   public int minStackSize;

   public StackedEntry(Object item, int stackSize) {
      this.nbt = null;
      this.minStackSize = 1;
      this.minStackSize = stackSize;
   }

   public StackedEntry(Object item, int stackSize, NBTTagCompound nbt) {
      this(item, stackSize);
      this.nbt = nbt;
   }

   public boolean isEntry(ItemStack stack) {
      if (!isObject(stack, this.item) && stack.getCount() >= this.minStackSize) {
         if (this.nbt != null) {
            return stack.hasTagCompound() ? stack.getTagCompound().equals(this.nbt) : false;
         } else {
            return true;
         }
      } else {
         return false;
      }
   }

   public int getStackSize(ItemStack stack) {
      return this.minStackSize == 0 ? Integer.MAX_VALUE : stack.getCount() / this.minStackSize;
   }
}
