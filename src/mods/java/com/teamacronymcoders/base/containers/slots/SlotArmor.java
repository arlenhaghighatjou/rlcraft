package com.teamacronymcoders.base.containers.slots;

import com.teamacronymcoders.base.util.ItemStackUtils;
import javax.annotation.Nullable;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class SlotArmor extends Slot {
   private EntityEquipmentSlot entityEquipmentSlot;
   private EntityPlayer entityPlayer;

   public SlotArmor(EntityPlayer entityPlayer, EntityEquipmentSlot entityEquipmentSlot, int xPos, int yPos) {
      super(entityPlayer.inventory, 36 + (3 - entityEquipmentSlot.ordinal()), xPos, yPos);
      this.entityEquipmentSlot = entityEquipmentSlot;
      this.entityPlayer = entityPlayer;
   }

   public int getSlotStackLimit() {
      return 1;
   }

   public boolean isItemValid(ItemStack itemStack) {
      return ItemStackUtils.isValid(itemStack) && itemStack.getItem().isValidArmor(itemStack, this.entityEquipmentSlot, this.entityPlayer);
   }

   @Nullable
   @SideOnly(Side.CLIENT)
   public String getSlotTexture() {
      return ItemArmor.EMPTY_SLOT_NAMES[this.entityEquipmentSlot.getIndex()];
   }
}
