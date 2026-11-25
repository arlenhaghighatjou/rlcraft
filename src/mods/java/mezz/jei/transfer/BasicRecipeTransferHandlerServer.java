package mezz.jei.transfer;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

public final class BasicRecipeTransferHandlerServer {
   private BasicRecipeTransferHandlerServer() {
   }

   public static void setItems(EntityPlayer player, Map<Integer, Integer> slotIdMap, List<Integer> craftingSlots, List<Integer> inventorySlots, boolean maxTransfer, boolean requireCompleteSets) {
      Container container = player.openContainer;
      Map<Integer, ItemStack> slotMap = new HashMap(slotIdMap.size());

      for(Entry<Integer, Integer> entry : slotIdMap.entrySet()) {
         Slot slot = container.getSlot(((Integer)entry.getValue()).intValue());
         ItemStack slotStack = slot.getStack();
         if (slotStack.isEmpty()) {
            return;
         }

         ItemStack stack = slotStack.copy();
         stack.setCount(1);
         slotMap.put(entry.getKey(), stack);
      }

      boolean transferAsCompleteSets = requireCompleteSets || !maxTransfer;
      Map<Integer, ItemStack> toTransfer = removeItemsFromInventory(player, container, slotMap, craftingSlots, inventorySlots, transferAsCompleteSets, maxTransfer);
      if (!toTransfer.isEmpty()) {
         int minSlotStackLimit = Integer.MAX_VALUE;
         List<ItemStack> clearedCraftingItems = new ArrayList();

         for(int craftingSlotNumberIndex = 0; craftingSlotNumberIndex < craftingSlots.size(); ++craftingSlotNumberIndex) {
            int craftingSlotNumber = ((Integer)craftingSlots.get(craftingSlotNumberIndex)).intValue();
            Slot craftingSlot = container.getSlot(craftingSlotNumber);
            if (craftingSlot.getHasStack()) {
               ItemStack craftingItem = craftingSlot.decrStackSize(Integer.MAX_VALUE);
               clearedCraftingItems.add(craftingItem);
            }

            if (requireCompleteSets) {
               ItemStack transferItem = (ItemStack)toTransfer.get(Integer.valueOf(craftingSlotNumberIndex));
               if (transferItem != null) {
                  int slotStackLimit = craftingSlot.getItemStackLimit(transferItem);
                  minSlotStackLimit = Math.min(slotStackLimit, minSlotStackLimit);
               }
            }
         }

         for(Entry<Integer, ItemStack> entry : toTransfer.entrySet()) {
            Integer craftNumber = (Integer)entry.getKey();
            Integer slotNumber = (Integer)craftingSlots.get(craftNumber.intValue());
            Slot slot = container.getSlot(slotNumber.intValue());
            ItemStack stack = (ItemStack)entry.getValue();
            if (slot.isItemValid(stack)) {
               if (stack.getCount() > minSlotStackLimit) {
                  ItemStack remainder = stack.splitStack(stack.getCount() - minSlotStackLimit);
                  clearedCraftingItems.add(remainder);
               }

               slot.putStack(stack);
            } else {
               clearedCraftingItems.add(stack);
            }
         }

         for(ItemStack oldCraftingItem : clearedCraftingItems) {
            int added = addStack(container, inventorySlots, oldCraftingItem);
            if (added < oldCraftingItem.getCount() && !player.inventory.addItemStackToInventory(oldCraftingItem)) {
               player.dropItem(oldCraftingItem, false);
            }
         }

         container.detectAndSendChanges();
      }
   }

