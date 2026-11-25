package com.alcatrazescapee.notreepunching.common.container;

import com.alcatrazescapee.alcatrazcore.inventory.container.ContainerTileInventory;
import com.alcatrazescapee.alcatrazcore.inventory.slot.SlotOutput;
import com.alcatrazescapee.alcatrazcore.inventory.slot.SlotTileCore;
import com.alcatrazescapee.notreepunching.common.tile.TileFirePit;
import javax.annotation.ParametersAreNonnullByDefault;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;

@ParametersAreNonnullByDefault
public class ContainerFirePit extends ContainerTileInventory<TileFirePit> {
   public ContainerFirePit(InventoryPlayer playerInv, TileFirePit tile) {
      super(playerInv, tile);
   }

   protected void addContainerSlots() {
      IItemHandler inventory = (IItemHandler)((TileFirePit)this.tile).getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, (EnumFacing)null);
      if (inventory != null) {
         this.addSlotToContainer(new SlotTileCore(inventory, 0, 80, 59, this.tile));
         this.addSlotToContainer(new SlotTileCore(inventory, 1, 52, 23, this.tile));
         this.addSlotToContainer(new SlotOutput(inventory, 2, 108, 23));
      }

   }
}
