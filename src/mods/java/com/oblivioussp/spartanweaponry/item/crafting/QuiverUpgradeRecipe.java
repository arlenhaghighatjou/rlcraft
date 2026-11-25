package com.oblivioussp.spartanweaponry.item.crafting;

import com.oblivioussp.spartanweaponry.item.ItemQuiverBase;
import com.oblivioussp.spartanweaponry.util.Log;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.item.crafting.ShapedRecipes;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.NonNullList;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;

public class QuiverUpgradeRecipe extends ShapedRecipes {
   public QuiverUpgradeRecipe(String regName, String group, int width, int height, NonNullList<Ingredient> ingredients, ItemStack result) {
      super(group, width, height, ingredients, result);
      this.setRegistryName("spartanweaponry", regName);
      this.validateRecipe();
   }

   public ItemStack getCraftingResult(InventoryCrafting inv) {
      ItemStack resultStack = super.getCraftingResult(inv);
      ItemStack quiverStack = ItemStack.EMPTY;
      if (!(resultStack.getItem() instanceof ItemQuiverBase)) {
         Log.error("Quiver upgrade recipe output is not a Quiver!");
      }

      for(int i = 0; i < inv.getSizeInventory(); ++i) {
         ItemStack stack = inv.getStackInSlot(i);
         if (stack.getItem() instanceof ItemQuiverBase) {
            quiverStack = stack;
            break;
         }
      }

      if (!quiverStack.isEmpty()) {
         IItemHandler itemHandler = (IItemHandler)quiverStack.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, (EnumFacing)null);
         IItemHandler resultItemHandler = (IItemHandler)resultStack.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, (EnumFacing)null);

         for(int i = 0; i < itemHandler.getSlots(); ++i) {
            ItemStack stack = itemHandler.getStackInSlot(i);
            if (!stack.isEmpty()) {
               resultItemHandler.insertItem(i, stack, false);
            }
         }

         resultStack.setTagCompound(resultStack.getItem().getNBTShareTag(resultStack));
      }

      return resultStack;
   }

   public void validateRecipe() {
      if (!(this.getRecipeOutput().getItem() instanceof ItemQuiverBase)) {
         throw new IllegalArgumentException("Recipe \"" + this.getRegistryName().toString() + "\" has invalid output! Output must be a ItemQuiverBase item!");
      } else {
         int inputQuiverCount = 0;

         for(Ingredient ingredient : this.getIngredients()) {
            if (ingredient.getMatchingStacks().length > 0) {
               ItemStack stack = ingredient.getMatchingStacks()[0];
               if (stack.getItem() instanceof ItemQuiverBase) {
                  ++inputQuiverCount;
               }
            }
         }

         if (inputQuiverCount != 1) {
            throw new IllegalArgumentException("Recipe \"" + this.getRegistryName().toString() + "\" has invalid inputs! Input must contain only one ItemQuiverBase item!");
         }
      }
   }
}
