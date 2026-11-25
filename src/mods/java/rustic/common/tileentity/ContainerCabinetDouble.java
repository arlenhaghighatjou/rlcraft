package rustic.common.tileentity;

import invtweaks.api.container.ChestContainer;
import javax.annotation.Nullable;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.SlotItemHandler;

@ChestContainer(
   isLargeChest = true
)
public class ContainerCabinetDouble extends Container {
   private TileEntityCabinet teTop;
   private TileEntityCabinet teBottom;

   public ContainerCabinetDouble(IInventory playerInventory, TileEntityCabinet teTop, TileEntityCabinet teBottom, EntityPlayer player) {
      this.teTop = teTop;
      this.teBottom = teBottom;
      teBottom.openInventory(player);
      this.addOwnSlots();
      this.addPlayerSlots(playerInventory);
   }

   public TileEntityCabinet getTileTop() {
      return this.teTop;
   }

   public TileEntityCabinet getTileBottom() {
      return this.teBottom;
   }

   public void onContainerClosed(EntityPlayer playerIn) {
      super.onContainerClosed(playerIn);
      this.getTileBottom().closeInventory(playerIn);
   }

   private void addPlayerSlots(IInventory playerInventory) {
      for(int row = 0; row < 3; ++row) {
         for(int col = 0; col < 9; ++col) {
            int x = 8 + col * 18;
            int y = row * 18 + 140;
            this.addSlotToContainer(new Slot(playerInventory, col + row * 9 + 9, x, y));
         }
      }

      for(int row = 0; row < 9; ++row) {
         int x = 8 + row * 18;
         int y = 198;
         this.addSlotToContainer(new Slot(playerInventory, row, x, y));
      }

   }

   private void addOwnSlots() {
      IItemHandler itemHandler = this.teTop.getSingleCabinetHandler();
      int slotIndex = 0;

      for(int row = 0; row < 3; ++row) {
         for(int col = 0; col < 9; ++col) {
            int x = 8 + col * 18;
            int y = row * 18 + 18;
            this.addSlotToContainer(new SlotItemHandler(itemHandler, slotIndex, x, y));
            ++slotIndex;
         }
      }

      itemHandler = this.teBottom.getSingleCabinetHandler();
      slotIndex = 0;

      for(int row = 3; row < 6; ++row) {
         for(int col = 0; col < 9; ++col) {
            int x = 8 + col * 18;
            int y = row * 18 + 18;
            this.addSlotToContainer(new SlotItemHandler(itemHandler, slotIndex, x, y));
            ++slotIndex;
         }
      }

   }

   @Nullable
   public ItemStack transferStackInSlot(EntityPlayer playerIn, int index) {
      ItemStack itemstack = ItemStack.EMPTY;
      Slot slot = (Slot)this.inventorySlots.get(index);
      if (slot != null && slot.getHasStack()) {
         ItemStack itemstack1 = slot.getStack();
         itemstack = itemstack1.copy();
         if (index < 54) {
            if (!this.mergeItemStack(itemstack1, 54, this.inventorySlots.size(), true)) {
               return ItemStack.EMPTY;
            }
         } else if (!this.mergeItemStack(itemstack1, 0, 54, false)) {
            return ItemStack.EMPTY;
         }

         if (itemstack1.isEmpty()) {
            slot.putStack(ItemStack.EMPTY);
         } else {
            slot.onSlotChanged();
         }
      }

      return itemstack;
   }

   public boolean canInteractWith(EntityPlayer playerIn) {
      return this.teTop.canInteractWith(playerIn) && this.teBottom.canInteractWith(playerIn);
   }
}
