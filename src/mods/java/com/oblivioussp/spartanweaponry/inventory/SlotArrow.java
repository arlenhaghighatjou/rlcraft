package com.oblivioussp.spartanweaponry.inventory;

import net.minecraft.item.ItemArrow;
import net.minecraft.item.ItemStack;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.SlotItemHandler;

public class SlotArrow extends SlotItemHandler {
   public SlotArrow(IItemHandler itemHandler, int index, int xPosition, int yPosition) {
      super(itemHandler, index, xPosition, yPosition);
   }

   public boolean isItemValid(ItemStack stack) {
      return stack.getItem() instanceof ItemArrow ? super.isItemValid(stack) : false;
   }
}
