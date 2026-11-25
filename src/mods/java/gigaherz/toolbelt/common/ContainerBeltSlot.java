package gigaherz.toolbelt.common;

import gigaherz.toolbelt.BeltFinder;
import gigaherz.toolbelt.ToolBelt;
import gigaherz.toolbelt.customslots.IExtensionSlot;
import gigaherz.toolbelt.customslots.SlotExtension;
import gigaherz.toolbelt.slot.ExtensionSlotBelt;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.ContainerPlayer;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;

public class ContainerBeltSlot extends ContainerPlayer {
   public static final ResourceLocation BACKGROUND_ICON = ToolBelt.location("gui/empty_belt_slot_background");
   private final SlotExtension slotBelt;
   private final IExtensionSlot extensionSlot;

   public ContainerBeltSlot(InventoryPlayer playerInventory, boolean localWorld, EntityPlayer playerIn) {
      super(playerInventory, localWorld, playerIn);
      ExtensionSlotBelt container = (ExtensionSlotBelt)playerIn.getCapability(ExtensionSlotBelt.CAPABILITY, (EnumFacing)null);
      this.extensionSlot = container.getBelt();
      this.addSlotToContainer(this.slotBelt = new SlotExtension(this.extensionSlot, 77, 44));
      this.slotBelt.setBackgroundName(BACKGROUND_ICON.toString());
   }

   public void onContainerClosed(EntityPlayer playerIn) {
      super.onContainerClosed(playerIn);
      if (!playerIn.world.isRemote) {
         BeltFinder.sendSync(playerIn);
      }

   }

   public boolean canInteractWith(EntityPlayer playerIn) {
      return true;
   }

   public ItemStack transferStackInSlot(EntityPlayer playerIn, int index) {
      Slot slot = (Slot)this.inventorySlots.get(index);
      if (slot != null && slot.getHasStack()) {
         ItemStack remaining = ItemStack.EMPTY;
         ItemStack slotContents = slot.getStack();
         remaining = slotContents.copy();
         if (index == this.slotBelt.slotNumber) {
            if (!this.mergeItemStack(slotContents, 9, 45, false)) {
               return ItemStack.EMPTY;
            }

            return remaining;
         }

         if (slot.isItemValid(slotContents) && !this.mergeItemStack(slotContents, this.slotBelt.slotNumber, this.slotBelt.slotNumber + 1, false)) {
            return ItemStack.EMPTY;
         }
      }

      return super.transferStackInSlot(playerIn, index);
   }
}
