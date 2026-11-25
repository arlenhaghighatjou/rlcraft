package vazkii.arl.util;

import javax.annotation.Nonnull;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandlerModifiable;

public class InventoryIIH implements IItemHandlerModifiable {
   private final IItemHandlerModifiable iih;
   final ItemStack stack;

   public InventoryIIH(ItemStack stack) {
      this.stack = stack;
      this.iih = (IItemHandlerModifiable)stack.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, (EnumFacing)null);
   }

   public void setStackInSlot(int slot, @Nonnull ItemStack stack) {
      this.iih.setStackInSlot(slot, stack);
   }

   public int getSlots() {
      return this.iih.getSlots();
   }

   @Nonnull
   public ItemStack getStackInSlot(int slot) {
      return this.iih.getStackInSlot(slot);
   }

   @Nonnull
   public ItemStack insertItem(int slot, @Nonnull ItemStack stack, boolean simulate) {
      return this.iih.insertItem(slot, stack, simulate);
   }

   @Nonnull
   public ItemStack extractItem(int slot, int amount, boolean simulate) {
      return this.iih.extractItem(slot, amount, simulate);
   }

   public int getSlotLimit(int slot) {
      return this.iih.getSlotLimit(slot);
   }
}
