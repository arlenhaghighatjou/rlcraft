package com.lycanitesmobs.core.container;

import com.lycanitesmobs.ObjectManager;
import com.lycanitesmobs.core.item.equipment.ItemEquipment;
import com.lycanitesmobs.core.item.equipment.ItemEquipmentPart;
import com.lycanitesmobs.core.tileentity.TileEntityEquipmentForge;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

public class EquipmentForgeContainer extends BaseContainer {
   public TileEntityEquipmentForge equipmentForge;

   public EquipmentForgeContainer(TileEntityEquipmentForge equipmentForge, InventoryPlayer playerInventory) {
      this.equipmentForge = equipmentForge;
      this.addPlayerSlots(playerInventory, 0, 0);
      this.inventoryStart = this.inventorySlots.size();
      int slots = 0;
      if (equipmentForge.getSizeInventory() > 0) {
         int slotSize = 18;
         int x = 8 + slotSize;
         int y = 38;
         EquipmentForgeSlot slotEquipmentPiece = new EquipmentForgeSlot(this, slots++, x + slotSize * 6, y, "piece");
         this.addSlotToContainer(slotEquipmentPiece);
         EquipmentForgeSlot slotEquipmentBase = new EquipmentForgeSlot(this, slots++, x + slotSize, y, "base");
         this.addSlotToContainer(slotEquipmentBase);
         EquipmentForgeSlot slotEquipmentHead = new EquipmentForgeSlot(this, slots++, x + slotSize * 2, y, "none");
         this.addSlotToContainer(slotEquipmentHead);
         slotEquipmentBase.addChildSlot(slotEquipmentHead);
         EquipmentForgeSlot slotEquipmentTipA = new EquipmentForgeSlot(this, slots++, x + slotSize * 3, y, "none");
         this.addSlotToContainer(slotEquipmentTipA);
         slotEquipmentHead.addChildSlot(slotEquipmentTipA);
         EquipmentForgeSlot slotEquipmentTipB = new EquipmentForgeSlot(this, slots++, x + slotSize * 2, y - slotSize, "none");
         this.addSlotToContainer(slotEquipmentTipB);
         slotEquipmentHead.addChildSlot(slotEquipmentTipB);
         EquipmentForgeSlot slotEquipmentTipC = new EquipmentForgeSlot(this, slots++, x + slotSize * 2, y + slotSize, "none");
         this.addSlotToContainer(slotEquipmentTipC);
         slotEquipmentHead.addChildSlot(slotEquipmentTipC);
         EquipmentForgeSlot slotEquipmentPommel = new EquipmentForgeSlot(this, slots++, x, y, "none");
         this.addSlotToContainer(slotEquipmentPommel);
         slotEquipmentBase.addChildSlot(slotEquipmentPommel);
      }

      this.inventoryFinish = this.inventoryStart + slots;
   }

   public boolean canInteractWith(EntityPlayer player) {
      return this.equipmentForge != null && this.equipmentForge.isUsableByPlayer(player);
   }

   public void onEquipmentPieceSlotChanged(EquipmentForgeSlot slotEquipment) {
      if (this.equipmentForge != null) {
         this.clearPartSlots();
         EquipmentForgeSlot slotBase = (EquipmentForgeSlot)this.getSlot(this.inventoryStart + 1);
         if (slotEquipment.getHasStack() && !slotBase.getHasStack() && slotEquipment.getStack().getItem() instanceof ItemEquipment) {
            EquipmentForgeSlot slotHead = (EquipmentForgeSlot)this.getSlot(this.inventoryStart + 2);
            EquipmentForgeSlot slotTipA = (EquipmentForgeSlot)this.getSlot(this.inventoryStart + 3);
            EquipmentForgeSlot slotTipB = (EquipmentForgeSlot)this.getSlot(this.inventoryStart + 4);
            EquipmentForgeSlot slotTipC = (EquipmentForgeSlot)this.getSlot(this.inventoryStart + 5);
            EquipmentForgeSlot slotPommel = (EquipmentForgeSlot)this.getSlot(this.inventoryStart + 6);
            ItemEquipment itemEquipment = (ItemEquipment)slotEquipment.getStack().getItem();
            int axeIndex = 0;

            for(ItemStack partStack : itemEquipment.getEquipmentPartStacks(slotEquipment.getStack())) {
               ItemEquipmentPart itemEquipmentPart = itemEquipment.getEquipmentPart(partStack);
               if (itemEquipmentPart != null) {
                  if ("base".equals(itemEquipmentPart.slotType)) {
                     slotBase.putStackWithoutUpdate(partStack);
                  } else if ("head".equals(itemEquipmentPart.slotType)) {
                     slotHead.putStackWithoutUpdate(partStack);
                  } else if (!"blade".equals(itemEquipmentPart.slotType) && !"pike".equals(itemEquipmentPart.slotType) && !"jewel".equals(itemEquipmentPart.slotType)) {
                     if ("axe".equals(itemEquipmentPart.slotType)) {
                        if (axeIndex++ == 0) {
                           slotTipB.putStackWithoutUpdate(partStack);
                        } else {
                           slotTipC.putStackWithoutUpdate(partStack);
                        }
                     } else if ("pommel".equals(itemEquipmentPart.slotType)) {
                        slotPommel.putStackWithoutUpdate(partStack);
                     }
                  } else {
                     slotTipA.putStackWithoutUpdate(partStack);
                  }
               }
            }
         }

      }
   }

