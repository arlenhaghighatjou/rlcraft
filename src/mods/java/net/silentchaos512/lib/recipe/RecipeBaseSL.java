package net.silentchaos512.lib.recipe;

import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.util.NonNullList;
import net.minecraftforge.registries.IForgeRegistryEntry.Impl;

public abstract class RecipeBaseSL extends Impl<IRecipe> implements IRecipe {
   public RecipeBaseSL() {
   }

   public boolean canFit(int width, int height) {
      return true;
   }

   public boolean isDynamic() {
      return true;
   }

   public NonNullList<ItemStack> getRemainingItems(InventoryCrafting inv) {
      for(int i = 0; i < inv.getSizeInventory(); ++i) {
         ItemStack stack = inv.getStackInSlot(i);
         if (!stack.isEmpty() && !stack.getItem().hasContainerItem(stack)) {
            stack.shrink(1);
            inv.setInventorySlotContents(i, stack);
         }
      }

      return NonNullList.create();
   }

   public static NonNullList<ItemStack> getNonEmptyStacks(InventoryCrafting inv) {
      NonNullList<ItemStack> list = NonNullList.create();

      for(int i = 0; i < inv.getSizeInventory(); ++i) {
         ItemStack stack = inv.getStackInSlot(i);
         if (!stack.isEmpty()) {
            list.add(stack);
         }
      }

      return list;
   }
}
