package de.impelon.disenchanter.inventory;

import de.impelon.disenchanter.DisenchantingUtils;
import net.minecraft.item.ItemStack;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.items.wrapper.RangedWrapper;

public class DisenchantmentItemStackHandler extends ItemStackHandler implements IDisenchantmentItemHandler {
   public static final int SOURCE_SLOT = 0;
   public static final int RECEIVER_SLOT = 1;
   public static final int OUTPUT_SLOT = 2;
   protected static final int NUMBER_OF_SLOTS = 3;
   protected IItemHandlerModifiable inputInventory = new RangedWrapper(this, 0, 3) {
      public ItemStack insertItem(int slot, ItemStack stack, boolean simulate) {
         return slot != 0 && slot != 1 ? stack : super.insertItem(slot, stack, simulate);
      }

      public ItemStack extractItem(int slot, int amount, boolean simulate) {
         return slot != 2 ? ItemStack.EMPTY : super.extractItem(slot, amount, simulate);
      }
   };
   protected IItemHandlerModifiable outputInventory = new RangedWrapper(this, 0, 3) {
      public ItemStack extractItem(int slot, int amount, boolean simulate) {
         return slot != 0 && slot != 2 ? ItemStack.EMPTY : super.extractItem(slot, amount, simulate);
      }
   };

   public DisenchantmentItemStackHandler() {
      super(3);
   }

   public IItemHandlerModifiable getInputInventory() {
      return this.inputInventory;
   }

   public IItemHandlerModifiable getOutputInventory() {
      return this.outputInventory;
   }

   public int getSlotLimit(int slot) {
      return 1;
   }

   public boolean isItemValid(int slot, ItemStack stack) {
      switch(slot) {
      case 0:
         return !DisenchantingUtils.getAvailableEnchantmentIndices(stack).isEmpty();
      case 1:
         return !DisenchantingUtils.getAppropriateResultTarget(stack).isEmpty();
      default:
         return super.isItemValid(slot, stack);
      }
   }

   public ItemStack insertItem(int slot, ItemStack stack, boolean simulate) {
      return !this.isItemValid(slot, stack) ? stack : super.insertItem(slot, stack, simulate);
   }

   public void setSourceStack(ItemStack stack) {
      this.setStackInSlot(0, stack);
   }

   public void setReceiverStack(ItemStack stack) {
      this.setStackInSlot(1, stack);
   }

   public void setOutputStack(ItemStack stack) {
      this.setStackInSlot(2, stack);
   }

   public ItemStack getSourceStack() {
      return this.getStackInSlot(0);
   }

   public ItemStack getReceiverStack() {
      return this.getStackInSlot(1);
   }

   public ItemStack getOutputStack() {
      return this.getStackInSlot(2);
   }
}
