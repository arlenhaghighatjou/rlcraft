package com.creativemd.creativecore.common.recipe;

import com.creativemd.creativecore.common.utils.stack.InfoStack;
import java.util.ArrayList;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.util.NonNullList;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.registries.IForgeRegistryEntry.Impl;

public class BetterShapelessRecipe extends Impl<IRecipe> implements IRecipe, IRecipeInfo {
   public ArrayList<InfoStack> info;
   public ItemStack output;
   public int width;

   public BetterShapelessRecipe(ArrayList<InfoStack> info, ItemStack output) {
      this.info = info;
      this.output = output;
      this.width = Math.min(info.size(), 3);
   }

   public boolean matches(InventoryCrafting inv, World world) {
      ArrayList<InfoStack> list = new ArrayList(this.info);

      for(int i = 0; i < 3; ++i) {
         for(int j = 0; j < 3; ++j) {
            ItemStack itemstack = inv.getStackInRowAndColumn(j, i);
            if (!itemstack.isEmpty()) {
               boolean flag = false;

               for(InfoStack stackInfo : list) {
                  if (stackInfo.isInstanceIgnoreSize(itemstack)) {
                     flag = true;
                     list.remove(stackInfo);
                     break;
                  }
               }

               if (!flag) {
                  return false;
               }
            }
         }
      }

      return list.isEmpty();
   }

   public ItemStack getCraftingResult(InventoryCrafting inventory) {
      return this.output.copy();
   }

   public ItemStack getRecipeOutput() {
      return this.output.copy();
   }

   public ItemStack[] getInput() {
      ItemStack[] stacks = new ItemStack[this.info.size()];

      for(int i = 0; i < stacks.length; ++i) {
         if (this.info.get(i) != null) {
            stacks[i] = ((InfoStack)this.info.get(i)).getItemStack();
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
      return width * height >= this.info.size();
   }
}
