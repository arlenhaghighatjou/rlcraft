package net.silentchaos512.lib.inventory;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

public class ContainerSL extends Container {
   protected final IInventory tileInventory;

   public ContainerSL(InventoryPlayer playerInventory, IInventory tileInventory) {
      this.tileInventory = tileInventory;
      this.addTileInventorySlots(tileInventory);
      this.addPlayerInventorySlots(playerInventory);
   }

   protected void addTileInventorySlots(IInventory inv) {
   }

   protected void addPlayerInventorySlots(InventoryPlayer inv) {
      for(int i = 0; i < 3; ++i) {
         for(int j = 0; j < 9; ++j) {
            this.addSlotToContainer(new Slot(inv, j + i * 9 + 9, 8 + j * 18, 84 + i * 18));
         }
      }

      for(int var4 = 0; var4 < 9; ++var4) {
         this.addSlotToContainer(new Slot(inv, var4, 8 + var4 * 18, 142));
      }

   }

   public boolean canInteractWith(EntityPlayer player) {
      return this.tileInventory.isUsableByPlayer(player);
   }

   /** @deprecated */
   @Deprecated
   public static void onTakeFromSlot(Slot slot, EntityPlayer player, ItemStack stack) {
      slot.onTake(player, stack);
   }
}