   @Nonnull
   private static Map<Integer, ItemStack> removeItemsFromInventory(EntityPlayer player, Container container, Map<Integer, ItemStack> required, List<Integer> craftingSlots, List<Integer> inventorySlots, boolean transferAsCompleteSets, boolean maxTransfer) {
      Map<Integer, ItemStack> result = new HashMap(required.size());

      while(true) {
         Map<Slot, ItemStack> originalSlotContents = null;
         if (transferAsCompleteSets) {
            originalSlotContents = new HashMap();
         }

         Map<Integer, ItemStack> foundItemsInSet = new HashMap(required.size());
         boolean noItemsFound = true;

         for(Entry<Integer, ItemStack> entry : required.entrySet()) {
            ItemStack requiredStack = ((ItemStack)entry.getValue()).copy();
            Slot slot = getSlotWithStack(container, requiredStack, craftingSlots, inventorySlots);
            boolean itemFound = slot != null && !slot.getStack().isEmpty() && slot.canTakeStack(player);
            ItemStack resultItemStack = (ItemStack)result.get(entry.getKey());
            boolean resultItemStackLimitReached = resultItemStack != null && resultItemStack.getCount() == resultItemStack.getMaxStackSize();
            if (itemFound && !resultItemStackLimitReached) {
               if (originalSlotContents != null && !originalSlotContents.containsKey(slot)) {
                  originalSlotContents.put(slot, slot.getStack().copy());
               }

               ItemStack removedItemStack = slot.decrStackSize(1);
               foundItemsInSet.put(entry.getKey(), removedItemStack);
               noItemsFound = false;
            } else if (transferAsCompleteSets) {
               for(Entry<Slot, ItemStack> slotEntry : originalSlotContents.entrySet()) {
                  ItemStack stack = (ItemStack)slotEntry.getValue();
                  ((Slot)slotEntry.getKey()).putStack(stack);
               }

               return result;
            }
         }

         for(Entry<Integer, ItemStack> entry : foundItemsInSet.entrySet()) {
            ItemStack resultItemStack = (ItemStack)result.get(entry.getKey());
            if (resultItemStack == null) {
               result.put(entry.getKey(), entry.getValue());
            } else {
               resultItemStack.grow(1);
            }
         }

         if (!maxTransfer || noItemsFound) {
            break;
         }
      }

      return result;
   }

   @Nullable
   private static Slot getSlotWithStack(Container container, ItemStack stack, List<Integer> craftingSlots, List<Integer> inventorySlots) {
      Slot slot = getSlotWithStack(container, craftingSlots, stack);
      if (slot == null) {
         slot = getSlotWithStack(container, inventorySlots, stack);
      }

      return slot;
   }

   private static int addStack(Container container, Collection<Integer> slotIndexes, ItemStack stack) {
      int added = 0;

      for(Integer slotIndex : slotIndexes) {
         if (slotIndex.intValue() >= 0 && slotIndex.intValue() < container.inventorySlots.size()) {
            Slot slot = container.getSlot(slotIndex.intValue());
            ItemStack inventoryStack = slot.getStack();
            if (!inventoryStack.isEmpty() && inventoryStack.isStackable() && inventoryStack.isItemEqual(stack) && ItemStack.areItemStackTagsEqual(inventoryStack, stack)) {
               int remain = stack.getCount() - added;
               int maxStackSize = Math.min(slot.getItemStackLimit(inventoryStack), inventoryStack.getMaxStackSize());
               int space = maxStackSize - inventoryStack.getCount();
               if (space > 0) {
                  if (space >= remain) {
                     inventoryStack.grow(remain);
                     return stack.getCount();
                  }

                  inventoryStack.setCount(inventoryStack.getMaxStackSize());
                  added += space;
               }
            }
         }
      }

      if (added >= stack.getCount()) {
         return added;
      } else {
         for(Integer slotIndex : slotIndexes) {
            if (slotIndex.intValue() >= 0 && slotIndex.intValue() < container.inventorySlots.size()) {
               Slot slot = container.getSlot(slotIndex.intValue());
               ItemStack inventoryStack = slot.getStack();
               if (inventoryStack.isEmpty()) {
                  ItemStack stackToAdd = stack.copy();
                  stackToAdd.setCount(stack.getCount() - added);
                  slot.putStack(stackToAdd);
                  return stack.getCount();
               }
            }
         }

         return added;
      }
   }

   @Nullable
   private static Slot getSlotWithStack(Container container, Iterable<Integer> slotNumbers, ItemStack itemStack) {
      for(Integer slotNumber : slotNumbers) {
         if (slotNumber.intValue() >= 0 && slotNumber.intValue() < container.inventorySlots.size()) {
            Slot slot = container.getSlot(slotNumber.intValue());
            ItemStack slotStack = slot.getStack();
            if (ItemStack.areItemsEqual(itemStack, slotStack) && ItemStack.areItemStackTagsEqual(itemStack, slotStack)) {
               return slot;
            }
         }
      }

      return null;
   }
}
