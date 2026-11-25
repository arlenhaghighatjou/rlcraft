package vazkii.quark.base.handler;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import java.util.function.ToIntFunction;
import net.minecraft.block.Block;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemArrow;
import net.minecraft.item.ItemAxe;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemBow;
import net.minecraft.item.ItemDye;
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemMinecart;
import net.minecraft.item.ItemPickaxe;
import net.minecraft.item.ItemPotion;
import net.minecraft.item.ItemSpade;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.item.ItemTool;
import net.minecraft.item.Item.ToolMaterial;
import net.minecraft.util.EnumActionResult;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.SlotItemHandler;
import net.minecraftforge.items.wrapper.InvWrapper;
import vazkii.quark.api.ICustomSorting;
import vazkii.quark.base.module.ModuleLoader;
import vazkii.quark.management.feature.InventorySorting;
import vazkii.quark.oddities.inventory.ContainerBackpack;

public final class SortingHandler {
   private static final Comparator<ItemStack> FALLBACK_COMPARATOR = jointComparator(Comparator.comparingInt((s) -> {
      return Item.getIdFromItem(s.getItem());
   }), SortingHandler::damageCompare, (s1, s2) -> {
      return s2.getCount() - s1.getCount();
   }, (s1, s2) -> {
      return s2.getDisplayName().compareTo(s1.getDisplayName());
   }, (s1, s2) -> {
      return s2.hashCode() - s1.hashCode();
   });
   private static final Comparator<ItemStack> FOOD_COMPARATOR = jointComparator(SortingHandler::foodHealCompare, SortingHandler::foodSaturationCompare);
   private static final Comparator<ItemStack> TOOL_COMPARATOR = jointComparator(SortingHandler::toolPowerCompare, SortingHandler::enchantmentCompare, SortingHandler::damageCompare);
   private static final Comparator<ItemStack> SWORD_COMPARATOR = jointComparator(SortingHandler::swordPowerCompare, SortingHandler::enchantmentCompare, SortingHandler::damageCompare);
   private static final Comparator<ItemStack> ARMOR_COMPARATOR = jointComparator(SortingHandler::armorSlotAndToughnessCompare, SortingHandler::enchantmentCompare, SortingHandler::damageCompare);
   private static final Comparator<ItemStack> BOW_COMPARATOR = jointComparator(SortingHandler::enchantmentCompare, SortingHandler::damageCompare);

   public SortingHandler() {
   }

   public static void sortInventory(EntityPlayer player, boolean forcePlayer) {
      if (ModuleLoader.isFeatureEnabled(InventorySorting.class)) {
         Container c = player.openContainer;
         if (forcePlayer || c == null) {
            c = player.inventoryContainer;
         }

         boolean backpack = c instanceof ContainerBackpack;
         boolean playerContainer = c == player.inventoryContainer || backpack;

         for(Slot s : c.inventorySlots) {
            IInventory inv = s.inventory;
            if (inv == player.inventory == playerContainer) {
               if (!playerContainer && s instanceof SlotItemHandler) {
                  sortInventory(((SlotItemHandler)s).getItemHandler());
                  break;
               }

               InvWrapper wrapper = new InvWrapper(inv);
               if (playerContainer) {
                  sortInventory(wrapper, 9, 36);
               } else {
                  sortInventory(wrapper);
               }
               break;
            }
         }

         if (backpack) {
            for(Slot s : c.inventorySlots) {
               if (s instanceof SlotItemHandler) {
                  sortInventory(((SlotItemHandler)s).getItemHandler());
                  break;
               }
            }
         }

      }
   }

   public static void sortInventory(IItemHandler handler) {
      sortInventory(handler, 0);
   }

   public static void sortInventory(IItemHandler handler, int iStart) {
      sortInventory(handler, iStart, handler.getSlots());
   }

