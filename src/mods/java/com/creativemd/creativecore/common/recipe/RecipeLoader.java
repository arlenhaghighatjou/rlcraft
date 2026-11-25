package com.creativemd.creativecore.common.recipe;

import java.lang.reflect.Method;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.ShapedRecipes;
import net.minecraft.item.crafting.ShapelessRecipes;
import net.minecraftforge.fml.relauncher.ReflectionHelper;
import net.minecraftforge.oredict.ShapedOreRecipe;
import net.minecraftforge.oredict.ShapelessOreRecipe;

public class RecipeLoader {
   public RecipeLoader() {
   }

   public static int[] getRecipeDimensions(IRecipe recipe) {
      for(int i = 0; i < 3; ++i) {
         for(int j = 0; j < 3; ++j) {
            if (recipe.canFit(i, j)) {
               return new int[]{i, j};
            }
         }
      }

      return new int[]{3, 3};
   }

   public static Object[] getInput(IRecipe recipe, boolean vanillaOnly) {
      if (recipe instanceof IRecipeInfo) {
         return ((IRecipeInfo)recipe).getInput();
      } else if (recipe instanceof ShapedRecipes) {
         return ((ShapedRecipes)recipe).recipeItems.toArray();
      } else if (recipe instanceof ShapedOreRecipe) {
         return ((ShapedOreRecipe)recipe).getIngredients().toArray();
      } else if (recipe instanceof ShapelessRecipes) {
         return ((ShapelessRecipes)recipe).recipeItems.toArray();
      } else {
         return recipe instanceof ShapelessOreRecipe ? ((ShapelessOreRecipe)recipe).getIngredients().toArray() : null;
      }
   }

   public static Object[] getInput(IRecipe recipe) {
      return getInput(recipe, false);
   }

   public static int getWidth(IRecipe recipe) {
      try {
         Method m = recipe.getClass().getMethod("getWidth");
         if (m != null) {
            return ((Integer)m.invoke(recipe)).intValue();
         }
      } catch (Exception var2) {
         ;
      }

      try {
         return ((Integer)ReflectionHelper.getPrivateValue(recipe.getClass(), recipe, new String[]{"width"})).intValue();
      } catch (Exception var3) {
         if (recipe instanceof IRecipeInfo) {
            return ((IRecipeInfo)recipe).getWidth();
         } else {
            return recipe instanceof ShapedRecipes ? ((ShapedRecipes)recipe).recipeWidth : 3;
         }
      }
   }
}
