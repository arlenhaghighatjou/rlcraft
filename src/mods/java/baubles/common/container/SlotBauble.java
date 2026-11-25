package baubles.common.container;

import baubles.api.IBauble;
import baubles.api.cap.BaublesCapabilities;
import baubles.api.cap.IBaublesItemHandler;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.items.SlotItemHandler;

public class SlotBauble extends SlotItemHandler {
   int baubleSlot;
   EntityPlayer player;

   public SlotBauble(EntityPlayer player, IBaublesItemHandler itemHandler, int slot, int par4, int par5) {
      super(itemHandler, slot, par4, par5);
      this.baubleSlot = slot;
      this.player = player;
   }

   public boolean isItemValid(ItemStack stack) {
      return ((IBaublesItemHandler)this.getItemHandler()).isItemValidForSlot(this.baubleSlot, stack, this.player);
   }

   public boolean canTakeStack(EntityPlayer player) {
      ItemStack stack = this.getStack();
      if (stack.isEmpty()) {
         return false;
      } else {
         IBauble bauble = (IBauble)stack.getCapability(BaublesCapabilities.CAPABILITY_ITEM_BAUBLE, (EnumFacing)null);
         return bauble.canUnequip(stack, player);
      }
   }

   public ItemStack onTake(EntityPlayer playerIn, ItemStack stack) {
      if (!this.getHasStack() && !((IBaublesItemHandler)this.getItemHandler()).isEventBlocked() && stack.hasCapability(BaublesCapabilities.CAPABILITY_ITEM_BAUBLE, (EnumFacing)null)) {
         ((IBauble)stack.getCapability(BaublesCapabilities.CAPABILITY_ITEM_BAUBLE, (EnumFacing)null)).onUnequipped(stack, playerIn);
      }

      super.onTake(playerIn, stack);
      return stack;
   }

   public void putStack(ItemStack stack) {
      if (this.getHasStack() && !ItemStack.areItemStacksEqual(stack, this.getStack()) && !((IBaublesItemHandler)this.getItemHandler()).isEventBlocked() && this.getStack().hasCapability(BaublesCapabilities.CAPABILITY_ITEM_BAUBLE, (EnumFacing)null)) {
         ((IBauble)this.getStack().getCapability(BaublesCapabilities.CAPABILITY_ITEM_BAUBLE, (EnumFacing)null)).onUnequipped(this.getStack(), this.player);
      }

      ItemStack oldstack = this.getStack().copy();
      super.putStack(stack);
      if (this.getHasStack() && !ItemStack.areItemStacksEqual(oldstack, this.getStack()) && !((IBaublesItemHandler)this.getItemHandler()).isEventBlocked() && this.getStack().hasCapability(BaublesCapabilities.CAPABILITY_ITEM_BAUBLE, (EnumFacing)null)) {
         ((IBauble)this.getStack().getCapability(BaublesCapabilities.CAPABILITY_ITEM_BAUBLE, (EnumFacing)null)).onEquipped(this.getStack(), this.player);
      }

   }

   public int getSlotStackLimit() {
      return 1;
   }
}
