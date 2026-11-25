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
import forestry.api.recipes.ICentrifugeRecipe;
import forestry.api.recipes.RecipeManagers;
import forestry.factory.recipes.CentrifugeRecipe;
import java.util.HashMap;
import java.util.Map;
import net.minecraft.item.ItemStack;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenMethod;

@ZenClass("mods.forestry.Centrifuge")
@ModOnly("forestry")
@ZenRegister
public class Centrifuge {
   public static final String name = "Forestry Centrifuge";

   public Centrifuge() {
   }

   @ZenMethod
   public static void addRecipe(WeightedItemStack[] output, IItemStack ingredients, int packagingTime) {
      Map<ItemStack, Float> products = new HashMap();

      for(WeightedItemStack product : output) {
         products.put(InputHelper.toStack(product.getStack()), Float.valueOf(product.getChance()));
      }

      ModTweaker.LATE_ADDITIONS.add(new Centrifuge.Add(new CentrifugeRecipe(packagingTime, InputHelper.toStack(ingredients), products)));
   }

   @ZenMethod
   public static void removeRecipe(IIngredient input) {
      ModTweaker.LATE_REMOVALS.add(new Centrifuge.Remove(input));
   }

   private static class Add extends BaseAddForestry<ICentrifugeRecipe> {
      public Add(ICentrifugeRecipe recipe) {
         super("Forestry Centrifuge", RecipeManagers.centrifugeManager, recipe);
      }

      protected String getRecipeInfo() {
         return LogHelper.getStackDescription(((ICentrifugeRecipe)this.recipe).getInput());
      }
   }

   private static class Remove extends BaseRemoveForestry<ICentrifugeRecipe> {
      private IIngredient input;

      public Remove(IIngredient input) {
         super("Forestry Centrifuge", RecipeManagers.centrifugeManager);
         this.input = input;
      }

      protected String getRecipeInfo() {
         return this.input.toString();
      }

      public boolean checkIsRecipe(ICentrifugeRecipe recipe) {
         return recipe != null && StackHelper.matches(this.input, InputHelper.toIItemStack(recipe.getInput()));
      }
   }
}
