package rustic.common.tileentity;

import invtweaks.api.container.ChestContainer;
import javax.annotation.Nullable;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.SlotItemHandler;

@ChestContainer
public class ContainerCabinet extends Container {
   private TileEntityCabinet te;

   public ContainerCabinet(IInventory playerInventory, TileEntityCabinet te, EntityPlayer player) {
      this.te = te;
      te.openInventory(player);
      this.addOwnSlots();
      this.addPlayerSlots(playerInventory);
   }

   public TileEntityCabinet getTile() {
      return this.te;
   }

   public void onContainerClosed(EntityPlayer playerIn) {
      super.onContainerClosed(playerIn);
      this.getTile().closeInventory(playerIn);
   }

   private void addPlayerSlots(IInventory playerInventory) {
      for(int row = 0; row < 3; ++row) {
         for(int col = 0; col < 9; ++col) {
            int x = 8 + col * 18;
            int y = row * 18 + 84;
            this.addSlotToContainer(new Slot(playerInventory, col + row * 9 + 9, x, y));
         }
      }

      for(int row = 0; row < 9; ++row) {
         int x = 8 + row * 18;
         int y = 142;
         this.addSlotToContainer(new Slot(playerInventory, row, x, y));
      }

   }

   private void addOwnSlots() {
      IItemHandler itemHandler = (IItemHandler)this.te.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, (EnumFacing)null);
      int slotIndex = 0;

      for(int row = 0; row < 3; ++row) {
         for(int col = 0; col < 9; ++col) {
            int x = 8 + col * 18;
            int y = row * 18 + 17;
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
         if (index < 27) {
            if (!this.mergeItemStack(itemstack1, 27, this.inventorySlots.size(), true)) {
               return ItemStack.EMPTY;
            }
         } else if (!this.mergeItemStack(itemstack1, 0, 27, false)) {
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
      return this.te.canInteractWith(playerIn);
   }
}
