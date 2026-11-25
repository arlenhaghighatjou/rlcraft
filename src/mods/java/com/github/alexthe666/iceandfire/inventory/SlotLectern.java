package com.github.alexthe666.iceandfire.inventory;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

public class SlotLectern extends Slot {
   private EntityPlayer thePlayer;
   private int removeCount;

   public SlotLectern(EntityPlayer player, IInventory inv, int slotIndex, int xPosition, int yPosition) {
      super(inv, slotIndex, xPosition, yPosition);
      this.thePlayer = player;
   }

   public boolean isItemValid(ItemStack stack) {
      return false;
   }

   public ItemStack decrStackSize(int amount) {
      if (this.getHasStack()) {
         this.removeCount += Math.min(amount, this.getStack().getCount());
      }

      return super.decrStackSize(amount);
   }

   public ItemStack onTake(EntityPlayer playerIn, ItemStack stack) {
      this.onCrafting(stack);
      return super.onTake(playerIn, stack);
   }

   protected void onCrafting(ItemStack stack, int amount) {
      this.removeCount += amount;
      this.onCrafting(stack);
   }

   protected void onCrafting(ItemStack stack) {
      this.removeCount = 0;
   }
}
