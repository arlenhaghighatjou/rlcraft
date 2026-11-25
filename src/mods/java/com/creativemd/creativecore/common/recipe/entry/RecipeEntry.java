package com.creativemd.creativecore.common.recipe.entry;

import net.minecraft.block.Block;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;

public abstract class RecipeEntry {
   public RecipeEntry() {
   }

   public abstract boolean isEntry(ItemStack var1);

   public abstract int getStackSize(ItemStack var1);

   public void consumeItemStack(int amount, int index, IInventory inventory) {
      ItemStack stack = inventory.getStackInSlot(index);
      stack.shrink(amount * this.getStackSize(stack));
      if (stack.isEmpty()) {
         inventory.setInventorySlotContents(index, (ItemStack)null);
      }

   }

   public static boolean isObject(ItemStack stack, Object object) {
      if (stack == null) {
         return object == null;
      } else if (object instanceof Block) {
         return Block.getBlockFromItem(stack.getItem()) == object;
      } else if (object instanceof Item) {
         return stack.getItem() == object;
      } else if (object instanceof ItemStack) {
         ItemStack input = (ItemStack)object;
         if (stack.getItem() != input.getItem()) {
            return false;
         } else if (stack.getMetadata() != input.getMetadata()) {
            return false;
         } else if (stack.getTagCompound() != input.getTagCompound()) {
            return stack.hasTagCompound() && input.hasTagCompound() ? stack.getTagCompound().equals(input.getTagCompound()) : false;
         } else {
            return true;
         }
      } else if (object instanceof String) {
         int[] ores = OreDictionary.getOreIDs(stack);
         int id = OreDictionary.getOreID((String)object);

         for(int i = 0; i < ores.length; ++i) {
            if (ores[i] == id) {
               return true;
            }
         }

         return false;
      } else {
         return stack == null;
      }
   }
}
