package ejektaflex.bountiful.gui;

import ejektaflex.bountiful.block.TileEntityBountyBoard;
import ejektaflex.bountiful.data.BountyData;
import ejektaflex.bountiful.item.ItemBounty;
import kotlin.Metadata;
import kotlin.jvm.internal.Intrinsics;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.SlotItemHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(
   mv = {1, 1, 13},
   bv = {1, 0, 3},
   k = 1,
   d1 = {"\u00000\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u000b\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\b\n\u0000\u0018\u00002\u00020\u0001B\u0015\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0005\u00a2\u0006\u0002\u0010\u0006J\u0010\u0010\u0007\u001a\u00020\b2\u0006\u0010\t\u001a\u00020\nH\u0016J\u001a\u0010\u000b\u001a\u00020\f2\b\u0010\t\u001a\u0004\u0018\u00010\n2\u0006\u0010\r\u001a\u00020\u000eH\u0016\u00a8\u0006\u000f"},
   d2 = {"Lejektaflex/bountiful/gui/ContainerBoard;", "Lnet/minecraft/inventory/Container;", "playerInv", "Lnet/minecraft/entity/player/InventoryPlayer;", "boardTE", "Lejektaflex/bountiful/block/TileEntityBountyBoard;", "(Lnet/minecraft/entity/player/InventoryPlayer;Lejektaflex/bountiful/block/TileEntityBountyBoard;)V", "canInteractWith", "", "player", "Lnet/minecraft/entity/player/EntityPlayer;", "transferStackInSlot", "Lnet/minecraft/item/ItemStack;", "index", "", "Bountiful"}
)
public final class ContainerBoard extends Container {
   public boolean canInteractWith(@NotNull EntityPlayer player) {
      Intrinsics.checkParameterIsNotNull(player, "player");
      return true;
   }

   @NotNull
   public ItemStack transferStackInSlot(@Nullable EntityPlayer player, int index) {
      ItemStack itemstack = ItemStack.EMPTY;
      Slot slot = (Slot)this.inventorySlots.get(index);
      if (slot != null && slot.getHasStack()) {
         ItemStack itemstack1 = slot.getStack();
         itemstack = itemstack1.copy();
         int var10000 = this.inventorySlots.size();
         if (player == null) {
            Intrinsics.throwNpe();
         }

         int containerSlots = var10000 - player.inventory.mainInventory.size();
         if (index < containerSlots) {
            if (!this.mergeItemStack(itemstack1, containerSlots, this.inventorySlots.size(), true)) {
               ItemStack var7 = ItemStack.EMPTY;
               Intrinsics.checkExpressionValueIsNotNull(ItemStack.EMPTY, "ItemStack.EMPTY");
               return var7;
            }
         } else if (!this.mergeItemStack(itemstack1, 0, containerSlots, false)) {
            ItemStack var10 = ItemStack.EMPTY;
            Intrinsics.checkExpressionValueIsNotNull(ItemStack.EMPTY, "ItemStack.EMPTY");
            return var10;
         }

         Intrinsics.checkExpressionValueIsNotNull(itemstack1, "itemstack1");
         if (itemstack1.getCount() == 0) {
            slot.putStack(ItemStack.EMPTY);
         } else {
            slot.onSlotChanged();
         }

         var10000 = itemstack1.getCount();
         Intrinsics.checkExpressionValueIsNotNull(itemstack, "itemstack");
         if (var10000 == itemstack.getCount()) {
            ItemStack var9 = ItemStack.EMPTY;
            Intrinsics.checkExpressionValueIsNotNull(ItemStack.EMPTY, "ItemStack.EMPTY");
            return var9;
         }

         slot.onTake(player, itemstack1);
      }

      Intrinsics.checkExpressionValueIsNotNull(itemstack, "itemstack");
      return itemstack;
   }

   public ContainerBoard(@NotNull InventoryPlayer playerInv, @NotNull final TileEntityBountyBoard boardTE) {
      Intrinsics.checkParameterIsNotNull(playerInv, "playerInv");
      Intrinsics.checkParameterIsNotNull(boardTE, "boardTE");
      super();
      Capability var10001 = CapabilityItemHandler.ITEM_HANDLER_CAPABILITY;
      Intrinsics.checkExpressionValueIsNotNull(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, "CapabilityItemHandler.ITEM_HANDLER_CAPABILITY");
      final IItemHandler inventory = (IItemHandler)boardTE.getCapability(var10001, EnumFacing.NORTH);
      final int i1 = 0;

      for(byte var5 = 3; i1 < var5; ++i1) {
         final int k = 0;

         for(byte var7 = 9; k < var7; ++k) {
            this.addSlotToContainer((Slot)(new SlotItemHandler(inventory, k + i1 * 9, 8 + k * 18, 18 + i1 * 18) {
               public void onSlotChanged() {
                  boardTE.markDirty();
               }

               public boolean isItemValid(@NotNull ItemStack stack) {
                  Intrinsics.checkParameterIsNotNull(stack, "stack");
                  return stack.getItem() instanceof ItemBounty ? BountyData.Companion.isValidBounty(stack) : false;
               }
            }));
         }
      }

      i1 = 0;

      for(byte var10 = 2; i1 <= var10; ++i1) {
         int var12 = 0;

         for(byte var13 = 8; var12 <= var13; ++var12) {
            this.addSlotToContainer(new Slot((IInventory)playerInv, var12 + i1 * 9 + 9, 8 + var12 * 18, 103 + i1 * 18 - 19));
         }
      }

      i1 = 0;

      for(byte var11 = 8; i1 <= var11; ++i1) {
         this.addSlotToContainer(new Slot((IInventory)playerInv, i1, 8 + i1 * 18, 142));
      }

   }
}
