package com.alcatrazescapee.alcatrazcore.inventory.capability;

import com.alcatrazescapee.alcatrazcore.tile.TileInventory;
import javax.annotation.Nonnull;
import net.minecraftforge.items.ItemStackHandler;

public class ItemHandlerTile extends ItemStackHandler {
   protected final TileInventory tile;

   public ItemHandlerTile(@Nonnull TileInventory tile, int size) {
      super(size);
      this.tile = tile;
   }

   public int getSlotLimit(int slot) {
      return this.tile.getSlotLimit(slot);
   }

   protected void onContentsChanged(int slot) {
      this.tile.setAndUpdateSlots(slot);
   }
}
