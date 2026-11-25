package vazkii.arl.container;

import javax.annotation.Nonnull;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

public abstract class ContainerBasic<T extends IInventory> extends Container {
   protected final T tile;
   protected final int tileSlots;

   public ContainerBasic(InventoryPlayer playerInv, T tile) {
      this.tile = tile;
      this.tileSlots = this.addSlots();

      for(int i = 0; i < 3; ++i) {
         for(int j = 0; j < 9; ++j) {
            this.addSlotToContainer(new Slot(playerInv, j + i * 9 + 9, 8 + j * 18, 84 + i * 18));
         }
      }

      for(int k = 0; k < 9; ++k) {
         this.addSlotToContainer(new Slot(playerInv, k, 8 + k * 18, 142));
      }

   }

   public abstract int addSlots();

   public boolean canInteractWith(@Nonnull EntityPlayer playerIn) {
      return this.tile.isUsableByPlayer(playerIn);
   }

   @Nonnull
   public ItemStack transferStackInSlot(EntityPlayer playerIn, int index) {
      ItemStack itemstack = ItemStack.EMPTY;
      Slot slot = (Slot)this.inventorySlots.get(index);
      if (slot != null && slot.getHasStack()) {
         ItemStack itemstack1 = slot.getStack();
         itemstack = itemstack1.copy();
         if (index < this.tileSlots) {
            if (!this.mergeItemStack(itemstack1, this.tileSlots, this.inventorySlots.size(), true)) {
               return ItemStack.EMPTY;
            }
         } else if (!this.mergeItemStack(itemstack1, 0, this.tileSlots, false)) {
            return ItemStack.EMPTY;
         }

         if (itemstack1.isEmpty()) {
            slot.putStack(ItemStack.EMPTY);
         } else {
            slot.onSlotChanged();
         }
      }

      return itemstack;
   }

   public boolean mergeItemStack(ItemStack stack, int start, int length, boolean r) {
      boolean successful = false;
      int i = !r ? start : length - 1;
      int iterOrder = !r ? 1 : -1;
      if (stack.isStackable()) {
         for(; stack.getCount() > 0 && (!r && i < length || r && i >= start); i += iterOrder) {
            Slot slot = (Slot)this.inventorySlots.get(i);
            ItemStack existingStack = slot.getStack();
            if (!existingStack.isEmpty()) {
               int maxStack = Math.min(stack.getMaxStackSize(), slot.getSlotStackLimit());
               int rmv = Math.min(maxStack, stack.getCount());
               if (slot.isItemValid(cloneStack(stack, rmv)) && existingStack.getItem().equals(stack.getItem()) && (!stack.getHasSubtypes() || stack.getItemDamage() == existingStack.getItemDamage()) && ItemStack.areItemStackTagsEqual(stack, existingStack)) {
                  int existingSize = existingStack.getCount() + stack.getCount();
                  if (existingSize <= maxStack) {
                     stack.setCount(0);
                     existingStack.setCount(existingSize);
                     slot.putStack(existingStack);
                     successful = true;
                  } else if (existingStack.getCount() < maxStack) {
                     stack.shrink(maxStack - existingStack.getCount());
                     existingStack.setCount(maxStack);
                     slot.putStack(existingStack);
                     successful = true;
                  }
               }
            }
         }
      }

      if (stack.getCount() > 0) {
         for(int var13 = !r ? start : length - 1; stack.getCount() > 0 && (!r && var13 < length || r && var13 >= start); var13 += iterOrder) {
            Slot slot = (Slot)this.inventorySlots.get(var13);
            ItemStack existingStack = slot.getStack();
            if (existingStack.isEmpty()) {
               int maxStack = Math.min(stack.getMaxStackSize(), slot.getSlotStackLimit());
               int rmv = Math.min(maxStack, stack.getCount());
               if (slot.isItemValid(cloneStack(stack, rmv))) {
                  existingStack = stack.splitStack(rmv);
                  slot.putStack(existingStack);
                  successful = true;
               }
            }
         }
      }

      return successful;
   }

   private static ItemStack cloneStack(ItemStack stack, int size) {
      if (stack.isEmpty()) {
         return ItemStack.EMPTY;
      } else {
         ItemStack copy = stack.copy();
         copy.setCount(size);
         return copy;
      }
   }
}
