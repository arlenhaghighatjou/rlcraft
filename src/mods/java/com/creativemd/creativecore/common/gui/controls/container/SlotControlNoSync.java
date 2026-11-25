package com.creativemd.creativecore.common.gui.controls.container;

import net.minecraft.inventory.Slot;
import net.minecraft.nbt.NBTTagCompound;

public class SlotControlNoSync extends SlotControl {
   public SlotControlNoSync(Slot slot) {
      super(slot);
   }

   public void sendPacket(NBTTagCompound nbt) {
   }

   public void writeToNBTUpdate(NBTTagCompound nbt) {
   }

   public void onPacketReceive(NBTTagCompound nbt) {
   }

   public void sendUpdate() {
   }

   public void onSlotClicked(int mouseButton, boolean shift, int scrolled) {
   }
}
