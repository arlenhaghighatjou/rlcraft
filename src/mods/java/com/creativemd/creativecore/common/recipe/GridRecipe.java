package com.creativemd.creativecore.common.recipe;

import com.creativemd.creativecore.common.utils.stack.InfoStack;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;

public class GridRecipe extends Recipe {
   public int width;
   public int height;

   public GridRecipe(ItemStack[] output, int width, int height, Object... input) {
      super(output, input);
      this.width = width;
      this.height = height;
   }

   public GridRecipe(ItemStack[] output, int width, int height, InfoStack... input) {
      super(output, input);
      this.width = width;
      this.height = height;
   }

   public void consumeRecipe(IInventory inventory, int width, int heigt) {
      this.consumeRecipe(inventory, width, heigt, 1);
   }

   public void consumeRecipe(IInventory inventory, int width, int heigt, int amount) {
      ItemStack[] inv = this.getObjectInValidOrder(inventory, width, heigt);

      for(int i = 0; i < inv.length; ++i) {
         if (!inv[i].isEmpty()) {
            ItemStack stack = inv[i];
            int index = indexOf(inventory, stack);
            if (index != -1 && this.input[i].isInstance(stack)) {
               stack.shrink(this.input[i].stackSize * amount);
               if (stack.isEmpty()) {
                  inventory.setInventorySlotContents(index, ItemStack.EMPTY);
               }
            }
         }
      }

   }

   public int getNumberofResults(IInventory inventory, int InvWidth, int InvHeigt) {
      ItemStack[] inv = this.getObjectInValidOrder(inventory, InvWidth, InvHeigt);
      if (!this.isValidRecipe(inv)) {
         return 0;
      } else {
         int number = Integer.MAX_VALUE;

         for(int i = 0; i < this.input.length; ++i) {
            int uses = inv[i].getCount() / this.input[i].stackSize;
            number = Math.min(uses, number);
         }

         return number;
      }
   }

   protected ItemStack[] getObjectInValidOrder(IInventory inventory, int InvWidth, int InvHeigt) {
      if (InvWidth >= this.width && InvHeigt >= this.height) {
         int posX = 0;
         int posY = 0;
         int posX2 = -1;
         int posY2 = -1;
         boolean foundX = false;

         for(int x = 0; x < InvWidth; ++x) {
            for(int y = 0; y < InvHeigt; ++y) {
               if (!inventory.getStackInSlot(x + y * InvHeigt).isEmpty()) {
                  foundX = true;
                  posX2 = x;
               }
            }

            if (!foundX) {
               ++posX;
            }
         }

         boolean foundY = false;

         for(int y = 0; y < InvHeigt; ++y) {
            for(int x = 0; x < InvWidth; ++x) {
               if (!inventory.getStackInSlot(x + y * InvHeigt).isEmpty()) {
                  foundY = true;
                  posY2 = y;
               }
            }

            if (!foundY) {
               ++posY;
            }
         }

         if (posX2 >= 0 && posY2 >= 0) {
            int gridWidth = posX2 - posX + 1;
            int gridHeight = posY2 - posY + 1;
            if (gridWidth == this.width && gridHeight == this.height) {
               ItemStack[] inv = new ItemStack[this.input.length];

               for(int y = 0; y < this.height; ++y) {
                  for(int x = 0; x < this.width; ++x) {
                     inv[y * this.width + x] = inventory.getStackInSlot(x + y * InvWidth);
                  }
               }

               return inv;
            } else {
               return null;
            }
         } else {
            return null;
         }
      } else {
         return null;
      }
   }

   public boolean isValidRecipe(IInventory inventory, int InvWidth, int InvHeigt) {
      ItemStack[] inv = this.getObjectInValidOrder(inventory, InvWidth, InvHeigt);
      return inv == null ? false : this.isValidRecipe(inv);
   }

   protected boolean isStackValid(ItemStack stack, InfoStack input) {
      if (stack.isEmpty() && input == null) {
         return true;
      } else {
         return input != null ? input.isInstance(stack) : false;
      }
   }

   protected boolean isValidRecipe(ItemStack[] inv) {
      for(int i = 0; i < this.input.length; ++i) {
         if (!this.isStackValid(inv[i], this.input[i])) {
            return false;
         }
      }

      return true;
   }
}
