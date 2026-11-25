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
import crafttweaker.api.liquid.ILiquidStack;
import forestry.api.recipes.IFabricatorRecipe;
import forestry.api.recipes.IFabricatorSmeltingRecipe;
import forestry.api.recipes.RecipeManagers;
import forestry.core.recipes.ShapedRecipeCustom;
import forestry.factory.recipes.FabricatorRecipe;
import forestry.factory.recipes.FabricatorSmeltingRecipe;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import stanhebben.zenscript.annotations.Optional;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenMethod;

@ZenClass("mods.forestry.ThermionicFabricator")
@ModOnly("forestry")
@ZenRegister
public class ThermionicFabricator {
   public static final String nameSmelting = "Forestry Thermionic Fabricator (Smelting)";
   public static final String nameCasting = "Forestry Thermionic Fabricator (Casting)";

   public ThermionicFabricator() {
   }

   @ZenMethod
   public static void addSmelting(ILiquidStack liquidStack, IItemStack itemInput, int meltingPoint) {
      ModTweaker.LATE_ADDITIONS.add(new ThermionicFabricator.AddSmelting(new FabricatorSmeltingRecipe(InputHelper.toStack(itemInput), InputHelper.toFluid(liquidStack), meltingPoint)));
   }

   @ZenMethod
   public static void addCast(IItemStack output, IIngredient[][] ingredients, ILiquidStack liquidStack, @Optional IItemStack plan) {
      ShapedRecipeCustom patternRecipe = new ShapedRecipeCustom(InputHelper.toStack(output), InputHelper.toShapedObjects(ingredients));
      NonNullList<NonNullList<ItemStack>> ingredientsList = patternRecipe.getRawIngredients();
      IFabricatorRecipe recipe = new FabricatorRecipe(InputHelper.toStack(plan), InputHelper.toFluid(liquidStack), InputHelper.toStack(output), ingredientsList, patternRecipe.getOreDicts(), patternRecipe.getWidth(), patternRecipe.getHeight());
      ModTweaker.LATE_ADDITIONS.add(new ThermionicFabricator.AddCast(recipe));
   }

   @ZenMethod
   public static void removeSmelting(IIngredient itemInput) {
      ModTweaker.LATE_REMOVALS.add(new ThermionicFabricator.RemoveSmelting(itemInput));
   }

   @ZenMethod
   public static void removeCast(IIngredient product) {
      ModTweaker.LATE_REMOVALS.add(new ThermionicFabricator.RemoveCasts(product));
   }

   private static class AddCast extends BaseAddForestry<IFabricatorRecipe> {
      public AddCast(IFabricatorRecipe recipe) {
         super("Forestry Thermionic Fabricator (Casting)", RecipeManagers.fabricatorManager, recipe);
      }

      public String getRecipeInfo() {
         return LogHelper.getStackDescription(((IFabricatorRecipe)this.recipe).getRecipeOutput());
      }
   }

   private static class AddSmelting extends BaseAddForestry<IFabricatorSmeltingRecipe> {
      public AddSmelting(IFabricatorSmeltingRecipe recipe) {
         super("Forestry Thermionic Fabricator (Smelting)", RecipeManagers.fabricatorSmeltingManager, recipe);
      }

      public String getRecipeInfo() {
         return LogHelper.getStackDescription(((IFabricatorSmeltingRecipe)this.recipe).getResource());
      }
   }

   private static class RemoveCasts extends BaseRemoveForestry<IFabricatorRecipe> {
      private IIngredient product;

      public RemoveCasts(IIngredient product) {
         super("Forestry Thermionic Fabricator (Casting)", RecipeManagers.fabricatorManager);
         this.product = product;
      }

      public String getRecipeInfo() {
         return this.product.toString();
      }

      public boolean checkIsRecipe(IFabricatorRecipe recipe) {
         return recipe != null && StackHelper.matches(this.product, InputHelper.toIItemStack(recipe.getRecipeOutput()));
      }
   }

   private static class RemoveSmelting extends BaseRemoveForestry<IFabricatorSmeltingRecipe> {
      private IIngredient itemInput;

      public RemoveSmelting(IIngredient itemInput) {
         super("Forestry Thermionic Fabricator (Smelting)", RecipeManagers.fabricatorSmeltingManager);
         this.itemInput = itemInput;
      }

      public String getRecipeInfo() {
         return this.itemInput.toString();
      }

      public boolean checkIsRecipe(IFabricatorSmeltingRecipe recipe) {
         return recipe != null && StackHelper.matches(this.itemInput, InputHelper.toIItemStack(recipe.getResource()));
      }
   }
}
