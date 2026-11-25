package xzeroair.trinkets.container;

import baubles.api.IBauble;
import baubles.api.cap.BaublesCapabilities;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.items.SlotItemHandler;
import xzeroair.trinkets.Trinkets;
import xzeroair.trinkets.capabilities.InventoryContainerCapability.ITrinketContainerHandler;
import xzeroair.trinkets.util.TrinketsConfig;
import xzeroair.trinkets.util.interfaces.IAccessoryInterface;

public class TrinketSlot extends SlotItemHandler {
   protected EntityPlayer player;

   public TrinketSlot(EntityPlayer player, ITrinketContainerHandler itemHandler, int index, int xPosition, int yPosition) {
      super(itemHandler, index, xPosition, yPosition);
      this.player = player;
   }

   public boolean isItemValid(ItemStack stack) {
      return ((ITrinketContainerHandler)this.getItemHandler()).isItemValidForSlot(this.getSlotIndex(), stack, this.player);
   }

   public boolean canTakeStack(EntityPlayer player) {
      ItemStack stack = this.getStack();
      if (stack.isEmpty()) {
         return false;
      } else {
         return stack.getItem() instanceof IAccessoryInterface ? ((IAccessoryInterface)stack.getItem()).canUnequipAccessory(stack, player) : true;
      }
   }

   public ItemStack onTake(EntityPlayer player, ItemStack stack) {
      if (!this.getHasStack() && !((ITrinketContainerHandler)this.getItemHandler()).isEventBlocked()) {
         if (stack.getItem() instanceof IAccessoryInterface) {
            ((IAccessoryInterface)stack.getItem()).onAccessoryUnequipped(stack, player);
         } else if (Trinkets.Baubles && !TrinketsConfig.compat.xatItemsInTrinketGuiOnly && stack.hasCapability(BaublesCapabilities.CAPABILITY_ITEM_BAUBLE, (EnumFacing)null)) {
            ((IBauble)stack.getCapability(BaublesCapabilities.CAPABILITY_ITEM_BAUBLE, (EnumFacing)null)).onUnequipped(stack, player);
         }
      }

      return super.onTake(player, stack);
   }

   public void putStack(ItemStack stack) {
      ItemStack currentStack = this.getStack();
      if (this.getHasStack() && !ItemStack.areItemStacksEqual(stack, currentStack) && !((ITrinketContainerHandler)this.getItemHandler()).isEventBlocked()) {
         if (currentStack.getItem() instanceof IAccessoryInterface) {
            ((IAccessoryInterface)currentStack.getItem()).onAccessoryUnequipped(currentStack, this.player);
         } else if (Trinkets.Baubles && !TrinketsConfig.compat.xatItemsInTrinketGuiOnly && this.getStack().hasCapability(BaublesCapabilities.CAPABILITY_ITEM_BAUBLE, (EnumFacing)null)) {
            ((IBauble)this.getStack().getCapability(BaublesCapabilities.CAPABILITY_ITEM_BAUBLE, (EnumFacing)null)).onUnequipped(currentStack, this.player);
         }
      }

      ItemStack oldstack = this.getStack().copy();
      super.putStack(stack);
      currentStack = this.getStack();
      if (this.getHasStack() && !ItemStack.areItemStacksEqual(oldstack, currentStack) && !((ITrinketContainerHandler)this.getItemHandler()).isEventBlocked()) {
         if (currentStack.getItem() instanceof IAccessoryInterface) {
            ((IAccessoryInterface)currentStack.getItem()).onAccessoryEquipped(currentStack, this.player);
         } else if (Loader.isModLoaded("baubles") && !TrinketsConfig.compat.xatItemsInTrinketGuiOnly && this.getStack().hasCapability(BaublesCapabilities.CAPABILITY_ITEM_BAUBLE, (EnumFacing)null)) {
            ((IBauble)this.getStack().getCapability(BaublesCapabilities.CAPABILITY_ITEM_BAUBLE, (EnumFacing)null)).onEquipped(currentStack, this.player);
         }
      }

   }

   public int getSlotStackLimit() {
      return 1;
   }
}
