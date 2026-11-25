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
import vazkii.botania.api.recipe.RecipeManaInfusion;

@ZenClass("mods.botania.ManaInfusion")
@ModOnly("botania")
@ZenRegister
public class ManaInfusion {
   protected static final String name = "Botania Mana Infusion";

   public ManaInfusion() {
   }

   @ZenMethod
   public static void addInfusion(IItemStack output, IIngredient input, int mana) {
      ModTweaker.LATE_ADDITIONS.add(new ManaInfusion.Add(new RecipeManaInfusion(InputHelper.toStack(output), InputHelper.toObject(input), mana)));
   }

   @ZenMethod
   public static void addAlchemy(IItemStack output, IIngredient input, int mana) {
      RecipeManaInfusion recipe = new RecipeManaInfusion(InputHelper.toStack(output), InputHelper.toObject(input), mana);
      recipe.setCatalyst(RecipeManaInfusion.alchemyState);
      ModTweaker.LATE_ADDITIONS.add(new ManaInfusion.Add(recipe));
   }

   @ZenMethod
   public static void addConjuration(IItemStack output, IIngredient input, int mana) {
      RecipeManaInfusion recipe = new RecipeManaInfusion(InputHelper.toStack(output), InputHelper.toObject(input), mana);
      recipe.setCatalyst(RecipeManaInfusion.conjurationState);
      ModTweaker.LATE_ADDITIONS.add(new ManaInfusion.Add(recipe));
   }

   @ZenMethod
   public static void removeRecipe(IIngredient output) {
      ModTweaker.LATE_REMOVALS.add(new ManaInfusion.Remove(output));
   }

   private static class Add extends BaseListAddition<RecipeManaInfusion> {
      public Add(RecipeManaInfusion recipe) {
         super("Botania Mana Infusion", BotaniaAPI.manaInfusionRecipes);
         this.recipes.add(recipe);
      }

      public String getRecipeInfo(RecipeManaInfusion recipe) {
         return LogHelper.getStackDescription(recipe.getOutput());
      }
   }

   private static class Remove extends BaseListRemoval<RecipeManaInfusion> {
      final IIngredient output;

      public Remove(IIngredient output) {
         super("Botania Mana Infusion", BotaniaAPI.manaInfusionRecipes, Collections.emptyList());
         this.output = output;
      }

      public String getRecipeInfo(RecipeManaInfusion recipe) {
         return LogHelper.getStackDescription(recipe.getOutput());
      }

      public void apply() {
         List<RecipeManaInfusion> recipes = new LinkedList();

         for(RecipeManaInfusion r : BotaniaAPI.manaInfusionRecipes) {
            if (r.getOutput() != null && StackHelper.matches(this.output, InputHelper.toIItemStack(r.getOutput()))) {
               recipes.add(r);
            }
         }

         if (!recipes.isEmpty()) {
            this.recipes.addAll(recipes);
            super.apply();
         } else {
            LogHelper.logWarning(String.format("No %s Recipe found for %s. Command ignored!", "Botania Mana Infusion", this.output.toString()));
         }

         CraftTweakerAPI.getLogger().logInfo(super.describe());
      }

      public String describe() {
         return "Attempting to remove mana infusion recipe for " + this.output.getItems();
      }
   }
}
