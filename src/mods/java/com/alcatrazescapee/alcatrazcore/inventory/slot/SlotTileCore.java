package com.alcatrazescapee.alcatrazcore.inventory.slot;

import com.alcatrazescapee.alcatrazcore.tile.TileInventory;
import javax.annotation.Nonnull;
import net.minecraft.item.ItemStack;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.SlotItemHandler;

public class SlotTileCore extends SlotItemHandler {
   protected final TileInventory te;

   public SlotTileCore(@Nonnull IItemHandler inventory, int idx, int x, int y, @Nonnull TileInventory te) {
      super(inventory, idx, x, y);
      this.te = te;
   }

   public void onSlotChanged() {
      this.te.setAndUpdateSlots(this.getSlotIndex());
      super.onSlotChanged();
   }

   public boolean isItemValid(@Nonnull ItemStack stack) {
      return this.te.isItemValid(this.getSlotIndex(), stack) && super.isItemValid(stack);
   }

   public int getSlotStackLimit() {
      return Math.min(this.te.getSlotLimit(this.getSlotIndex()), super.getSlotStackLimit());
   }
}
