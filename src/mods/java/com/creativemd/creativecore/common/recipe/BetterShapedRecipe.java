package com.creativemd.creativecore.common.recipe;

import com.creativemd.creativecore.common.utils.stack.InfoStack;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.util.NonNullList;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.registries.IForgeRegistryEntry.Impl;

public class BetterShapedRecipe extends Impl<IRecipe> implements IRecipe, IRecipeInfo {
   public InfoStack[] info;
   public ItemStack output;
   public int width;
   public int height;

   public BetterShapedRecipe(int width, InfoStack[] info, ItemStack output) {
      this.info = info;
      this.output = output;
      this.width = width;
      this.height = this.info.length / this.width;
   }

   public boolean matches(InventoryCrafting inv, World world) {
      for(int x = 0; x <= 3 - this.width; ++x) {
         for(int y = 0; y <= 3 - this.height; ++y) {
            if (this.checkMatch(inv, x, y, false)) {
               return true;
            }

            if (this.checkMatch(inv, x, y, true)) {
               return true;
            }
         }
      }

      return false;
   }

   private boolean checkMatch(InventoryCrafting inv, int startX, int startY, boolean mirror) {
      for(int x = 0; x < 3; ++x) {
         for(int y = 0; y < 3; ++y) {
            int subX = x - startX;
            int subY = y - startY;
            InfoStack target = null;
            if (subX >= 0 && subY >= 0 && subX < this.width && subY < this.height) {
               if (mirror) {
                  target = this.info[this.width - subX - 1 + subY * this.width];
               } else {
                  target = this.info[subX + subY * this.width];
               }
            }

            ItemStack slot = inv.getStackInRowAndColumn(x, y);
            if (target == null && !slot.isEmpty()) {
               return false;
            }

            if (target != null && slot.isEmpty()) {
               return false;
            }

            if (target != null && !slot.isEmpty() && !target.isInstanceIgnoreSize(slot)) {
               return false;
            }
         }
      }

      return true;
   }

   public ItemStack getCraftingResult(InventoryCrafting inventory) {
      return this.output.copy();
   }

   public ItemStack getRecipeOutput() {
      return this.output.copy();
   }

   public ItemStack[] getInput() {
      ItemStack[] stacks = new ItemStack[this.info.length];

      for(int i = 0; i < stacks.length; ++i) {
         if (this.info[i] != null) {
            stacks[i] = this.info[i].getItemStack();
         }
      }

      return stacks;
   }

   public int getWidth() {
      return this.width;
   }

   public NonNullList<ItemStack> getRemainingItems(InventoryCrafting inv) {
      NonNullList<ItemStack> nonnulllist = NonNullList.withSize(inv.getSizeInventory(), ItemStack.EMPTY);

      for(int i = 0; i < nonnulllist.size(); ++i) {
         ItemStack itemstack = inv.getStackInSlot(i);
         nonnulllist.set(i, ForgeHooks.getContainerItem(itemstack));
      }

      return nonnulllist;
   }

   public boolean canFit(int width, int height) {
      return width >= this.width && height >= this.height;
   }
}
