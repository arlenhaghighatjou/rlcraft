package com.fantasticsource.mctools.items;

import java.util.Collection;
import java.util.Iterator;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

public class ItemMatcher {
   public ItemMatcher() {
   }

   public static boolean stacksMatch(ItemStack stack1, ItemStack stack2) {
      return stacksMatch(stack1, stack2, true);
   }

   public static boolean stacksMatch(ItemStack stack1, ItemStack stack2, boolean testCount) {
      if (stack1 == stack2) {
         return true;
      } else if (testCount && stack1.getCount() != stack2.getCount()) {
         return false;
      } else {
         Item item = stack1.getItem();
         if (item != stack2.getItem()) {
            return false;
         } else if (!item.isDamageable() && item.getMetadata(stack1) != item.getMetadata(stack2)) {
            return false;
         } else {
            NBTTagCompound compound1 = stack1.getTagCompound();
            NBTTagCompound compound2 = stack2.getTagCompound();
            return compound1 == null ? compound2 == null : compound1.equals(compound2);
         }
      }
   }

   public static boolean stacksMatch(Collection<ItemStack> stacks1, Collection<ItemStack> stacks2) {
      return stacksMatch(stacks1, stacks2, true);
   }

   public static boolean stacksMatch(Collection<ItemStack> stacks1, Collection<ItemStack> stacks2, boolean testCount) {
      Iterator iterator1 = stacks1.iterator();

      for(ItemStack stack2 : stacks2) {
         ItemStack stack1 = (ItemStack)iterator1.next();
         if (stack1 != stack2) {
            if (testCount && stack1.getCount() != stack2.getCount()) {
               return false;
            }

            Item item = stack1.getItem();
            if (item != stack2.getItem()) {
               return false;
            }

            if (!item.isDamageable() && item.getMetadata(stack1) != item.getMetadata(stack2)) {
               return false;
            }
         }
      }

      iterator1 = stacks1.iterator();
      Iterator var11 = stacks2.iterator();

      while(true) {
         if (!var11.hasNext()) {
            return true;
         }

         ItemStack stack2 = (ItemStack)var11.next();
         ItemStack stack1 = (ItemStack)iterator1.next();
         NBTTagCompound compound1 = stack1.getTagCompound();
         NBTTagCompound compound2 = stack2.getTagCompound();
         if (compound1 == null) {
            if (compound2 != null) {
               break;
            }
         } else if (!compound1.equals(compound2)) {
            break;
         }
      }

      return false;
   }

   public static boolean stacksMatch(ItemStack[] stacks1, ItemStack[] stacks2) {
      return stacksMatch(stacks1, stacks2, true);
   }

   public static boolean stacksMatch(ItemStack[] stacks1, ItemStack[] stacks2, boolean testCount) {
      int size = stacks1.length;

      for(int i = 0; i < size; ++i) {
         ItemStack stack1 = stacks1[i];
         ItemStack stack2 = stacks2[i];
         if (stack1 != stack2) {
            if (testCount && stack1.getCount() != stack2.getCount()) {
               return false;
            }

            Item item = stack1.getItem();
            if (item != stack2.getItem()) {
               return false;
            }

            if (!item.isDamageable() && item.getMetadata(stack1) != item.getMetadata(stack2)) {
               return false;
            }
         }
      }

      int i = 0;

      while(true) {
         if (i >= size) {
            return true;
         }

         ItemStack stack1 = stacks1[i];
         ItemStack stack2 = stacks2[i];
         NBTTagCompound compound1 = stack1.getTagCompound();
         NBTTagCompound compound2 = stack2.getTagCompound();
         if (compound1 == null) {
            if (compound2 != null) {
               break;
            }
         } else if (!compound1.equals(compound2)) {
            break;
         }

         ++i;
      }

      return false;
   }
}