   public void onEquipmentPartSlotChanged(EquipmentForgeSlot slotEquipment) {
      if (this.equipmentForge != null) {
         Slot slotPiece = this.getSlot(this.inventoryStart);
         Slot slotBase = this.getSlot(this.inventoryStart + 1);
         Slot slotHead = this.getSlot(this.inventoryStart + 2);
         Slot slotTipA = this.getSlot(this.inventoryStart + 3);
         Slot slotTipB = this.getSlot(this.inventoryStart + 4);
         Slot slotTipC = this.getSlot(this.inventoryStart + 5);
         Slot slotPommel = this.getSlot(this.inventoryStart + 6);
         ItemEquipment itemEquipment = (ItemEquipment)ObjectManager.getItem("equipment");
         ItemStack pieceStack = new ItemStack(itemEquipment);
         if (slotBase.getHasStack()) {
            itemEquipment.addEquipmentPart(pieceStack, slotBase.getStack(), 0);
         }

         if (slotHead.getHasStack()) {
            itemEquipment.addEquipmentPart(pieceStack, slotHead.getStack(), 1);
         }

         if (slotTipA.getHasStack()) {
            itemEquipment.addEquipmentPart(pieceStack, slotTipA.getStack(), 2);
         }

         if (slotTipB.getHasStack()) {
            itemEquipment.addEquipmentPart(pieceStack, slotTipB.getStack(), 3);
         }

         if (slotTipC.getHasStack()) {
            itemEquipment.addEquipmentPart(pieceStack, slotTipC.getStack(), 4);
         }

         if (slotPommel.getHasStack()) {
            itemEquipment.addEquipmentPart(pieceStack, slotPommel.getStack(), 5);
         }

         if (itemEquipment.getEquipmentPartCount(pieceStack) > 0) {
            slotPiece.putStack(pieceStack);
         } else {
            slotPiece.putStack(ItemStack.EMPTY);
         }

      }
   }

   public void clearPartSlots() {
      for(int i = 1; i <= 6; ++i) {
         Slot slot = this.getSlot(this.inventoryStart + i);
         if (slot instanceof EquipmentForgeSlot) {
            EquipmentForgeSlot slotEquipment = (EquipmentForgeSlot)slot;
            slotEquipment.putStackWithoutUpdate(ItemStack.EMPTY);
         }
      }

   }

   public boolean isEquipmentValid() {
      Slot slotBase = this.getSlot(this.inventoryStart + 1);
      Slot slotHead = this.getSlot(this.inventoryStart + 2);
      Slot slotTipA = this.getSlot(this.inventoryStart + 3);
      Slot slotTipB = this.getSlot(this.inventoryStart + 4);
      Slot slotTipC = this.getSlot(this.inventoryStart + 5);
      if (slotBase.getHasStack() && slotHead.getHasStack()) {
         return slotTipA.getHasStack() || slotTipB.getHasStack() || slotTipC.getHasStack();
      } else {
         return false;
      }
   }

   public ItemStack transferStackInSlot(EntityPlayer player, int slotID) {
      return ItemStack.EMPTY;
   }
}
