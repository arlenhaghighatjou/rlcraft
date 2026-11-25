package com.alcatrazescapee.alcatrazcore.inventory.slot;

import javax.annotation.Nonnull;
import net.minecraft.item.ItemStack;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.SlotItemHandler;

public class SlotOutput extends SlotItemHandler {
   public SlotOutput(@Nonnull IItemHandler inventory, int idx, int x, int y) {
      super(inventory, idx, x, y);
   }

   public boolean isItemValid(@Nonnull ItemStack stack) {
      return false;
   }
}