   public static void sortInventory(IItemHandler handler, int iStart, int iEnd) {
      List<ItemStack> stacks = new ArrayList();
      List<ItemStack> restore = new ArrayList();

      for(int i = iStart; i < iEnd; ++i) {
         ItemStack stackAt = handler.getStackInSlot(i);
         restore.add(stackAt.copy());
         if (!stackAt.isEmpty()) {
            stacks.add(stackAt.copy());
         }
      }

      mergeStacks(stacks);
      sortStackList(stacks);
      if (setInventory(handler, stacks, iStart, iEnd) == EnumActionResult.FAIL) {
         setInventory(handler, restore, iStart, iEnd);
      }

   }

   private static EnumActionResult setInventory(IItemHandler inventory, List<ItemStack> stacks, int iStart, int iEnd) {
      for(int i = iStart; i < iEnd; ++i) {
         int j = i - iStart;
         ItemStack stack = j >= stacks.size() ? ItemStack.EMPTY : (ItemStack)stacks.get(j);
         if (!stack.isEmpty() && !inventory.isItemValid(i, stack)) {
            return EnumActionResult.PASS;
         }
      }

      for(int i = iStart; i < iEnd; ++i) {
         int j = i - iStart;
         ItemStack stack = j >= stacks.size() ? ItemStack.EMPTY : (ItemStack)stacks.get(j);
         inventory.extractItem(i, inventory.getSlotLimit(i), false);
         if (!stack.isEmpty() && !inventory.insertItem(i, stack, false).isEmpty()) {
            return EnumActionResult.FAIL;
         }
      }

      return EnumActionResult.SUCCESS;
   }

   private static void mergeStacks(List<ItemStack> list) {
      for(int i = 0; i < list.size(); ++i) {
         ItemStack set = mergeStackWithOthers(list, i);
         list.set(i, set);
      }

      list.removeIf((stack) -> {
         return (void)(stack.isEmpty() || stack.getCount() == 0);
      });
   }

   private static ItemStack mergeStackWithOthers(List<ItemStack> list, int index) {
      ItemStack stack = (ItemStack)list.get(index);
      if (stack.isEmpty()) {
         return stack;
      } else {
         for(int i = 0; i < list.size(); ++i) {
            if (i != index) {
               ItemStack stackAt = (ItemStack)list.get(i);
               if (!stackAt.isEmpty() && stackAt.getCount() < stackAt.getMaxStackSize() && ItemStack.areItemsEqual(stack, stackAt) && ItemStack.areItemStackTagsEqual(stack, stackAt)) {
                  int setSize = stackAt.getCount() + stack.getCount();
                  int carryover = Math.max(0, setSize - stackAt.getMaxStackSize());
                  stackAt.setCount(carryover);
                  stack.setCount(setSize - carryover);
                  if (stack.getCount() == stack.getMaxStackSize()) {
                     return stack;
                  }
               }
            }
         }

         return stack;
      }
   }

   public static void sortStackList(List<ItemStack> list) {
      list.sort(SortingHandler::stackCompare);
   }

   private static int stackCompare(ItemStack stack1, ItemStack stack2) {
      if (stack1 == stack2) {
         return 0;
      } else if (stack1.isEmpty()) {
         return -1;
      } else if (stack2.isEmpty()) {
         return 1;
      } else {
         if (ICustomSorting.hasSorting(stack1) && ICustomSorting.hasSorting(stack2)) {
            ICustomSorting sort1 = ICustomSorting.getSorting(stack1);
            ICustomSorting sort2 = ICustomSorting.getSorting(stack2);
            if (sort1.getSortingCategory().equals(sort2.getSortingCategory())) {
               return sort1.getItemComparator().compare(stack1, stack2);
            }
         }

         SortingHandler.ItemType type1 = getType(stack1);
         SortingHandler.ItemType type2 = getType(stack2);
         return type1 == type2 ? type1.comparator.compare(stack1, stack2) : type1.ordinal() - type2.ordinal();
      }
   }

   private static SortingHandler.ItemType getType(ItemStack stack) {
      for(SortingHandler.ItemType type : SortingHandler.ItemType.values()) {
         if (type.fitsInType(stack)) {
            return type;
         }
      }

      throw new RuntimeException("Having an ItemStack that doesn't fit in any type is impossible.");
   }

   private static Predicate<ItemStack> classPredicate(Class<? extends Item> clazz) {
      return (s) -> {
         return !s.isEmpty() && clazz.isInstance(s.getItem());
      };
   }

