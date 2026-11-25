package com.lycanitesmobs.core.container;

import com.lycanitesmobs.core.info.ItemManager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

public class EquipmentStationRepairSlot extends BaseSlot {
   public EquipmentStationContainer container;

   public EquipmentStationRepairSlot(EquipmentStationContainer container, int slotIndex, int x, int y) {
      super(container.equipmentStation, slotIndex, x, y);
      this.container = container;
   }

   public boolean isItemValid(ItemStack itemStack) {
      return ItemManager.getInstance().getEquipmentSharpnessRepair(itemStack) > 0 || ItemManager.getInstance().getEquipmentManaRepair(itemStack) > 0;
   }

   public boolean getHasStack() {
      return super.getHasStack();
   }

   public int getSlotStackLimit() {
      return 64;
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
