package net.silentchaos512.lib.collection;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;

public final class StackList extends ArrayList<ItemStack> {
   private StackList() {
   }

   public static StackList of(ItemStack... stacks) {
      StackList newList = new StackList();
      Collections.addAll(newList, stacks);
      return newList;
   }

   public static StackList fromInventory(IInventory inventory) {
      StackList newList = new StackList();

      for(int i = 0; i < inventory.getSizeInventory(); ++i) {
         newList.add(inventory.getStackInSlot(i));
      }

      return newList;
   }

   public static StackList fromNBT(NBTTagList tagList) {
      StackList newList = new StackList();

      for(NBTBase nbt : tagList) {
         if (nbt instanceof NBTTagCompound) {
            newList.add(new ItemStack((NBTTagCompound)nbt));
         }
      }

      return newList;
   }

   public ItemStack firstOfType(Class<?> itemClass) {
      return this.firstMatch(itemClassMatcher(itemClass));
   }

   public ItemStack firstMatch(Predicate<ItemStack> predicate) {
      return (ItemStack)this.stream().filter(predicate).findFirst().orElse(ItemStack.EMPTY);
   }

   public ItemStack uniqueOfType(Class<?> itemClass) {
      return this.uniqueMatch(itemClassMatcher(itemClass));
   }

   public ItemStack uniqueMatch(Predicate<ItemStack> predicate) {
      return (ItemStack)this.stream().filter(predicate).collect(Collectors.collectingAndThen(Collectors.toList(), (list) -> {
         return list.size() == 1 ? (ItemStack)list.get(0) : ItemStack.EMPTY;
      }));
   }

   public Collection<ItemStack> allOfType(Class<?> itemClass) {
      return this.allMatches(itemClassMatcher(itemClass));
   }

   public Collection<ItemStack> allMatches(Predicate<ItemStack> predicate) {
      return (Collection)this.stream().filter(predicate).collect(Collectors.toList());
   }

   public int countOfType(Class<?> itemClass) {
      return this.countOfMatches(itemClassMatcher(itemClass));
   }

   public int countOfMatches(Predicate<ItemStack> predicate) {
      return (int)this.stream().filter(predicate).count();
   }

   private static Predicate<ItemStack> itemClassMatcher(Class<?> itemClass) {
      return (stack) -> {
         return itemClass.isInstance(stack.getItem());
      };
   }

   public boolean add(ItemStack itemStack) {
      return !itemStack.isEmpty() && super.add(itemStack);
   }

   public boolean addAll(Collection<? extends ItemStack> c) {
      boolean added = false;

      for(ItemStack stack : c) {
         if (!stack.isEmpty()) {
            added |= super.add(stack);
         }
      }

      return added;
   }

   public boolean addAll(int index, Collection<? extends ItemStack> c) {
      boolean added = false;

      for(ItemStack stack : c) {
         if (!stack.isEmpty()) {
            super.add(index, stack);
            added = true;
         }
      }

      return added;
   }

   public void add(int index, ItemStack element) {
      if (!element.isEmpty()) {
         super.add(index, element);
      }

   }
}
