package com.alcatrazescapee.alcatrazcore.inventory.container;

import com.alcatrazescapee.alcatrazcore.tile.ITileFields;
import com.alcatrazescapee.alcatrazcore.tile.TileInventory;
import javax.annotation.Nonnull;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IContainerListener;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public abstract class ContainerTileInventory<T extends TileInventory> extends Container {
   protected final T tile;
   protected final InventoryPlayer playerInv;
   private final boolean shouldSendFields;
   private int[] cachedFields;

   public ContainerTileInventory(InventoryPlayer playerInv, T tile) {
      this(playerInv, tile, 0, 0);
   }

   public ContainerTileInventory(InventoryPlayer playerInv, T tile, int playerSlotOffsetX, int playerSlotOffsetY) {
      this.tile = tile;
      this.playerInv = playerInv;
      this.shouldSendFields = tile instanceof ITileFields;
      this.addContainerSlots();
      this.addPlayerInventorySlots(playerInv, playerSlotOffsetX, playerSlotOffsetY);
   }

   public void detectAndSendChanges() {
      super.detectAndSendChanges();
      if (this.shouldSendFields) {
         this.detectAndSendFieldChanges();
      }

   }

   @Nonnull
   public ItemStack transferStackInSlot(EntityPlayer player, int index) {
      Slot slot = (Slot)this.inventorySlots.get(index);
      if (slot != null && slot.getHasStack()) {
         ItemStack stack = slot.getStack();
         ItemStack stackCopy = stack.copy();
         int containerSlots = this.inventorySlots.size() - player.inventory.mainInventory.size();
         if (index < containerSlots) {
            if (!this.mergeItemStack(stack, containerSlots, this.inventorySlots.size(), true)) {
               return ItemStack.EMPTY;
            }

            this.tile.setAndUpdateSlots(index);
         } else {
            for(int i = 0; i < containerSlots; ++i) {
               if (((Slot)this.inventorySlots.get(i)).isItemValid(stack) && this.mergeItemStack(stack, i, i + 1, false)) {
                  this.tile.setAndUpdateSlots(i);
               }
            }
         }

         if (stack.getCount() == 0) {
            slot.putStack(ItemStack.EMPTY);
         } else {
            slot.onSlotChanged();
         }

         if (stack.getCount() == stackCopy.getCount()) {
            return ItemStack.EMPTY;
         } else {
            slot.onTake(player, stack);
            return stackCopy;
         }
      } else {
         return ItemStack.EMPTY;
      }
   }

   @SideOnly(Side.CLIENT)
   public void updateProgressBar(int id, int data) {
      if (this.shouldSendFields) {
         ((ITileFields)this.tile).setField(id, data);
      }

   }

   public boolean canInteractWith(@Nonnull EntityPlayer player) {
      return true;
   }

   protected void detectAndSendFieldChanges() {
      ITileFields tileFields = (ITileFields)this.tile;
      boolean allFieldsHaveChanged = false;
      boolean[] fieldHasChanged = new boolean[tileFields.getFieldCount()];
      if (this.cachedFields == null) {
         this.cachedFields = new int[tileFields.getFieldCount()];
         allFieldsHaveChanged = true;
      }

      for(int i = 0; i < this.cachedFields.length; ++i) {
         if (allFieldsHaveChanged || this.cachedFields[i] != tileFields.getField(i)) {
            this.cachedFields[i] = tileFields.getField(i);
            fieldHasChanged[i] = true;
         }
      }

      for(IContainerListener listener : this.listeners) {
         for(int fieldID = 0; fieldID < tileFields.getFieldCount(); ++fieldID) {
            if (fieldHasChanged[fieldID]) {
               listener.sendWindowProperty(this, fieldID, this.cachedFields[fieldID]);
            }
         }
      }

   }

   protected void detectAndSendAllChanges() {
      for(int i = 0; i < this.inventorySlots.size(); ++i) {
         ItemStack stack = ((Slot)this.inventorySlots.get(i)).getStack();
         ItemStack stackOld = (ItemStack)this.inventoryItemStacks.get(i);
         if (!ItemStack.areItemStacksEqual(stackOld, stack)) {
            stackOld = stack.isEmpty() ? ItemStack.EMPTY : stack.copy();
            this.inventoryItemStacks.set(i, stackOld);

            for(IContainerListener listener : this.listeners) {
               listener.sendSlotContents(this, i, stackOld);
            }
         }
      }

   }

   protected abstract void addContainerSlots();

   protected void addPlayerInventorySlots(InventoryPlayer playerInv, int offsetX, int offsetY) {
      for(int i = 0; i < 3; ++i) {
         for(int j = 0; j < 9; ++j) {
            this.addSlotToContainer(new Slot(playerInv, j + i * 9 + 9, offsetX + 8 + j * 18, offsetY + 84 + i * 18));
         }
      }

      for(int k = 0; k < 9; ++k) {
         this.addSlotToContainer(new Slot(playerInv, k, offsetX + 8 + k * 18, offsetY + 142));
      }

   }
}
