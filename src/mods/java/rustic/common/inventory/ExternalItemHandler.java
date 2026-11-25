package rustic.common.inventory;

import net.minecraft.item.ItemStack;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.ItemStackHandler;

public class ExternalItemHandler implements IItemHandler, IItemHandlerModifiable {
   private ItemStackHandler internalHandler;

   public ExternalItemHandler(ItemStackHandler internalHandler) {
      this.internalHandler = internalHandler;
   }

   public int getSlots() {
      return this.internalHandler.getSlots();
   }

   public ItemStack getStackInSlot(int slot) {
      return this.internalHandler.getStackInSlot(slot);
   }

   public ItemStack insertItem(int slot, ItemStack stack, boolean simulate) {
      return this.internalHandler.insertItem(slot, stack, simulate);
   }

   public ItemStack extractItem(int slot, int amount, boolean simulate) {
      return this.internalHandler.extractItem(slot, amount, simulate);
   }

   public int getSlotLimit(int slot) {
      return this.internalHandler.getSlotLimit(slot);
   }

   public void setStackInSlot(int slot, ItemStack stack) {
      this.internalHandler.setStackInSlot(slot, stack);
   }
}
