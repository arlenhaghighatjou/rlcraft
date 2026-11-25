package com.lycanitesmobs.core.container;

import com.lycanitesmobs.core.inventory.InventoryCreature;
import com.lycanitesmobs.core.item.equipment.ItemEquipment;
import com.lycanitesmobs.core.item.equipment.ItemEquipmentPart;
import com.lycanitesmobs.core.item.equipment.features.EquipmentFeature;
import com.lycanitesmobs.core.item.equipment.features.SlotEquipmentFeature;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class EquipmentForgeSlot extends BaseSlot {
   public String type;
   public EquipmentForgeContainer containerForge;
   public List<EquipmentForgeSlot> childSlots = new ArrayList();

   public EquipmentForgeSlot(EquipmentForgeContainer containerForge, int slotIndex, int x, int y, String type) {
      super(containerForge.equipmentForge, slotIndex, x, y);
      this.containerForge = containerForge;
      this.type = type;
   }

   public boolean isItemValid(ItemStack itemStack) {
      Item item = itemStack.getItem();
      if (item instanceof ItemEquipmentPart) {
         ItemEquipmentPart equipmentPart = (ItemEquipmentPart)item;
         return this.type.equals(equipmentPart.slotType) && equipmentPart.getLevel(itemStack) <= this.containerForge.equipmentForge.getLevel();
      } else if (item instanceof ItemEquipment) {
         if (this.containerForge.equipmentForge.getLevel() < 3) {
            ItemEquipment equipment = (ItemEquipment)item;
            int equipmentLevel = equipment.getHighestLevel(itemStack);
            if (equipmentLevel > this.containerForge.equipmentForge.getLevel()) {
               return false;
            }
         }

         return this.type.equals("piece") && !this.getHasStack();
      } else {
         return false;
      }
   }

   public boolean getHasStack() {
      if (!super.getHasStack()) {
         return false;
      } else {
         if (this.getStack().getItem() instanceof ItemEquipment) {
            ItemEquipment itemEquipment = (ItemEquipment)this.getStack().getItem();
            if (itemEquipment.getEquipmentPartCount(this.getStack()) == 0) {
               return false;
            }
         }

         return true;
      }
   }

   public int getSlotStackLimit() {
      return this.inventory instanceof InventoryCreature && ((InventoryCreature)this.inventory).getTypeFromSlot(this.getSlotIndex()) != null ? 1 : this.inventory.getInventoryStackLimit();
   }

   public void setType(String type) {
      this.type = type;
   }

   public void addChildSlot(EquipmentForgeSlot slot) {
      if (!this.childSlots.contains(slot)) {
         this.childSlots.add(slot);
         this.updateChildSlots();
      }
   }

   public boolean updateChildSlot(int index, String type) {
      if (index >= this.childSlots.size()) {
         return false;
      } else {
         ((EquipmentForgeSlot)this.childSlots.get(index)).setType(type);
         return true;
      }
   }

   public void putStack(ItemStack itemStack) {
      this.putStackWithoutUpdate(itemStack);
      if ("piece".equals(this.type)) {
         this.containerForge.onEquipmentPieceSlotChanged(this);
      } else {
         this.containerForge.onEquipmentPartSlotChanged(this);
      }

   }

   public void putStackWithoutUpdate(ItemStack itemStack) {
      super.putStack(itemStack);
      if (!this.childSlots.isEmpty()) {
         this.updateChildSlots();
      }

   }

   public ItemStack onTake(EntityPlayer player, ItemStack itemStack) {
      Item item = itemStack.getItem();
      if (item instanceof ItemEquipmentPart) {
         this.updateChildSlots();
      }

      if (item instanceof ItemEquipment) {
         int forgeLevel = this.containerForge.equipmentForge.getLevel();
         if (forgeLevel < 3) {
            ((ItemEquipment)item).applyLevelCap(itemStack, this.containerForge.equipmentForge.getLevel());
         }
      }

      if ("piece".equals(this.type)) {
         this.containerForge.onEquipmentPieceSlotChanged(this);
      } else {
         this.containerForge.onEquipmentPartSlotChanged(this);
      }

      return super.onTake(player, itemStack);
   }

   public boolean canTakeStack(EntityPlayer player) {
      if (!this.childSlots.isEmpty()) {
         for(EquipmentForgeSlot childSlot : this.childSlots) {
            if (!childSlot.getStack().isEmpty()) {
               return false;
            }
         }
      } else if ("piece".equals(this.type) && this.getHasStack() && !this.containerForge.isEquipmentValid()) {
         return false;
      }

      return true;
   }

   public void updateChildSlots() {
      Item item = this.getStack().getItem();
      List<Integer> updatedChildSlots = new ArrayList();
      if (item instanceof ItemEquipmentPart) {
         ItemEquipmentPart itemEquipmentPart = (ItemEquipmentPart)item;
         int axeSlots = 0;

         for(EquipmentFeature feature : itemEquipmentPart.features) {
            if (feature instanceof SlotEquipmentFeature) {
               SlotEquipmentFeature slotFeature = (SlotEquipmentFeature)feature;
               int level = itemEquipmentPart.getLevel(this.getStack());
               if (slotFeature.isActive(this.getStack(), level)) {
                  int index = 0;
                  if (slotFeature.slotType.equals("axe")) {
                     ++axeSlots;
                     index = axeSlots;
                  } else if (slotFeature.slotType.equals("pommel")) {
                     index = 1;
                  }

                  if (!updatedChildSlots.contains(Integer.valueOf(index)) && this.updateChildSlot(index, slotFeature.slotType)) {
                     updatedChildSlots.add(Integer.valueOf(index));
                  }
               }
            }
         }
      }

      for(int index = 0; index < this.childSlots.size(); ++index) {
         if (!updatedChildSlots.contains(Integer.valueOf(index))) {
            this.updateChildSlot(index, "none");
         }
      }

   }
}
