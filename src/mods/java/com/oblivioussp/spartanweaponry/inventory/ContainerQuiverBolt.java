package com.oblivioussp.spartanweaponry.inventory;

import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;

public class ContainerQuiverBolt extends ContainerQuiverArrow {
   public ContainerQuiverBolt(InventoryPlayer inventoryPlayer, ItemStack quiverStack) {
      super(inventoryPlayer, quiverStack);
   }

   protected void addInventorySlots() {
      int slotStartX = 53;
      int slotStartY = 20;
      if (this.handler.getSlots() == 6) {
         slotStartX = 35;
      } else if (this.handler.getSlots() == 9) {
         slotStartX = 8;
      }

      for(int i = 0; i < this.handler.getSlots(); ++i) {
         this.addSlotToContainer(new SlotBolt(this.handler, i, slotStartX + 18 * i, slotStartY));
      }

   }
}
