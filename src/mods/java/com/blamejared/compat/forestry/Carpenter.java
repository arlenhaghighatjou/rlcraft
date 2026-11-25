package com.blamejared.compat.forestry;

import com.blamejared.ModTweaker;
import com.blamejared.mtlib.helpers.InputHelper;
import com.blamejared.mtlib.utils.BaseAddForestry;
import com.blamejared.mtlib.utils.BaseRemoveForestry;
import crafttweaker.annotations.ModOnly;
import crafttweaker.annotations.ZenRegister;
import crafttweaker.api.item.IIngredient;
import crafttweaker.api.item.IItemStack;
import crafttweaker.api.liquid.ILiquidStack;
import forestry.api.recipes.ICarpenterRecipe;
import forestry.api.recipes.RecipeManagers;
import forestry.core.recipes.ShapedRecipeCustom;
import forestry.factory.recipes.CarpenterRecipe;
import java.util.Arrays;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;
import stanhebben.zenscript.annotations.Optional;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenMethod;

@ZenClass("mods.forestry.Carpenter")
@ModOnly("forestry")
@ZenRegister
public class Carpenter {
   public static final String name = "Forestry Carpenter";

   public Carpenter() {
   }

   @ZenMethod
   public static void addRecipe(IItemStack output, IIngredient[][] ingredients, int packagingTime, @Optional ILiquidStack fluidInput, @Optional IItemStack box) {
      ModTweaker.LATE_ADDITIONS.add(new Carpenter.Add(new CarpenterRecipe(packagingTime, InputHelper.toFluid(fluidInput), InputHelper.toStack(box), new ShapedRecipeCustom(InputHelper.toStack(output), InputHelper.toShapedObjects(ingredients)))));
   }

   private static IItemStack[][] transform(IItemStack[] arr, int N) {
      int M = (arr.length + N - 1) / N;
      IItemStack[][] mat = new IItemStack[M][];
      int start = 0;

      for(int r = 0; r < M; ++r) {
         int L = Math.min(N, arr.length - start);
         mat[r] = (IItemStack[])Arrays.copyOfRange(arr, start, start + L);
         start += L;
      }

      return mat;
   }

   @ZenMethod
   public static void removeRecipe(IItemStack output, @Optional ILiquidStack fluidInput) {
      ModTweaker.LATE_REMOVALS.add(new Carpenter.Remove(output, fluidInput));
   }

   private static class Add extends BaseAddForestry<ICarpenterRecipe> {
      protected Add(ICarpenterRecipe recipe) {
         super("Forestry Carpenter", RecipeManagers.carpenterManager, recipe);
      }

      protected String getRecipeInfo() {
         return ((ICarpenterRecipe)this.recipe).getCraftingGridRecipe().getOutput().getDisplayName();
      }
   }

   private static class Remove extends BaseRemoveForestry<ICarpenterRecipe> {
      private ItemStack output;
      private FluidStack fluidInput;

      public Remove(IItemStack output, ILiquidStack fluidInput) {
         super("Forestry Carpenter", RecipeManagers.carpenterManager);
         this.output = InputHelper.toStack(output);
         this.fluidInput = InputHelper.toFluid(fluidInput);
      }

      public boolean checkIsRecipe(ICarpenterRecipe recipe) {
         if (recipe.getCraftingGridRecipe().getOutput().isItemEqual(this.output)) {
            return this.fluidInput == null ? true : this.fluidInput.isFluidEqual(recipe.getFluidResource());
         } else {
            return false;
         }
      }

      protected String getRecipeInfo() {
         return this.output.getDisplayName();
      }
   }
}
