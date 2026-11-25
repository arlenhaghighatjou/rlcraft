package invtweaks;

import invtweaks.api.IItemTreeItem;
import invtweaks.api.SortingMethod;
import invtweaks.api.container.ContainerSection;
import invtweaks.container.ContainerSectionManager;
import invtweaks.container.IContainerManager;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.Consumer;
import java.util.function.Predicate;
import net.minecraft.client.Minecraft;
import net.minecraft.inventory.Slot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class InvTweaksHandlerSorting extends InvTweaksObfuscation {
   private static final Logger log = InvTweaks.log;
   private static final int MAX_CONTAINER_SIZE = 999;
   @Nullable
   private static int[] DEFAULT_LOCK_PRIORITIES = null;
   @Nullable
   private static boolean[] DEFAULT_FROZEN_SLOTS = null;
   private ContainerSectionManager containerMgr;
   private SortingMethod algorithm;
   private int size;
   private boolean sortArmorParts;
   private InvTweaksItemTree tree;
   private List<InvTweaksConfigSortingRule> rules;
   private int[] rulePriority;
   private int[] keywordOrder;
   @Nullable
   private int[] lockPriorities;
   @Nullable
   private boolean[] frozenSlots;

   public InvTweaksHandlerSorting(Minecraft mc_, @NotNull InvTweaksConfig config, ContainerSection section, SortingMethod algorithm_, int rowSize) throws Exception {
      super(mc_);
      if (DEFAULT_LOCK_PRIORITIES == null) {
         DEFAULT_LOCK_PRIORITIES = new int[999];

         for(int i = 0; i < 999; ++i) {
            DEFAULT_LOCK_PRIORITIES[i] = 0;
         }
      }

      if (DEFAULT_FROZEN_SLOTS == null) {
         DEFAULT_FROZEN_SLOTS = new boolean[999];

         for(int i = 0; i < 999; ++i) {
            DEFAULT_FROZEN_SLOTS[i] = false;
         }
      }

      this.containerMgr = new ContainerSectionManager(section);
      this.size = this.containerMgr.getSize();
      this.sortArmorParts = config.getProperty("enableAutoEquipArmor").equals("true") && !isGuiInventoryCreative(this.getCurrentScreen());
      this.rules = config.getRules();
      this.tree = config.getTree();
      if (section == ContainerSection.INVENTORY) {
         this.lockPriorities = config.getLockPriorities();
         this.frozenSlots = config.getFrozenSlots();
         this.algorithm = SortingMethod.INVENTORY;
      } else {
         this.lockPriorities = DEFAULT_LOCK_PRIORITIES;
         this.frozenSlots = DEFAULT_FROZEN_SLOTS;
         this.algorithm = algorithm_;
         if (this.algorithm != SortingMethod.DEFAULT) {
            this.computeLineSortingRules(rowSize, this.algorithm == SortingMethod.HORIZONTAL);
         }
      }

      this.rulePriority = new int[this.size];
      this.keywordOrder = new int[this.size];

      for(int i = 0; i < this.size; ++i) {
         this.rulePriority[i] = -1;
         ItemStack stack = this.containerMgr.getItemStack(i);
         if (!stack.isEmpty()) {
            this.keywordOrder[i] = this.getItemOrder(stack);
         } else {
            this.keywordOrder[i] = -1;
         }
      }

      this.rules.stream().filter((rule) -> {
         return rule.getContainerSize() == this.size && rule.getPreferredSlots() != null;
      }).forEach((rule) -> {
         int priority = rule.getPriority();

         for(int slot : rule.getPreferredSlots()) {
            ItemStack stack = this.containerMgr.getItemStack(slot);
            if (!stack.isEmpty()) {
               List<IItemTreeItem> items = this.tree.getItems(stack.getItem().getRegistryName().toString(), stack.getItemDamage(), stack.getTagCompound());
               if (this.rulePriority[slot] < priority && this.tree.matches(items, rule.getKeyword())) {
                  this.rulePriority[slot] = priority;
               }
            }
         }

      });
   }

   private static boolean canMergeStacks(@NotNull ItemStack from, @NotNull ItemStack to) {
      if (areItemsStackable(from, to)) {
         if (from.getCount() > from.getMaxStackSize()) {
            return false;
         }

         if (to.getCount() < to.getMaxStackSize()) {
            return true;
         }
      }

      return false;
   }

   public void sort() {
      long timer = System.nanoTime();
      IContainerManager globalContainer = InvTweaks.getCurrentContainerManager();
      if (!this.getHeldStack().isEmpty()) {
         int emptySlot = globalContainer.getFirstEmptyIndex(ContainerSection.INVENTORY);
         if (emptySlot == -1) {
            return;
         }

         globalContainer.putHoldItemDown(ContainerSection.INVENTORY, emptySlot);
      }

      if (this.algorithm != SortingMethod.DEFAULT) {
         if (this.algorithm == SortingMethod.EVEN_STACKS) {
            this.sortEvenStacks();
         } else if (this.algorithm == SortingMethod.INVENTORY) {
            this.sortInventory(globalContainer);
         }

         this.sortWithRules();
      }

      this.defaultSorting();
      if (log.isEnabled(InvTweaksConst.DEBUG)) {
         timer = System.nanoTime() - timer;
         log.info("Sorting done in " + timer + "ns");
      }

      if (!this.getHeldStack().isEmpty()) {
         int emptySlot = globalContainer.getFirstEmptyIndex(ContainerSection.INVENTORY);
         if (emptySlot != -1) {
            globalContainer.putHoldItemDown(ContainerSection.INVENTORY, emptySlot);
         }
      }

      globalContainer.applyChanges();
   }

   private void sortWithRules() {
      log.info("Applying rules.");

      for(InvTweaksConfigSortingRule rule : this.rules) {
         int priority = rule.getPriority();
         if (log.isEnabled(InvTweaksConst.DEBUG)) {
            log.info("Rule : " + rule.getKeyword() + "(" + priority + ")");
         }

         for(int i = 0; i < this.size; ++i) {
            ItemStack from = this.containerMgr.getItemStack(i);
            if (this.hasToBeMoved(i, priority) && this.lockPriorities[i] < priority) {
               List<IItemTreeItem> fromItems = this.tree.getItems(from.getItem().getRegistryName().toString(), from.getItemDamage(), from.getTagCompound());
               if (this.tree.matches(fromItems, rule.getKeyword())) {
                  int[] preferredSlots = rule.getPreferredSlots();
                  int stackToMove = i;

                  for(int k : preferredSlots) {
                     int moveResult = this.move(stackToMove, k, priority);
                     if (moveResult != -1) {
                        if (moveResult == k) {
                           break;
                        }

                        from = this.containerMgr.getItemStack(moveResult);
                        fromItems = this.tree.getItems(from.getItem().getRegistryName().toString(), from.getItemDamage(), from.getTagCompound());
                        if (!this.tree.matches(fromItems, rule.getKeyword()) || i < moveResult) {
                           break;
                        }

                        stackToMove = moveResult;
                     }
                  }
               }
            }
         }
      }

      log.info("Locking stacks.");

      for(int i = 0; i < this.size; ++i) {
         if (this.hasToBeMoved(i, 1) && this.lockPriorities[i] > 0) {
            this.markAsMoved(i, 1);
         }
      }

   }

   private void sortInventory(@NotNull IContainerManager globalContainer) {
      log.info("Handling crafting slots.");
      if (globalContainer.hasSection(ContainerSection.CRAFTING_IN)) {
         List<Slot> craftingSlots = globalContainer.getSlots(ContainerSection.CRAFTING_IN);
         int emptyIndex = globalContainer.getFirstEmptyIndex(ContainerSection.INVENTORY);
         if (emptyIndex != -1) {
            for(Slot craftingSlot : craftingSlots) {
               if (craftingSlot.getHasStack()) {
                  globalContainer.move(ContainerSection.CRAFTING_IN, globalContainer.getSlotIndex(getSlotNumber(craftingSlot)), ContainerSection.INVENTORY, emptyIndex);
                  emptyIndex = globalContainer.getFirstEmptyIndex(ContainerSection.INVENTORY);
                  if (emptyIndex == -1) {
                     break;
                  }
               }
            }
         }
      }

      this.sortMergeArmor(globalContainer);
   }

   private void sortMergeArmor(@NotNull IContainerManager globalContainer) {
      log.info("Merging stacks.");

      for(int i = this.size - 1; i >= 0; --i) {
         ItemStack from = this.containerMgr.getItemStack(i);
         if (!from.isEmpty()) {
            Item fromItem = from.getItem();
            if (fromItem.isDamageable()) {
               this.moveArmor(globalContainer, i, from, fromItem);
            } else {
               this.mergeItem(i, from);
            }
         }
      }

   }

   private void mergeItem(int i, @NotNull ItemStack from) {
      int j = 0;
      int[] var4 = this.lockPriorities;
      int var5 = var4.length;

      for(int var6 = 0; var6 < var5; ++var6) {
         Integer lockPriority = var4[var6];
         if (lockPriority.intValue() > 0) {
            ItemStack to = this.containerMgr.getItemStack(j);
            if (!to.isEmpty() && areItemsStackable(from, to)) {
               this.move(i, j, Integer.MAX_VALUE);
               this.markAsNotMoved(j);
               if (this.containerMgr.getItemStack(i).isEmpty()) {
                  break;
               }
            }
         }

         ++j;
      }

   }

   private void moveArmor(@NotNull IContainerManager globalContainer, int i, @NotNull ItemStack from, Item fromItem) {
      if (this.sortArmorParts && isItemArmor(fromItem)) {
         ItemArmor fromItemArmor = (ItemArmor)fromItem;
         if (globalContainer.hasSection(ContainerSection.ARMOR)) {
            for(Slot slot : globalContainer.getSlots(ContainerSection.ARMOR)) {
               boolean move = false;
               if (!slot.getHasStack()) {
                  move = true;
               } else {
                  Item currentArmor = slot.getStack().getItem();
                  if (isItemArmor(currentArmor)) {
                     int armorLevel = ((ItemArmor)currentArmor).damageReduceAmount;
                     if (armorLevel < fromItemArmor.damageReduceAmount || armorLevel == fromItemArmor.damageReduceAmount && slot.getStack().getItemDamage() < from.getItemDamage()) {
                        move = true;
                     }
                  } else {
                     move = true;
                  }
               }

               if (slot.isItemValid(from) && move) {
                  globalContainer.move(ContainerSection.INVENTORY, i, ContainerSection.ARMOR, globalContainer.getSlotIndex(getSlotNumber(slot)));
               }
            }
         }
      }

   }

   private void sortEvenStacks() {
      log.info("Distributing items.");
      HashMap<Pair<String, Integer>, int[]> itemCounts = new HashMap();

      for(int i = 0; i < this.size; ++i) {
         ItemStack stack = this.containerMgr.getItemStack(i);
         if (!stack.isEmpty()) {
            Pair<String, Integer> item = Pair.of(stack.getItem().getRegistryName().toString(), Integer.valueOf(stack.getItemDamage()));
            int[] count = (int[])itemCounts.get(item);
            if (count == null) {
               int[] newCount = new int[]{stack.getCount(), 1};
               itemCounts.put(item, newCount);
            } else {
               count[0] += stack.getCount();
               ++count[1];
            }
         }
      }

      for(Entry<Pair<String, Integer>, int[]> entry : itemCounts.entrySet()) {
         Pair<String, Integer> item = (Pair)entry.getKey();
         int[] count = (int[])entry.getValue();
         int numPerSlot = count[0] / count[1];
         if (numPerSlot <= (new ItemStack((Item)Item.REGISTRY.getObject(new ResourceLocation((String)item.getLeft())), 1, 0)).getMaxStackSize()) {
            LinkedList<Integer> smallStacks = new LinkedList();
            LinkedList<Integer> largeStacks = new LinkedList();

            for(int i = 0; i < this.size; ++i) {
               ItemStack stack = this.containerMgr.getItemStack(i);
               if (!stack.isEmpty() && Pair.of(stack.getItem().getRegistryName().toString(), Integer.valueOf(stack.getItemDamage())).equals(item)) {
                  int stackSize = stack.getCount();
                  if (stackSize > numPerSlot) {
                     largeStacks.offer(Integer.valueOf(i));
                  } else if (stackSize < numPerSlot) {
                     smallStacks.offer(Integer.valueOf(i));
                  }
               }
            }

            while(!smallStacks.isEmpty()) {
               int largeIndex = ((Integer)largeStacks.peek()).intValue();
               int largeSize = this.containerMgr.getItemStack(largeIndex).getCount();
               int smallIndex = ((Integer)smallStacks.peek()).intValue();
               int smallSize = this.containerMgr.getItemStack(smallIndex).getCount();
               this.containerMgr.moveSome(largeIndex, smallIndex, Math.min(numPerSlot - smallSize, largeSize - numPerSlot));
               largeSize = this.containerMgr.getItemStack(largeIndex).getCount();
               smallSize = this.containerMgr.getItemStack(smallIndex).getCount();
               if (largeSize == numPerSlot) {
                  largeStacks.remove();
               }

               if (smallSize == numPerSlot) {
                  smallStacks.remove();
               }
            }

            while(largeStacks.size() > 1) {
               int largeIndex = ((Integer)largeStacks.poll()).intValue();
               int largeSize = this.containerMgr.getItemStack(largeIndex).getCount();
               this.containerMgr.moveSome(largeIndex, ((Integer)largeStacks.peek()).intValue(), largeSize - numPerSlot);
            }
         }
      }

      for(int i = 0; i < this.size; ++i) {
         this.markAsMoved(i, 1);
      }

   }

   private void defaultSorting() {
      log.info("Default sorting.");
      ArrayList<Integer> remaining = new ArrayList();
      ArrayList<Integer> nextRemaining = new ArrayList();

      for(int i = 0; i < this.size; ++i) {
         if (this.hasToBeMoved(i, 1)) {
            remaining.add(Integer.valueOf(i));
            nextRemaining.add(Integer.valueOf(i));
         }
      }

      int iterations = 0;

      while(remaining.size() > 0 && iterations++ < 50) {
         Iterator var4 = remaining.iterator();

         while(var4.hasNext()) {
            int i = ((Integer)var4.next()).intValue();
            if (this.hasToBeMoved(i, 1)) {
               for(int j = 0; j < this.size; ++j) {
                  if (this.move(i, j, 1) != -1) {
                     nextRemaining.remove(Integer.valueOf(j));
                     break;
                  }
               }
            } else {
               nextRemaining.remove(Integer.valueOf(i));
            }
         }

         remaining.clear();
         remaining.addAll(nextRemaining);
      }

      if (iterations == 100) {
         log.warn("Sorting takes too long, aborting.");
      }

   }

   private boolean canSwapSlots(int i, int j, int priority) {
      return this.lockPriorities[j] <= priority && (this.rulePriority[j] < priority || this.rulePriority[j] == priority && this.isOrderedBefore(i, j));
   }

   private int move(int i, int j, int priority) {
      ItemStack from = this.containerMgr.getItemStack(i);
      ItemStack to = this.containerMgr.getItemStack(j);
      if (!from.isEmpty() && !this.frozenSlots[j] && !this.frozenSlots[i]) {
         if (this.lockPriorities[i] <= priority) {
            if (i == j) {
               this.markAsMoved(i, priority);
               return j;
            }

            if (to.isEmpty() && this.lockPriorities[j] <= priority && !this.frozenSlots[j]) {
               this.rulePriority[i] = -1;
               this.keywordOrder[i] = -1;
               this.rulePriority[j] = priority;
               this.keywordOrder[j] = this.getItemOrder(from);
               if (this.containerMgr.move(i, j)) {
                  return j;
               }

               return -1;
            }

            if (!to.isEmpty() && (this.canSwapSlots(i, j, priority) || canMergeStacks(from, to))) {
               this.keywordOrder[j] = this.keywordOrder[i];
               this.rulePriority[j] = priority;
               this.rulePriority[i] = -1;
               boolean success = this.containerMgr.move(i, j);
               if (success) {
                  ItemStack remains = this.containerMgr.getItemStack(i);
                  if (remains.isEmpty()) {
                     return j;
                  }

                  int dropSlot = i;
                  if (this.lockPriorities[j] > this.lockPriorities[i]) {
                     for(int k = 0; k < this.size; ++k) {
                        if (this.containerMgr.getItemStack(k).isEmpty() && this.lockPriorities[k] == 0) {
                           dropSlot = k;
                           break;
                        }
                     }
                  }

                  if (dropSlot != i && !this.containerMgr.move(i, dropSlot)) {
                     return -1;
                  }

                  this.rulePriority[dropSlot] = -1;
                  this.keywordOrder[dropSlot] = this.getItemOrder(remains);
                  return dropSlot;
               }

               return -1;
            }
         }

         return -1;
      } else {
         return -1;
      }
   }

   private void markAsMoved(int i, int priority) {
      this.rulePriority[i] = priority;
   }

   private void markAsNotMoved(int i) {
      this.rulePriority[i] = -1;
   }

   private boolean hasToBeMoved(int slot, int priority) {
      return !this.containerMgr.getItemStack(slot).isEmpty() && this.rulePriority[slot] <= priority;
   }

   private boolean isOrderedBefore(int i, int j) {
      ItemStack iStack = this.containerMgr.getItemStack(i);
      ItemStack jStack = this.containerMgr.getItemStack(j);
      return InvTweaks.getInstance().compareItems(iStack, jStack, this.keywordOrder[i], this.keywordOrder[j]) < 0;
   }

   private int getItemOrder(@NotNull ItemStack itemStack) {
      List<IItemTreeItem> items = this.tree.getItems(itemStack.getItem().getRegistryName().toString(), itemStack.getItemDamage(), itemStack.getTagCompound());
      return items.size() > 0 ? ((IItemTreeItem)items.get(0)).getOrder() : Integer.MAX_VALUE;
   }

   private void computeLineSortingRules(int rowSize, boolean horizontal) {
      if (rowSize <= 9) {
         this.rules = new ArrayList();
         Map<IItemTreeItem, Integer> stats = this.computeContainerStats();
         List<IItemTreeItem> itemOrder = new ArrayList();
         int distinctItems = stats.size();
         int columnSize = this.getContainerColumnSize(rowSize);
         int availableSlots = this.size;
         int remainingStacks = 0;

         for(Integer stacks : stats.values()) {
            remainingStacks += stacks.intValue();
         }

         if (distinctItems != 0) {
            List<IItemTreeItem> unorderedItems = new ArrayList(stats.keySet());
            boolean hasStacksToOrderFirst = true;

            label124:
            while(hasStacksToOrderFirst) {
               hasStacksToOrderFirst = false;
               Iterator row = unorderedItems.iterator();

               IItemTreeItem item;
               while(true) {
                  if (!row.hasNext()) {
                     continue label124;
                  }

                  item = (IItemTreeItem)row.next();
                  Integer value = (Integer)stats.get(item);
                  if (value.intValue() > (horizontal ? rowSize : columnSize) && !itemOrder.contains(item)) {
                     break;
                  }
               }

               hasStacksToOrderFirst = true;
               itemOrder.add(item);
               unorderedItems.remove(item);
            }

            unorderedItems.sort(Collections.reverseOrder());
            itemOrder.addAll(unorderedItems);
            int spaceWidth;
            int spaceHeight;
            if (horizontal) {
               spaceHeight = 1;
               spaceWidth = rowSize / ((distinctItems + columnSize - 1) / columnSize);
            } else {
               spaceWidth = 1;
               spaceHeight = columnSize / ((distinctItems + rowSize - 1) / rowSize);
            }

            char row = 'a';
            char maxRow = (char)(row - 1 + columnSize);
            char column = '1';
            char maxColumn = (char)(column - 1 + rowSize);

            for(IItemTreeItem item : itemOrder) {
               int thisSpaceWidth = spaceWidth;
               int thisSpaceHeight = spaceHeight;

               while(((Integer)stats.get(item)).intValue() > thisSpaceHeight * thisSpaceWidth) {
                  if (horizontal) {
                     if (column + thisSpaceWidth < maxColumn) {
                        thisSpaceWidth = maxColumn - column + 1;
                     } else {
                        if (row + thisSpaceHeight >= maxRow) {
                           break;
                        }

                        ++thisSpaceHeight;
                     }
                  } else if (row + thisSpaceHeight < maxRow) {
                     thisSpaceHeight = maxRow - row + 1;
                  } else {
                     if (column + thisSpaceWidth >= maxColumn) {
                        break;
                     }

                     ++thisSpaceWidth;
                  }
               }

               if (horizontal && column + thisSpaceWidth == maxColumn) {
                  ++thisSpaceWidth;
               } else if (!horizontal && row + thisSpaceHeight == maxRow) {
                  ++thisSpaceHeight;
               }

               String constraint = String.format("%c%c-%c%c", row, column, (char)(row - 1 + thisSpaceHeight), (char)(column - 1 + thisSpaceWidth));
               if (!horizontal) {
                  constraint = constraint + 'v';
               }

               this.rules.add(new InvTweaksConfigSortingRule(this.tree, constraint, item.getName(), this.size, rowSize));
               availableSlots -= thisSpaceHeight * thisSpaceWidth;
               remainingStacks -= ((Integer)stats.get(item)).intValue();
               if (availableSlots < remainingStacks) {
                  break;
               }

               if (horizontal) {
                  if (column + thisSpaceWidth + spaceWidth <= maxColumn + 1) {
                     column = (char)(column + thisSpaceWidth);
                  } else {
                     column = '1';
                     row = (char)(row + thisSpaceHeight);
                  }
               } else if (row + thisSpaceHeight + spaceHeight <= maxRow + 1) {
                  row = (char)(row + thisSpaceHeight);
               } else {
                  row = 'a';
                  column = (char)(column + thisSpaceWidth);
               }

               if (row > maxRow || column > maxColumn) {
                  break;
               }
            }

            String defaultRule;
            if (horizontal) {
               defaultRule = maxRow + "1-a" + maxColumn;
            } else {
               defaultRule = "a" + maxColumn + "-" + maxRow + "1v";
            }

            this.rules.add(new InvTweaksConfigSortingRule(this.tree, defaultRule, this.tree.getRootCategory().getName(), this.size, rowSize));
         }
      }
   }

   @NotNull
   private Map<IItemTreeItem, Integer> computeContainerStats() {
      Map<IItemTreeItem, Integer> stats = new HashMap();
      Map<Integer, IItemTreeItem> itemSearch = new HashMap();

      for(int i = 0; i < this.size; ++i) {
         ItemStack stack = this.containerMgr.getItemStack(i);
         if (!stack.isEmpty()) {
            int itemSearchKey = Item.getIdFromItem(stack.getItem()) * 100000 + (stack.getMaxStackSize() != 1 ? stack.getItemDamage() : 0);
            IItemTreeItem item = (IItemTreeItem)itemSearch.get(Integer.valueOf(itemSearchKey));
            if (item == null) {
               item = (IItemTreeItem)this.tree.getItems(stack.getItem().getRegistryName().toString(), stack.getItemDamage(), stack.getTagCompound()).get(0);
               itemSearch.put(Integer.valueOf(itemSearchKey), item);
               stats.put(item, Integer.valueOf(1));
            } else {
               stats.put(item, Integer.valueOf(((Integer)stats.get(item)).intValue() + 1));
            }
         }
      }

      return stats;
   }

   private int getContainerColumnSize(int rowSize) {
      return this.size / rowSize;
   }
}
