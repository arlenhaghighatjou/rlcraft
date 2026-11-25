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
import de.ellpeck.actuallyadditions.api.recipe.LensConversionRecipe;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Stream;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenMethod;

@ZenClass("mods.actuallyadditions.AtomicReconstructor")
@ModOnly("actuallyadditions")
@ZenRegister
public class AtomicReconstructor {
   public AtomicReconstructor() {
   }

   @ZenMethod
   public static void addRecipe(IItemStack output, IIngredient input, int energyUsed) {
      ModTweaker.LATE_ADDITIONS.add(new AtomicReconstructor.Add(Collections.singletonList(new LensConversionRecipe(CraftTweakerMC.getIngredient(input), InputHelper.toStack(output), energyUsed, ActuallyAdditionsAPI.lensDefaultConversion))));
   }

   @ZenMethod
   public static void removeRecipe(IItemStack output) {
      ModTweaker.LATE_ADDITIONS.add(new AtomicReconstructor.Remove(output));
   }

   private static class Add extends BaseListAddition<LensConversionRecipe> {
      protected Add(List<LensConversionRecipe> recipies) {
         super("Atomic Reconstructor", ActuallyAdditionsAPI.RECONSTRUCTOR_LENS_CONVERSION_RECIPES, recipies);
      }

      protected String getRecipeInfo(LensConversionRecipe recipe) {
         return LogHelper.getStackDescription(recipe.getOutput());
      }
   }

   private static class Remove extends BaseListRemoval<LensConversionRecipe> {
      private IItemStack output;

      protected Remove(IItemStack output) {
         super("Atomic Reconstructor", ActuallyAdditionsAPI.RECONSTRUCTOR_LENS_CONVERSION_RECIPES);
         this.output = output;
      }

      public void apply() {
         Stream var10000 = ActuallyAdditionsAPI.RECONSTRUCTOR_LENS_CONVERSION_RECIPES.stream().filter((recipe) -> {
            return this.output.matches(InputHelper.toIItemStack(recipe.getOutput()));
         });
         LinkedList var10001 = this.recipes;
         this.recipes.getClass();
         var10000.forEach(var10001::add);
         super.apply();
      }

      protected String getRecipeInfo(LensConversionRecipe recipe) {
         return LogHelper.getStackDescription(recipe.getOutput());
      }
   }
}
