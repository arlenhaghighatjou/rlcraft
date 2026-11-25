package cursedflames.bountifulbaubles.block;

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

public abstract class GenericContainer extends Container {
   protected GenericTileEntity te;

   public GenericContainer(IInventory playerInventory, GenericTileEntity te) {
      this(playerInventory, te, true);
   }

   public GenericContainer(IInventory playerInventory, GenericTileEntity te, boolean initSlots) {
      this.te = te;
      if (initSlots) {
         this.addOwnSlots();
         this.addPlayerSlots(playerInventory);
      }

   }

   protected void addPlayerSlots(IInventory playerInventory) {
      for(int row = 0; row < 3; ++row) {
         for(int col = 0; col < 9; ++col) {
            int x = 9 + col * 18;
            int y = row * 18 + 70;
            this.addSlotToContainer(new Slot(playerInventory, col + row * 9 + 10, x, y));
         }
      }

      for(int row = 0; row < 9; ++row) {
         int x = 9 + row * 18;
         int y = 128;
         this.addSlotToContainer(new Slot(playerInventory, row, x, y));
      }

   }

   protected void addOwnSlots() {
      IItemHandler itemHandler = (IItemHandler)this.te.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, (EnumFacing)null);
      int x = 9;
      int y = 6;
      this.addSlotToContainer(new SlotItemHandler(itemHandler, 0, x, y));
   }

   @Nullable
   public ItemStack transferStackInSlot(EntityPlayer playerIn, int index) {
      ItemStack itemstack = ItemStack.EMPTY;
      Slot slot = (Slot)this.inventorySlots.get(index);
      if (slot != null && slot.getHasStack()) {
         ItemStack itemstack1 = slot.getStack();
         itemstack = itemstack1.copy();
         if (index == 0) {
            if (!this.mergeItemStack(itemstack1, 1, this.inventorySlots.size(), true)) {
               return ItemStack.EMPTY;
            }
         } else if (!this.mergeItemStack(itemstack1, 0, 1, false)) {
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
