package vazkii.quark.oddities.inventory;

import javax.annotation.Nonnull;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.ClickType;
import net.minecraft.inventory.ContainerPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.inventory.Slot;
import net.minecraft.inventory.EntityEquipmentSlot.Type;
import net.minecraft.item.ItemStack;
import vazkii.arl.util.InventoryIIH;
import vazkii.quark.oddities.feature.Backpacks;

public class ContainerBackpack extends ContainerPlayer {
   public ContainerBackpack(EntityPlayer player) {
      super(player.inventory, !player.world.isRemote, player);

      for(Slot slot : this.inventorySlots) {
         if (slot.inventory == player.inventory && slot.getSlotIndex() < player.inventory.getSizeInventory() - 5) {
            slot.yPos += 58;
         }
      }

      Slot anchor = (Slot)this.inventorySlots.get(9);
      int left = anchor.xPos;
      int top = anchor.yPos - 58;
      ItemStack backpack = (ItemStack)player.inventory.armorInventory.get(2);
      if (backpack.getItem() == Backpacks.backpack) {
         InventoryIIH inv = new InventoryIIH(backpack);

         for(int i = 0; i < 3; ++i) {
            for(int j = 0; j < 9; ++j) {
               int k = j + i * 9;
               this.addSlotToContainer(new SlotCachingItemHandler(inv, k, left + j * 18, top + i * 18));
            }
         }
      }

   }

   @Nonnull
   public ItemStack transferStackInSlot(@Nonnull EntityPlayer playerIn, int index) {
      ItemStack baseStack = ItemStack.EMPTY;
      Slot slot = (Slot)this.inventorySlots.get(index);
      if (slot != null && slot.getHasStack()) {
         ItemStack stack = slot.getStack();
         baseStack = stack.copy();
         EntityEquipmentSlot slotType = EntityLiving.getSlotForItemStack(baseStack);
         int equipIndex = 8 - slotType.getIndex();
         if (index == 0) {
            if (!this.mergeItemStack(stack, 9, 45, false) && !this.mergeItemStack(stack, 46, 73, false)) {
               return ItemStack.EMPTY;
            }

            slot.onSlotChange(stack, baseStack);
         } else if (index < 5) {
            if (!this.mergeItemStack(stack, 9, 45, false)) {
               return ItemStack.EMPTY;
            }
         } else if (index < 9) {
            if (!this.mergeItemStack(stack, 9, 45, false) && !this.mergeItemStack(stack, 46, 73, false)) {
               return ItemStack.EMPTY;
            }
         } else if (slotType.getSlotType() == Type.ARMOR && !((Slot)this.inventorySlots.get(equipIndex)).getHasStack()) {
            if (!this.mergeItemStack(stack, equipIndex, equipIndex + 1, false)) {
               return ItemStack.EMPTY;
            }
         } else if (slotType == EntityEquipmentSlot.OFFHAND && !((Slot)this.inventorySlots.get(45)).getHasStack()) {
            if (!this.mergeItemStack(stack, 45, 46, false)) {
               return ItemStack.EMPTY;
            }
         } else if (index < 36) {
            if (!this.mergeItemStack(stack, 46, 73, false) && !this.mergeItemStack(stack, 36, 45, false)) {
               return ItemStack.EMPTY;
            }
         } else if (index < 73) {
            if (!this.mergeItemStack(stack, 9, 36, false)) {
               return ItemStack.EMPTY;
            }
         } else if (!this.mergeItemStack(stack, 46, 73, false) && !this.mergeItemStack(stack, 9, 45, false)) {
            return ItemStack.EMPTY;
         }

         if (stack.isEmpty()) {
            slot.putStack(ItemStack.EMPTY);
         } else {
            slot.onSlotChanged();
         }

         if (stack.getCount() == baseStack.getCount()) {
            return ItemStack.EMPTY;
         }

         ItemStack remainder = slot.onTake(playerIn, stack);
         if (index == 0) {
            playerIn.dropItem(remainder, false);
         }
      }

      return baseStack;
   }

   public boolean mergeItemStack(ItemStack stack, int start, int length, boolean r) {
      boolean successful = false;
      int i = !r ? start : length - 1;
      int iterOrder = !r ? 1 : -1;
      if (stack.isStackable()) {
         for(; stack.getCount() > 0 && (!r && i < length || r && i >= start); i += iterOrder) {
            Slot slot = (Slot)this.inventorySlots.get(i);
            ItemStack existingStack = slot.getStack();
            if (!existingStack.isEmpty()) {
               int maxStack = Math.min(stack.getMaxStackSize(), slot.getSlotStackLimit());
               int rmv = Math.min(maxStack, stack.getCount());
               if (slot.isItemValid(cloneStack(stack, rmv)) && existingStack.getItem().equals(stack.getItem()) && (!stack.getHasSubtypes() || stack.getItemDamage() == existingStack.getItemDamage()) && ItemStack.areItemStackTagsEqual(stack, existingStack)) {
                  int existingSize = existingStack.getCount() + stack.getCount();
                  if (existingSize <= maxStack) {
                     stack.setCount(0);
                     existingStack.setCount(existingSize);
                     slot.putStack(existingStack);
                     successful = true;
                  } else if (existingStack.getCount() < maxStack) {
                     stack.shrink(maxStack - existingStack.getCount());
                     existingStack.setCount(maxStack);
                     slot.putStack(existingStack);
                     successful = true;
                  }
               }
            }
         }
      }

      if (stack.getCount() > 0) {
         for(int var13 = !r ? start : length - 1; stack.getCount() > 0 && (!r && var13 < length || r && var13 >= start); var13 += iterOrder) {
            Slot slot = (Slot)this.inventorySlots.get(var13);
            ItemStack existingStack = slot.getStack();
            if (existingStack.isEmpty()) {
               int maxStack = Math.min(stack.getMaxStackSize(), slot.getSlotStackLimit());
               int rmv = Math.min(maxStack, stack.getCount());
               if (slot.isItemValid(cloneStack(stack, rmv))) {
                  existingStack = stack.splitStack(rmv);
                  slot.putStack(existingStack);
                  successful = true;
               }
            }
         }
      }

      return successful;
   }

   @Nonnull
   public ItemStack slotClick(int slotId, int dragType, ClickType clickTypeIn, EntityPlayer player) {
      SlotCachingItemHandler.cache(this);
      ItemStack stack = super.slotClick(slotId, dragType, clickTypeIn, player);
      SlotCachingItemHandler.applyCache(this);
      return stack;
   }

   private static ItemStack cloneStack(ItemStack stack, int size) {
      if (stack.isEmpty()) {
         return ItemStack.EMPTY;
      } else {
         ItemStack copy = stack.copy();
         copy.setCount(size);
         return copy;
      }
   }

   public static void saveCraftingInventory(EntityPlayer player) {
      InventoryCrafting crafting = ((ContainerPlayer)player.openContainer).craftMatrix;

      for(int i = 0; i < crafting.getSizeInventory(); ++i) {
         ItemStack stack = crafting.getStackInSlot(i);
         if (!stack.isEmpty() && !player.addItemStackToInventory(stack)) {
            player.dropItem(stack, false);
         }
      }

   }
}