   private static Predicate<ItemStack> inverseClassPredicate(Class<? extends Item> clazz) {
      return classPredicate(clazz).negate();
   }

   private static Predicate<ItemStack> itemPredicate(List<Item> list) {
      return (s) -> {
         return !s.isEmpty() && list.contains(s.getItem());
      };
   }

   public static Comparator<ItemStack> jointComparator(Comparator<ItemStack> finalComparator, Comparator<ItemStack>[] otherComparators) {
      if (otherComparators == null) {
         return jointComparator(finalComparator);
      } else {
         Comparator<ItemStack>[] resizedArray = (Comparator[])Arrays.copyOf(otherComparators, otherComparators.length + 1);
         resizedArray[otherComparators.length] = finalComparator;
         return jointComparator(resizedArray);
      }
   }

   @SafeVarargs
   public static Comparator<ItemStack> jointComparator(Comparator<ItemStack>... comparators) {
      return jointComparatorFallback((s1, s2) -> {
         for(Comparator<ItemStack> comparator : comparators) {
            if (comparator != null) {
               int compare = comparator.compare(s1, s2);
               if (compare != 0) {
                  return compare;
               }
            }
         }

         return 0;
      }, FALLBACK_COMPARATOR);
   }

   private static Comparator<ItemStack> jointComparatorFallback(Comparator<ItemStack> comparator, Comparator<ItemStack> fallback) {
      return (s1, s2) -> {
         int compare = comparator.compare(s1, s2);
         if (compare == 0) {
            return fallback == null ? 0 : fallback.compare(s1, s2);
         } else {
            return compare;
         }
      };
   }

   private static Comparator<ItemStack> listOrderComparator(List<Item> list) {
      return (stack1, stack2) -> {
         Item i1 = stack1.getItem();
         Item i2 = stack2.getItem();
         if (list.contains(i1)) {
            return list.contains(i2) ? list.indexOf(i1) - list.indexOf(i2) : 1;
         } else {
            return list.contains(i2) ? -1 : 0;
         }
      };
   }

   private static List<Item> list(Object... items) {
      List<Item> itemList = new ArrayList();

      for(Object o : items) {
         if (o != null) {
            if (o instanceof Item) {
               itemList.add((Item)o);
            } else if (o instanceof Block) {
               itemList.add(Item.getItemFromBlock((Block)o));
            } else if (o instanceof ItemStack) {
               itemList.add(((ItemStack)o).getItem());
            } else if (o instanceof String) {
               Item i = Item.getByNameOrId((String)o);
               if (i != null) {
                  itemList.add(i);
               }
            }
         }
      }

      return itemList;
   }

   private static int foodHealCompare(ItemStack stack1, ItemStack stack2) {
      return ((ItemFood)stack2.getItem()).getHealAmount(stack2) - ((ItemFood)stack1.getItem()).getHealAmount(stack1);
   }

   private static int foodSaturationCompare(ItemStack stack1, ItemStack stack2) {
      return (int)(((ItemFood)stack2.getItem()).getSaturationModifier(stack2) * 100.0F - ((ItemFood)stack1.getItem()).getSaturationModifier(stack1) * 100.0F);
   }

   private static int enchantmentCompare(ItemStack stack1, ItemStack stack2) {
      return enchantmentPower(stack2) - enchantmentPower(stack1);
   }

   private static int enchantmentPower(ItemStack stack) {
      if (!stack.isItemEnchanted()) {
         return 0;
      } else {
         Map<Enchantment, Integer> enchantments = EnchantmentHelper.getEnchantments(stack);
         int total = 0;

         for(Integer i : enchantments.values()) {
            total += i.intValue();
         }

         return total;
      }
   }

   private static int toolPowerCompare(ItemStack stack1, ItemStack stack2) {
      ToolMaterial mat1 = (ToolMaterial)ObfuscationReflectionHelper.getPrivateValue(ItemTool.class, (ItemTool)stack1.getItem(), "toolMaterial");
      ToolMaterial mat2 = (ToolMaterial)ObfuscationReflectionHelper.getPrivateValue(ItemTool.class, (ItemTool)stack2.getItem(), "toolMaterial");
      return (int)(mat2.getEfficiency() * 100.0F - mat1.getEfficiency() * 100.0F);
   }

