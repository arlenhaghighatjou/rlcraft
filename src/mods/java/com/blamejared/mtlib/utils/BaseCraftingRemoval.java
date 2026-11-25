package com.blamejared.mtlib.utils;

import com.blamejared.mtlib.helpers.InputHelper;
import com.blamejared.mtlib.helpers.LogHelper;
import com.blamejared.mtlib.helpers.StackHelper;
import crafttweaker.api.item.IIngredient;
import java.util.LinkedList;
import java.util.List;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;

public class BaseCraftingRemoval extends BaseListRemoval<IRecipe> {
   public BaseCraftingRemoval(String name, List<IRecipe> list, List<IRecipe> recipes) {
      super(name, list, recipes);
   }

   public static List<IRecipe> getRecipes(List<IRecipe> list, IIngredient ingredient) {
      List<IRecipe> recipes = new LinkedList();

      for(IRecipe r : list) {
         if (r != null && r.getRecipeOutput() != ItemStack.EMPTY && StackHelper.matches(ingredient, InputHelper.toIItemStack(r.getRecipeOutput()))) {
            recipes.add(r);
         }
      }

      return recipes;
   }

   public String getRecipeInfo(IRecipe recipe) {
      return LogHelper.getStackDescription(recipe.getRecipeOutput());
   }
}
