package com.creativemd.creativecore.common.recipe;

import com.creativemd.creativecore.common.utils.stack.InfoStack;
import java.util.ArrayList;
import java.util.Arrays;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;

public class Recipe {
   public ItemStack[] output;
   public InfoStack[] input;

   public Recipe(ItemStack output, Object... input) {
      this(new ItemStack[]{output}, input);
   }

   public Recipe(ItemStack[] output, InfoStack[] info) {
      this.output = output;
      this.input = info;
   }

   public Recipe(ItemStack[] output, Object... input) {
      ArrayList<InfoStack> info = new ArrayList();

      for(int i = 0; i < input.length; ++i) {
         if (input[i] != null) {
            if (input[i] instanceof InfoStack) {
               info.add((InfoStack)input[i]);
            } else {
               InfoStack infoStack = InfoStack.parseObject(input[i]);
               if (infoStack != null) {
                  info.add(infoStack);
               }
            }
         }
      }

      this.output = output;
      this.input = (InfoStack[])info.toArray(new InfoStack[0]);
   }

   public ItemStack[] getInputStacks() {
      ItemStack[] stacks = new ItemStack[this.input.length];

      for(int i = 0; i < stacks.length; ++i) {
         if (this.input[i] != null) {
            stacks[i] = this.input[i].getItemStack();
         }
      }

      return stacks;
   }

   public void consumeRecipe(IInventory inventory) {
      this.consumeRecipe(inventory, 1);
   }

   public void consumeRecipe(IInventory inventory, int amount) {
      ArrayList<InfoStack> info = new ArrayList(Arrays.asList(this.input));

      for(int i = 0; i < inventory.getSizeInventory(); ++i) {
         ItemStack stack = inventory.getStackInSlot(i);
         if (stack != null) {
            for(int j = 0; j < info.size(); ++j) {
               if (((InfoStack)info.get(j)).isInstance(stack)) {
                  stack.shrink(((InfoStack)info.get(j)).stackSize * amount);
                  if (stack.isEmpty()) {
                     inventory.setInventorySlotContents(i, ItemStack.EMPTY);
                  }

                  info.remove(j);
                  break;
               }
            }
         }
      }

   }

   public ItemStack[] getCraftingResult(IInventory inventory) {
      return this.output;
   }

   public int getNumberofResults(IInventory inventory) {
      int limit = 1;
      ArrayList<InfoStack> info = new ArrayList(Arrays.asList(this.input));

      for(int i = 0; i < inventory.getSizeInventory(); ++i) {
         ItemStack stack = inventory.getStackInSlot(i);
         if (stack != null) {
            boolean found = false;

            for(int j = 0; j < info.size(); ++j) {
               if (((InfoStack)info.get(i)).isInstance(stack)) {
                  limit = Math.min(limit, ((InfoStack)info.get(i)).getAmount(stack));
                  found = true;
                  info.remove(j);
                  break;
               }
            }

            if (!found) {
               return 0;
            }
         }
      }

      return info.size() == 0 ? limit : 0;
   }

   public boolean isValidRecipe(IInventory inventory) {
      ArrayList<InfoStack> info = new ArrayList(Arrays.asList(this.input));

      for(int i = 0; i < inventory.getSizeInventory(); ++i) {
         ItemStack stack = inventory.getStackInSlot(i);
         if (stack != null) {
            boolean found = false;

            for(int j = 0; j < info.size(); ++j) {
               if (((InfoStack)info.get(j)).isInstance(stack)) {
                  found = true;
                  info.remove(j);
                  break;
               }
            }

            if (!found) {
               return false;
            }
         }
      }

      return info.size() == 0;
   }

   public static int indexOf(IInventory inventory, ItemStack stack) {
      for(int i = 0; i < inventory.getSizeInventory(); ++i) {
         if (inventory.getStackInSlot(i) == stack) {
            return i;
         }
      }

      return -1;
   }
}
