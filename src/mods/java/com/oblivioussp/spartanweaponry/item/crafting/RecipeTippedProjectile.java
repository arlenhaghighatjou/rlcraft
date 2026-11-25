package com.oblivioussp.spartanweaponry.item.crafting;

import javax.annotation.Nullable;
import net.minecraft.init.Items;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.potion.PotionUtils;
import net.minecraft.util.NonNullList;
import net.minecraft.world.World;
import net.minecraftforge.registries.IForgeRegistryEntry.Impl;

public class RecipeTippedProjectile extends Impl<IRecipe> implements IRecipe {
   protected final Item projectileIn;
   protected final Item projectileOut;

   public RecipeTippedProjectile(Item projIn, Item projOut) {
      this.projectileIn = projIn;
      this.projectileOut = projOut;
   }

   public boolean matches(InventoryCrafting inv, World worldIn) {
      if (inv.getWidth() == 3 && inv.getHeight() == 3) {
         for(int i = 0; i < inv.getWidth(); ++i) {
            for(int j = 0; j < inv.getHeight(); ++j) {
               ItemStack itemstack = inv.getStackInRowAndColumn(i, j);
               if (itemstack == null) {
                  return false;
               }

               Item item = itemstack.getItem();
               if (i == 1 && j == 1) {
                  if (item != Items.LINGERING_POTION) {
                     return false;
                  }
               } else if (item != this.projectileIn) {
                  return false;
               }
            }
         }

         return true;
      } else {
         return false;
      }
   }

   public ItemStack getCraftingResult(InventoryCrafting inv) {
      ItemStack potion = inv.getStackInRowAndColumn(1, 1);
      if (potion != null && potion.getItem() == Items.LINGERING_POTION) {
         ItemStack result = new ItemStack(this.projectileOut, 8);
         PotionUtils.addPotionToItemStack(result, PotionUtils.getPotionFromItem(potion));
         PotionUtils.appendEffects(result, PotionUtils.getFullEffectsFromItem(potion));
         return result;
      } else {
         return ItemStack.EMPTY;
      }
   }

   @Nullable
   public ItemStack getRecipeOutput() {
      return ItemStack.EMPTY;
   }

   public NonNullList<ItemStack> getRemainingItems(InventoryCrafting inv) {
      return NonNullList.withSize(inv.getSizeInventory(), ItemStack.EMPTY);
   }

   public boolean isDynamic() {
      return true;
   }

   public boolean canFit(int width, int height) {
      return width >= 2 && height >= 2;
   }
}
