package betterquesting.blocks;

import javax.annotation.Nonnull;
import net.minecraft.item.ItemStack;
import net.minecraftforge.items.IItemHandlerModifiable;

public class SSItemHandler implements IItemHandlerModifiable {
   private final TileSubmitStation tile;

   public SSItemHandler(TileSubmitStation tile) {
      this.tile = tile;
   }

   public int getSlots() {
      return this.tile.getSizeInventory();
   }

   @Nonnull
   public ItemStack insertItem(int slot, @Nonnull ItemStack stack, boolean simulate) {
      if (!stack.isEmpty() && this.tile.isItemValidForSlot(slot, stack)) {
         ItemStack ts1 = this.getStackInSlot(slot);
         if (!ts1.isEmpty() && !stack.isItemEqual(ts1)) {
            return stack;
         } else {
            int inMax = Math.min(stack.getCount(), stack.getMaxStackSize() - ts1.getCount());
            ItemStack ts2 = stack.copy();
            ts2.setCount(inMax);
            if (!simulate) {
               if (ts1.isEmpty()) {
                  ts1 = ts2;
               } else {
                  ts1.grow(ts2.getCount());
               }

               this.tile.setInventorySlotContents(slot, ts1);
            }

            if (stack.getCount() > inMax) {
               ItemStack ts3 = stack.copy();
               ts3.setCount(stack.getCount() - inMax);
               return ts3;
            } else {
               return ItemStack.EMPTY;
            }
         }
      } else {
         return stack;
      }
   }

   @Nonnull
   public ItemStack extractItem(int slot, int amount, boolean simulate) {
      if (slot == 1 && amount > 0) {
         if (!simulate) {
            return this.tile.decrStackSize(slot, amount);
         } else {
            ItemStack stack = this.getStackInSlot(slot);
            if (stack.isEmpty()) {
               return ItemStack.EMPTY;
            } else {
               int outMax = Math.min(stack.getCount(), amount);
               ItemStack ts1 = stack.copy();
               ts1.setCount(outMax);
               return ts1;
            }
         }
      } else {
         return ItemStack.EMPTY;
      }
   }

   public void setStackInSlot(int slot, @Nonnull ItemStack stack) {
      this.tile.setInventorySlotContents(slot, stack);
   }

   @Nonnull
   public ItemStack getStackInSlot(int idx) {
      return this.tile.getStackInSlot(idx);
   }

   public int getSlotLimit(int slot) {
      return 64;
   }
}
