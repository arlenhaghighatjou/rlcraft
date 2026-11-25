package ivorius.reccomplex.gui;

import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

public class SlotDynamicIndex extends Slot {
   public int slotIndex;

   public SlotDynamicIndex(IInventory inventory, int slotIndex, int x, int y) {
      super(inventory, slotIndex, x, y);
      this.slotIndex = slotIndex;
   }

   public ItemStack getStack() {
      return this.inventory.getStackInSlot(this.slotIndex);
   }

   public void putStack(ItemStack par1ItemStack) {
      this.inventory.setInventorySlotContents(this.slotIndex, par1ItemStack);
      this.onSlotChanged();
   }

   public ItemStack decrStackSize(int par1) {
      return this.inventory.decrStackSize(this.slotIndex, par1);
   }

   public boolean isHere(IInventory par1IInventory, int par2) {
      return par1IInventory == this.inventory && par2 == this.slotIndex;
   }

   public int getSlotIndex() {
      return this.slotIndex;
   }
}
