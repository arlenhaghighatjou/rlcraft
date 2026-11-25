package com.lycanitesmobs.core.container;

import com.lycanitesmobs.core.item.equipment.ItemEquipment;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

public class EquipmentStationEquipmentSlot extends BaseSlot {
   public EquipmentStationContainer container;

   public EquipmentStationEquipmentSlot(EquipmentStationContainer container, int slotIndex, int x, int y) {
      super(container.equipmentStation, slotIndex, x, y);
      this.container = container;
   }

   public boolean isItemValid(ItemStack itemStack) {
      return itemStack.getItem() instanceof ItemEquipment;
   }

   public boolean getHasStack() {
      return super.getHasStack();
   }

   public int getSlotStackLimit() {
      return 1;
   }

   public void putStack(ItemStack itemStack) {
      super.putStack(itemStack);
      this.container.attemptRepair();
   }

   public ItemStack onTake(EntityPlayer player, ItemStack itemStack) {
      return super.onTake(player, itemStack);
   }

   public boolean canTakeStack(EntityPlayer player) {
      return true;
   }
}
