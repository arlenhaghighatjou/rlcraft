package net.mcft.copy.backpacks.item;

import net.minecraft.item.ItemStack;

public interface IDyeableItem {
   default boolean canDye(ItemStack stack) {
      return true;
   }

   default boolean canWash(ItemStack stack) {
      return this.canDye(stack);
   }
}
