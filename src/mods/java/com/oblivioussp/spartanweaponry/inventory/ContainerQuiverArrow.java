package com.oblivioussp.spartanweaponry.inventory;

import com.oblivioussp.spartanweaponry.ModSpartanWeaponry;
import com.oblivioussp.spartanweaponry.item.ItemQuiverBase;
import com.oblivioussp.spartanweaponry.util.QuiverHelper;
import javax.annotation.Nonnull;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.ClickType;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;

public class ContainerQuiverArrow extends Container {
   protected final ItemStack quiver;
   protected final IItemHandler handler;
   protected int playerInvStart;
   protected int playerInvEnd;
   protected int hotbarStart;
   protected int hotbarEnd;

   public ContainerQuiverArrow(InventoryPlayer inventoryPlayer, @Nonnull ItemStack quiverStack) {
      this.quiver = quiverStack;
      this.handler = (IItemHandler)this.quiver.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, (EnumFacing)null);
      this.playerInvStart = this.handler.getSlots();
      this.playerInvEnd = this.playerInvStart + 26;
      this.hotbarStart = this.playerInvEnd + 1;
      this.hotbarEnd = this.hotbarStart + 8;
      this.addInventorySlots();
      this.addPlayerSlots(inventoryPlayer);
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
         this.addSlotToContainer(new SlotArrow(this.handler, i, slotStartX + 18 * i, slotStartY));
      }

   }

   protected void addPlayerSlots(InventoryPlayer inventoryPlayer) {
      for(int i = 0; i < 3; ++i) {
         for(int j = 0; j < 9; ++j) {
            this.addSlotToContainer(new Slot(inventoryPlayer, 9 + i * 9 + j, 8 + j * 18, 51 + i * 18));
         }
      }

      for(int i = 0; i < 9; ++i) {
         this.addSlotToContainer(new Slot(inventoryPlayer, i, 8 + i * 18, 109));
      }

      this.addSlotToContainer(new Slot(inventoryPlayer, 40, -21, 109) {
         public String getSlotTexture() {
            return "minecraft:items/empty_armor_slot_shield";
         }
      });
   }

   public boolean canInteractWith(EntityPlayer player) {
      return player.inventory.hasItemStack(this.quiver) || ModSpartanWeaponry.isBaublesLoaded && QuiverHelper.isInBaublesSlot(player, this.quiver);
   }

   public ItemStack transferStackInSlot(EntityPlayer player, int slotIdx) {
      ItemStack stack = ItemStack.EMPTY;
      Slot slot = (Slot)this.inventorySlots.get(slotIdx);
      if (slot != null && slot.getHasStack()) {
         ItemStack slotStack = slot.getStack();
         stack = slotStack.copy();
         if (slotIdx >= 0 && slotIdx < this.handler.getSlots()) {
            if (!this.mergeItemStack(slotStack, this.hotbarStart, this.hotbarEnd + 1, false) && !this.mergeItemStack(slotStack, this.playerInvStart, this.playerInvEnd + 1, false)) {
               return ItemStack.EMPTY;
            }
         } else if (slotIdx >= this.playerInvStart && slotIdx <= this.hotbarEnd && slot.isItemValid(stack) && !this.mergeItemStack(slotStack, 0, this.playerInvStart, false)) {
            return ItemStack.EMPTY;
         }

         if (slotStack.getCount() == 0) {
            slot.putStack(ItemStack.EMPTY);
         } else {
            slot.onSlotChanged();
         }

         if (slotStack.getCount() == stack.getCount()) {
            return ItemStack.EMPTY;
         }

         slot.onTake(player, slotStack);
      }

      return stack;
   }

   public ItemStack slotClick(int slot, int dragType, ClickType clickType, EntityPlayer player) {
      return slot >= 0 && this.getSlot(slot) != null && this.getSlot(slot).getStack().getItem() instanceof ItemQuiverBase ? ItemStack.EMPTY : super.slotClick(slot, dragType, clickType, player);
   }
}
