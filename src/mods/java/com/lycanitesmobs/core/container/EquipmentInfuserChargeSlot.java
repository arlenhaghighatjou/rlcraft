package com.lycanitesmobs.core.container;

import com.lycanitesmobs.core.item.ChargeItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemDye;
import net.minecraft.item.ItemStack;

public class EquipmentInfuserChargeSlot extends BaseSlot {
   public EquipmentInfuserContainer container;

   public EquipmentInfuserChargeSlot(EquipmentInfuserContainer infuserContainer, int slotIndex, int x, int y) {
      super(infuserContainer.equipmentInfuser, slotIndex, x, y);
      this.container = infuserContainer;
   }

   public boolean isItemValid(ItemStack itemStack) {
      return itemStack.getItem() instanceof ChargeItem || itemStack.getItem() instanceof ItemDye || itemStack.getItem() == Items.WATER_BUCKET || itemStack.getItem() == Items.BUCKET;
   }

   public boolean getHasStack() {
      return super.getHasStack();
   }

   public int getSlotStackLimit() {
      return 64;
   }

   public void putStack(ItemStack itemStack) {
      super.putStack(itemStack);
      this.container.attemptInfusion();
   }

   public ItemStack onTake(EntityPlayer player, ItemStack itemStack) {
      return super.onTake(player, itemStack);
   }

   public boolean canTakeStack(EntityPlayer player) {
      return true;
   }
}
