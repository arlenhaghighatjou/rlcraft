package xzeroair.trinkets.container;

import baubles.api.IBauble;
import baubles.api.cap.BaublesCapabilities;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.items.ItemStackHandler;
import xzeroair.trinkets.Trinkets;
import xzeroair.trinkets.capabilities.InventoryContainerCapability.ITrinketContainerHandler;
import xzeroair.trinkets.util.TrinketsConfig;
import xzeroair.trinkets.util.interfaces.IAccessoryInterface;

public class TrinketContainerHandler extends ItemStackHandler implements ITrinketContainerHandler {
   private static int slots;
   private boolean[] changed;
   private boolean blockEvents;
   private EntityLivingBase player;

   public TrinketContainerHandler() {
      super(slots);
      this.changed = new boolean[slots];
      this.blockEvents = false;
   }

   public void setSize(int size) {
      if (size < slots || size > slots) {
         size = slots;
      }

      super.setSize(size);
      boolean[] old = this.changed;
      this.changed = new boolean[size];

      for(int i = 0; i < old.length && i < this.changed.length; ++i) {
         this.changed[i] = old[i];
      }

   }

   public void setStackInSlot(int slot, ItemStack stack) {
      if (stack == null || stack.isEmpty() || this.isItemValidForSlot(slot, stack, this.player)) {
         super.setStackInSlot(slot, stack);
      }

   }

   public ItemStack insertItem(int slot, ItemStack stack, boolean simulate) {
      return !this.isItemValidForSlot(slot, stack, this.player) ? stack : super.insertItem(slot, stack, simulate);
   }

   public void setPlayer(EntityLivingBase player) {
      this.player = player;
   }

   public boolean isItemValidForSlot(int slot, ItemStack stack, EntityLivingBase player) {
      if (stack != null && !stack.isEmpty() && stack.getItem() instanceof IAccessoryInterface) {
         if (stack.getItem() instanceof IAccessoryInterface) {
            IAccessoryInterface trinket = (IAccessoryInterface)stack.getItem();
            return trinket.canEquipAccessory(stack, player);
         } else if (TrinketsConfig.compat.baubles && Trinkets.Baubles && stack.getItem() instanceof IBauble) {
            IBauble bauble = (IBauble)stack.getCapability(BaublesCapabilities.CAPABILITY_ITEM_BAUBLE, (EnumFacing)null);
            return true;
         } else {
            return false;
         }
      } else {
         return false;
      }
   }

   public boolean isEventBlocked() {
      return this.blockEvents;
   }

   public void setEventBlock(boolean blockEvents) {
      this.blockEvents = blockEvents;
   }

   static {
      slots = TrinketsConfig.SERVER.GUI.guiSlotsRows;
   }
}
