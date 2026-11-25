package com.blamejared.compat.botania.handlers;

import com.blamejared.ModTweaker;
import com.blamejared.mtlib.helpers.InputHelper;
import com.blamejared.mtlib.helpers.LogHelper;
import com.blamejared.mtlib.helpers.StackHelper;
import com.blamejared.mtlib.utils.BaseListAddition;
import com.blamejared.mtlib.utils.BaseListRemoval;
import crafttweaker.CraftTweakerAPI;
import crafttweaker.annotations.ModOnly;
import crafttweaker.annotations.ZenRegister;
import crafttweaker.api.item.IIngredient;
import crafttweaker.api.item.IItemStack;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenMethod;
import vazkii.botania.api.BotaniaAPI;
import vazkii.botania.api.recipe.RecipeRuneAltar;

@ZenClass("mods.botania.RuneAltar")
@ModOnly("botania")
@ZenRegister
public class RuneAltar {
   protected static final String name = "Botania Rune Altar";

   public RuneAltar() {
   }

   @ZenMethod
   public static void addRecipe(IItemStack output, IIngredient[] input, int mana) {
      ModTweaker.LATE_ADDITIONS.add(new RuneAltar.Add(new RecipeRuneAltar(InputHelper.toStack(output), mana, InputHelper.toObjects(input))));
   }

   @ZenMethod
   public static void removeRecipe(IIngredient output) {
      ModTweaker.LATE_REMOVALS.add(new RuneAltar.Remove(output));
   }

   private static class Add extends BaseListAddition<RecipeRuneAltar> {
      public Add(RecipeRuneAltar recipe) {
         super("Botania Rune Altar", BotaniaAPI.runeAltarRecipes, Collections.singletonList(recipe));
      }

      public String getRecipeInfo(RecipeRuneAltar recipe) {
         return LogHelper.getStackDescription(recipe.getOutput());
      }
   }

   private static class Remove extends BaseListRemoval<RecipeRuneAltar> {
      final IIngredient output;

      public Remove(IIngredient output) {
         super("Botania Rune Altar", BotaniaAPI.runeAltarRecipes);
         this.output = output;
      }

      public String getRecipeInfo(RecipeRuneAltar recipe) {
         return LogHelper.getStackDescription(recipe.getOutput());
      }

      public void apply() {
         List<RecipeRuneAltar> recipes = new LinkedList();

         for(RecipeRuneAltar r : BotaniaAPI.runeAltarRecipes) {
            if (r != null && r.getOutput() != null && StackHelper.matches(this.output, InputHelper.toIItemStack(r.getOutput()))) {
               recipes.add(r);
            }
         }

         if (!recipes.isEmpty()) {
            this.recipes.addAll(recipes);
            super.apply();
         } else {
            LogHelper.logWarning(String.format("No %s Recipe found for %s. Command ignored!", "Botania Rune Altar", this.output.toString()));
         }

         CraftTweakerAPI.getLogger().logInfo(super.describe());
      }

      public String describe() {
         return "Attempting to remove Rune Altar recipe for " + this.output.getItems();
      }
   }
}
