package com.blamejared.compat.forestry;

import com.blamejared.ModTweaker;
import com.blamejared.mtlib.helpers.InputHelper;
import com.blamejared.mtlib.helpers.LogHelper;
import com.blamejared.mtlib.helpers.StackHelper;
import com.blamejared.mtlib.utils.BaseAddForestry;
import com.blamejared.mtlib.utils.BaseRemoveForestry;
import crafttweaker.annotations.ModOnly;
import crafttweaker.annotations.ZenRegister;
import crafttweaker.api.item.IIngredient;
import crafttweaker.api.item.IItemStack;
import crafttweaker.api.item.WeightedItemStack;
import crafttweaker.api.liquid.ILiquidStack;
import forestry.api.recipes.ISqueezerRecipe;
import forestry.api.recipes.RecipeManagers;
import forestry.factory.recipes.SqueezerRecipe;
import net.minecraft.item.ItemStack;
import stanhebben.zenscript.annotations.Optional;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenMethod;

@ZenClass("mods.forestry.Squeezer")
@ModOnly("forestry")
@ZenRegister
public class Squeezer {
   public static final String name = "Forestry Squeezer";

   public Squeezer() {
   }

   @ZenMethod
   public static void addRecipe(ILiquidStack fluidOutput, IItemStack[] ingredients, int timePerItem, @Optional WeightedItemStack itemOutput) {
      ModTweaker.LATE_ADDITIONS.add(new Squeezer.Add(new SqueezerRecipe(timePerItem, InputHelper.toNonNullList(InputHelper.toStacks(ingredients)), InputHelper.toFluid(fluidOutput), itemOutput != null ? InputHelper.toStack(itemOutput.getStack()) : ItemStack.EMPTY, itemOutput != null ? itemOutput.getChance() : 0.0F)));
   }

   @ZenMethod
   public static void removeRecipe(ILiquidStack liquid, @Optional IIngredient[] ingredients) {
      ModTweaker.LATE_REMOVALS.add(new Squeezer.Remove(liquid, ingredients));
   }

   private static class Add extends BaseAddForestry<ISqueezerRecipe> {
      public Add(ISqueezerRecipe recipe) {
         super("Forestry Squeezer", RecipeManagers.squeezerManager, recipe);
      }

      public String getRecipeInfo() {
         return LogHelper.getStackDescription(((ISqueezerRecipe)this.recipe).getFluidOutput());
      }
   }

   private static class Remove extends BaseRemoveForestry<ISqueezerRecipe> {
      private final ILiquidStack liquid;
      private final IIngredient[] ingredients;

      public Remove(ILiquidStack liquid, IIngredient[] ingredients) {
         super("Forestry Squeezer", RecipeManagers.squeezerManager);
         this.liquid = liquid;
         this.ingredients = ingredients;
      }

      public String getRecipeInfo() {
         return this.liquid.getDisplayName();
      }

      public boolean checkIsRecipe(ISqueezerRecipe recipe) {
         if (!StackHelper.matches(this.liquid, InputHelper.toILiquidStack(recipe.getFluidOutput()))) {
            return false;
         } else if (this.ingredients != null) {
            boolean matched = true;
            if (this.ingredients.length != recipe.getResources().size()) {
               return false;
            } else {
               for(int i = 0; i < this.ingredients.length; ++i) {
                  if (!StackHelper.matches(this.ingredients[i], InputHelper.toIItemStack((ItemStack)recipe.getResources().get(i)))) {
                     matched = false;
                  }
               }

               return matched;
            }
         } else {
            return true;
         }
      }
   }
}
