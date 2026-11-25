package com.alcatrazescapee.notreepunching.common.container;

import com.alcatrazescapee.alcatrazcore.inventory.container.ContainerTileInventory;
import com.alcatrazescapee.alcatrazcore.inventory.slot.SlotTileCore;
import com.alcatrazescapee.notreepunching.common.tile.TileLargeVessel;
import javax.annotation.ParametersAreNonnullByDefault;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;

@ParametersAreNonnullByDefault
public class ContainerLargeVessel extends ContainerTileInventory<TileLargeVessel> {
   public ContainerLargeVessel(InventoryPlayer playerInv, TileLargeVessel tile) {
      super(playerInv, tile);
   }

   protected void addContainerSlots() {
      IItemHandler cap = (IItemHandler)((TileLargeVessel)this.tile).getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, (EnumFacing)null);
      if (cap != null) {
         for(int i = 0; i < 3; ++i) {
            for(int j = 0; j < 3; ++j) {
               this.addSlotToContainer(new SlotTileCore(cap, i + 3 * j, 62 + i * 18, 20 + j * 18, this.tile));
            }
         }
      }

   }
}
