package com.lycanitesmobs.core.container;

import com.lycanitesmobs.core.item.equipment.ItemEquipmentPart;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;

public class EquipmentInfuserPartSlot extends BaseSlot {
   public EquipmentInfuserContainer container;

   public EquipmentInfuserPartSlot(EquipmentInfuserContainer infuserContainer, int slotIndex, int x, int y) {
      super(infuserContainer.equipmentInfuser, slotIndex, x, y);
      this.container = infuserContainer;
   }

   public boolean isItemValid(ItemStack itemStack) {
      return itemStack.getItem() instanceof ItemEquipmentPart || itemStack.getItem() == Items.GLASS_BOTTLE;
   }

   public boolean getHasStack() {
      return super.getHasStack();
   }

   public int getSlotStackLimit() {
      return 1;
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