   private static int swordPowerCompare(ItemStack stack1, ItemStack stack2) {
      return (int)(((ItemSword)stack2.getItem()).getAttackDamage() * 100.0F - ((ItemSword)stack1.getItem()).getAttackDamage() * 100.0F);
   }

   private static int armorSlotAndToughnessCompare(ItemStack stack1, ItemStack stack2) {
      ItemArmor armor1 = (ItemArmor)stack1.getItem();
      ItemArmor armor2 = (ItemArmor)stack2.getItem();
      EntityEquipmentSlot slot1 = armor1.armorType;
      EntityEquipmentSlot slot2 = armor2.armorType;
      return slot1 == slot2 ? armor2.getArmorMaterial().getDamageReductionAmount(slot2) - armor2.getArmorMaterial().getDamageReductionAmount(slot1) : slot2.getIndex() - slot1.getIndex();
   }

   private static int damageCompare(ItemStack stack1, ItemStack stack2) {
      return stack1.getItemDamage() - stack2.getItemDamage();
   }

   private static enum ItemType {
      FOOD(SortingHandler.classPredicate(ItemFood.class), SortingHandler.FOOD_COMPARATOR),
      TORCH(SortingHandler.list(Blocks.TORCH), new Comparator[0]),
      TOOL_PICKAXE(SortingHandler.classPredicate(ItemPickaxe.class), SortingHandler.TOOL_COMPARATOR),
      TOOL_SHOVEL(SortingHandler.classPredicate(ItemSpade.class), SortingHandler.TOOL_COMPARATOR),
      TOOL_AXE(SortingHandler.classPredicate(ItemAxe.class), SortingHandler.TOOL_COMPARATOR),
      TOOL_SWORD(SortingHandler.classPredicate(ItemSword.class), SortingHandler.SWORD_COMPARATOR),
      TOOL_GENERIC(SortingHandler.classPredicate(ItemTool.class), SortingHandler.TOOL_COMPARATOR),
      ARMOR(SortingHandler.classPredicate(ItemArmor.class), SortingHandler.ARMOR_COMPARATOR),
      BOW(SortingHandler.classPredicate(ItemBow.class), SortingHandler.BOW_COMPARATOR),
      ARROWS(SortingHandler.classPredicate(ItemArrow.class)),
      POTION(SortingHandler.classPredicate(ItemPotion.class)),
      REDSTONE(SortingHandler.list(Items.REDSTONE, Blocks.REDSTONE_TORCH, Items.REPEATER, Items.COMPARATOR, Blocks.LEVER, Blocks.STONE_BUTTON, Blocks.WOODEN_BUTTON), new Comparator[0]),
      MINECART(SortingHandler.classPredicate(ItemMinecart.class)),
      RAIL(SortingHandler.list(Blocks.RAIL, Blocks.GOLDEN_RAIL, Blocks.DETECTOR_RAIL, Blocks.ACTIVATOR_RAIL), new Comparator[0]),
      DYE(SortingHandler.classPredicate(ItemDye.class)),
      ANY(SortingHandler.inverseClassPredicate(ItemBlock.class)),
      BLOCK(SortingHandler.classPredicate(ItemBlock.class));

      private final Predicate<ItemStack> predicate;
      private final Comparator<ItemStack> comparator;

      @SafeVarargs
      private ItemType(List<Item> list, Comparator<ItemStack>[] comparators) {
         this(SortingHandler.itemPredicate(list), SortingHandler.jointComparator(SortingHandler.listOrderComparator(list), comparators));
      }

      private ItemType(Predicate<ItemStack> predicate) {
         this(predicate, SortingHandler.FALLBACK_COMPARATOR);
      }

      private ItemType(Predicate<ItemStack> predicate, Comparator<ItemStack> comparator) {
         this.predicate = predicate;
         this.comparator = comparator;
      }

      public boolean fitsInType(ItemStack stack) {
         return this.predicate.test(stack);
      }
   }
}
