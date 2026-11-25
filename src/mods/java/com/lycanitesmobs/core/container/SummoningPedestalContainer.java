package com.lycanitesmobs.core.container;

import com.lycanitesmobs.core.tileentity.TileEntitySummoningPedestal;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;

public class SummoningPedestalContainer extends BaseContainer {
   public TileEntitySummoningPedestal summoningPedestal;

   public SummoningPedestalContainer(TileEntitySummoningPedestal summoningPedestal, InventoryPlayer playerInventory) {
      this.summoningPedestal = summoningPedestal;
      this.inventoryStart = this.inventorySlots.size();
      this.playerInventoryStart = this.inventorySlots.size();
      this.addSlotGrid(playerInventory, 8, 171, 1, 0, 8);
      int slots = 0;
      if (summoningPedestal.getSizeInventory() > 0) {
         this.addSlotToContainer(new BaseSlot(summoningPedestal, slots++, 93, 43));
      }

      this.inventoryFinish = this.inventoryStart + slots;
   }

   public boolean canInteractWith(EntityPlayer player) {
      if (this.summoningPedestal != null && this.summoningPedestal.isUsableByPlayer(player)) {
         return player == this.summoningPedestal.getPlayer();
      } else {
         return false;
      }
   }

   public ItemStack transferStackInSlot(EntityPlayer player, int slotID) {
      return ItemStack.EMPTY;
   }
}
