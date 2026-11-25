package net.silentchaos512.lib.util;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableList.Builder;
import java.util.Collection;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;

public final class InventoryUtils {
   private InventoryUtils() {
      throw new IllegalAccessError("Utility class");
   }

   public static boolean canItemsStack(ItemStack a, ItemStack b) {
      if (!a.isEmpty() && a.isItemEqual(b) && a.hasTagCompound() == b.hasTagCompound()) {
         return (!a.hasTagCompound() || a.getTagCompound().equals(b.getTagCompound())) && a.areCapsCompatible(b);
      } else {
         return false;
      }
   }

   public static ItemStack mergeItem(IInventory inventory, int slotStart, int slotEndExclusive, ItemStack stack) {
      if (inventory != null && !stack.isEmpty()) {
         for(int i = slotStart; i < slotEndExclusive && !stack.isEmpty(); ++i) {
            ItemStack inSlot = inventory.getStackInSlot(i);
            if (canItemsStack(inSlot, stack)) {
               int amountCanFit = MathUtils.min(inSlot.getMaxStackSize() - inSlot.getCount(), stack.getCount(), inventory.getInventoryStackLimit());
               inSlot.grow(amountCanFit);
               stack.shrink(amountCanFit);
               inventory.setInventorySlotContents(i, inSlot);
            }
         }

         for(int i = slotStart; i < slotEndExclusive && !stack.isEmpty(); ++i) {
            if (inventory.getStackInSlot(i).isEmpty()) {
               int amountCanFit = MathUtils.min(stack.getCount(), inventory.getInventoryStackLimit());
               ItemStack toInsert = stack.copy();
               toInsert.setCount(amountCanFit);
               stack.shrink(amountCanFit);
               inventory.setInventorySlotContents(i, toInsert);
            }
         }

         return stack;
      } else {
         return stack;
      }
   }

   public static Collection<ItemStack> mergeItems(IInventory inventory, int slotStart, int slotEndExclusive, Collection<ItemStack> stacks) {
      if (inventory == null && stacks.isEmpty()) {
         return ImmutableList.of();
      } else {
         Builder<ItemStack> leftovers = ImmutableList.builder();

         for(ItemStack stack : stacks) {
            stack = mergeItem(inventory, slotStart, slotEndExclusive, stack);
            if (!stack.isEmpty()) {
               leftovers.add(stack);
            }
         }

         return leftovers.build();
      }
   }
}
