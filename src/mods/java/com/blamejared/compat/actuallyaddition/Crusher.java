package com.blamejared.compat.actuallyaddition;

import com.blamejared.ModTweaker;
import com.blamejared.mtlib.helpers.InputHelper;
import com.blamejared.mtlib.helpers.LogHelper;
import com.blamejared.mtlib.utils.BaseListAddition;
import com.blamejared.mtlib.utils.BaseListRemoval;
import crafttweaker.annotations.ModOnly;
import crafttweaker.annotations.ZenRegister;
import crafttweaker.api.item.IIngredient;
import crafttweaker.api.item.IItemStack;
import crafttweaker.api.minecraft.CraftTweakerMC;
import de.ellpeck.actuallyadditions.api.ActuallyAdditionsAPI;
import de.ellpeck.actuallyadditions.api.recipe.CrusherRecipe;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;
import stanhebben.zenscript.annotations.Optional;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenMethod;

@ZenClass("mods.actuallyadditions.Crusher")
@ModOnly("actuallyadditions")
@ZenRegister
public class Crusher {
   public Crusher() {
   }

   @ZenMethod
   public static void addRecipe(IItemStack output, IIngredient input, @Optional IItemStack outputSecondary, @Optional int outputSecondaryChance) {
      ModTweaker.LATE_ADDITIONS.add(new Crusher.Add(Collections.singletonList(new CrusherRecipe(CraftTweakerMC.getIngredient(input), InputHelper.toStack(output), InputHelper.toStack(outputSecondary), outputSecondaryChance))));
   }

   @ZenMethod
   public static void removeRecipe(IItemStack output) {
      ModTweaker.LATE_REMOVALS.add(new Crusher.Remove(output));
   }

   private static class Add extends BaseListAddition<CrusherRecipe> {
      protected Add(List<CrusherRecipe> recipes) {
         super("Crusher", ActuallyAdditionsAPI.CRUSHER_RECIPES, recipes);
      }

      public String getRecipeInfo(CrusherRecipe recipe) {
         return LogHelper.getStackDescription(recipe.getInput());
      }
   }

   private static class Remove extends BaseListRemoval<CrusherRecipe> {
      private IItemStack output;

      protected Remove(IItemStack output) {
         super("Crusher", ActuallyAdditionsAPI.CRUSHER_RECIPES);
         this.output = output;
      }

      public void apply() {
         ActuallyAdditionsAPI.CRUSHER_RECIPES.forEach((recipe) -> {
            if (this.output.matches(InputHelper.toIItemStack(recipe.getOutputOne()))) {
               this.recipes.add(recipe);
            }

         });
         super.apply();
      }

      public String getRecipeInfo(CrusherRecipe recipe) {
         return LogHelper.getStackDescription(recipe.getOutputOne());
      }
   }
}
