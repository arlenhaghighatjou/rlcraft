package com.alcatrazescapee.notreepunching.common.container;

import com.alcatrazescapee.alcatrazcore.inventory.container.ContainerItemStack;
import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.ClickType;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.SlotItemHandler;

@ParametersAreNonnullByDefault
public class ContainerSmallVessel extends ContainerItemStack {
   private final int itemDragIndex;

   public ContainerSmallVessel(InventoryPlayer playerInv, ItemStack stack) {
      super(playerInv, stack, 4);
      this.itemDragIndex = playerInv.currentItem;
   }

   protected void addContainerSlots() {
      IItemHandler cap = (IItemHandler)this.stack.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, (EnumFacing)null);
      if (cap != null) {
         this.addSlotToContainer(new SlotItemHandler(cap, 0, 71, 23));
         this.addSlotToContainer(new SlotItemHandler(cap, 1, 89, 23));
         this.addSlotToContainer(new SlotItemHandler(cap, 2, 71, 41));
         this.addSlotToContainer(new SlotItemHandler(cap, 3, 89, 41));
      }

   }

   @Nonnull
   public ItemStack slotClick(int slotID, int dragType, ClickType clickType, EntityPlayer player) {
      if (slotID != this.itemIndex || clickType != ClickType.QUICK_MOVE && clickType != ClickType.PICKUP && clickType != ClickType.THROW && clickType != ClickType.SWAP) {
         return dragType == this.itemDragIndex && clickType == ClickType.SWAP ? ItemStack.EMPTY : super.slotClick(slotID, dragType, clickType, player);
      } else {
         return ItemStack.EMPTY;
      }
   }
}
