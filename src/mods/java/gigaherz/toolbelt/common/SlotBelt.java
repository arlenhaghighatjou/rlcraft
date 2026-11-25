package gigaherz.toolbelt.common;

import gigaherz.toolbelt.Config;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandlerModifiable;

public class SlotBelt extends Slot {
   public SlotBelt(final IInventory playerInventory, final int blockedSlot, final int index, int xPosition, int yPosition) {
      super(new IInventory() {
         final IInventory sourceInventory = playerInventory;
         final int slot = blockedSlot;
         final int subSlot = index;
         ItemStack beltStack = null;
         IItemHandlerModifiable inventory = null;

         Optional<IItemHandlerModifiable> findStack() {
            ItemStack stack = this.sourceInventory.getStackInSlot(this.slot);
            if (stack != this.beltStack) {
               this.beltStack = stack;
               this.inventory = (IItemHandlerModifiable)stack.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, (EnumFacing)null);
            }

            return Optional.ofNullable(this.inventory);
         }

         public String getName() {
            return "toolbelt.slot.wrapper";
         }

         public boolean hasCustomName() {
            return false;
         }

         public ITextComponent getDisplayName() {
            return null;
         }

         public int getSizeInventory() {
            return 1;
         }

         public boolean isEmpty() {
            return this.getStackInSlot(0).getCount() <= 0;
         }

         public ItemStack getStackInSlot(int n) {
            return (ItemStack)this.findStack().map((inv) -> {
               return inv.getStackInSlot(this.subSlot);
            }).orElse(ItemStack.EMPTY);
         }

         public ItemStack decrStackSize(int n, int count) {
            return (ItemStack)this.findStack().map((inv) -> {
               return inv.extractItem(this.subSlot, count, false);
            }).orElse(ItemStack.EMPTY);
         }

         public ItemStack removeStackFromSlot(int n) {
            return ItemStack.EMPTY;
         }

         public void setInventorySlotContents(int n, ItemStack stack) {
            this.findStack().ifPresent((inv) -> {
               inv.setStackInSlot(this.subSlot, stack);
            });
         }

         public int getInventoryStackLimit() {
            return ((Integer)this.findStack().map((inv) -> {
               return inv.getSlotLimit(this.subSlot);
            }).orElse(Integer.valueOf(0))).intValue();
         }

         public void markDirty() {
         }

         public boolean isUsableByPlayer(EntityPlayer player) {
            return false;
         }

         public void openInventory(EntityPlayer player) {
         }

         public void closeInventory(EntityPlayer player) {
         }

         public boolean isItemValidForSlot(int indexx, ItemStack stack) {
            return Config.isItemStackAllowed(stack);
         }

         public int getField(int id) {
            return 0;
         }

         public void setField(int id, int value) {
         }

         public int getFieldCount() {
            return 0;
         }

         public void clear() {
         }
      }, blockedSlot, xPosition, yPosition);
   }

   public boolean isItemValid(ItemStack stack) {
      return Config.isItemStackAllowed(stack);
   }
}
