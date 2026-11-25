package com.lycanitesmobs.core.container;

import com.lycanitesmobs.core.entity.BaseCreatureEntity;
import com.lycanitesmobs.core.entity.RideableCreatureEntity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;

public class CreatureContainer extends BaseContainer {
   public BaseCreatureEntity creature;

   public CreatureContainer(BaseCreatureEntity creature, InventoryPlayer playerInventory) {
      this.addPlayerSlots(playerInventory, 0, 0);
      this.creature = creature;
      this.specialStart = this.inventorySlots.size();
      this.drawCreatureEquipment(creature, 8, 18);
      this.specialFinish = this.inventorySlots.size() - 1;
      this.inventoryStart = this.inventorySlots.size();
      if (creature.inventory.getItemSlotsSize() > 0) {
         this.addSlotsByColumn(creature.inventory, 80, 18, 5, 0, creature.inventory.getActiveItemSlotsSize() - 1);
      }

      this.inventoryFinish = this.inventorySlots.size() - 1;
   }

   public void drawCreatureEquipment(BaseCreatureEntity creature, int equipX, int equipY) {
      if (creature instanceof RideableCreatureEntity) {
         this.addSlot(creature.inventory, creature.inventory.getSlotFromType("saddle"), equipX, equipY);
         equipY += 18;
      }

      if (creature.getBagSize() > 0) {
         this.addSlot(creature.inventory, creature.inventory.getSlotFromType("bag"), equipX, equipY);
         equipY = equipY + 18;
      }

      equipX = equipX + 18;
      equipY = 18;
      if (creature.inventory.useAdvancedArmor()) {
         this.addSlot(creature.inventory, creature.inventory.getSlotFromType("head"), equipX, equipY);
         equipY += 18;
      }

      this.addSlot(creature.inventory, creature.inventory.getSlotFromType("chest"), equipX, equipY);
      equipY = equipY + 18;
      if (creature.inventory.useAdvancedArmor()) {
         this.addSlot(creature.inventory, creature.inventory.getSlotFromType("legs"), equipX, equipY);
         equipY = equipY + 18;
         this.addSlot(creature.inventory, creature.inventory.getSlotFromType("feet"), equipX, equipY);
         equipY = equipY + 18;
      }

   }

   public void onContainerClosed(EntityPlayer player) {
      super.onContainerClosed(player);
   }
}
