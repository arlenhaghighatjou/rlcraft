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
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenMethod;
import vazkii.botania.api.BotaniaAPI;
import vazkii.botania.api.recipe.RecipePetals;
import vazkii.botania.common.item.block.ItemBlockSpecialFlower;

@ZenClass("mods.botania.Apothecary")
@ModOnly("botania")
@ZenRegister
public class Apothecary {
   protected static final String name = "Botania Petal";

   public Apothecary() {
   }

   @ZenMethod
   public static void addRecipe(IItemStack output, IIngredient[] input) {
      ModTweaker.LATE_ADDITIONS.add(new Apothecary.Add(new RecipePetals(InputHelper.toStack(output), InputHelper.toObjects(input))));
   }

   @ZenMethod
   public static void addRecipe(String output, IIngredient[] input) {
      addRecipe(InputHelper.toIItemStack(ItemBlockSpecialFlower.ofType(output)), input);
   }

   @ZenMethod
   public static void removeRecipe(IIngredient output) {
      ModTweaker.LATE_REMOVALS.add(new Apothecary.Remove(output));
   }

   @ZenMethod
   public static void removeRecipe(String output) {
      removeRecipe(InputHelper.toIItemStack(ItemBlockSpecialFlower.ofType(output)));
   }

   private static class Add extends BaseListAddition<RecipePetals> {
      public Add(RecipePetals recipe) {
         super("Botania Petal", BotaniaAPI.petalRecipes, Collections.singletonList(recipe));
      }

      public String getRecipeInfo(RecipePetals recipe) {
         return LogHelper.getStackDescription(recipe.getOutput());
      }
   }

   private static class Remove extends BaseListRemoval<RecipePetals> {
      final IIngredient output;

      public Remove(IIngredient output) {
         super("Botania Petal", BotaniaAPI.petalRecipes, Collections.emptyList());
         this.output = output;
      }

      public String getRecipeInfo(RecipePetals recipe) {
         return LogHelper.getStackDescription(recipe.getOutput());
      }

      public void apply() {
         LinkedList<RecipePetals> result = new LinkedList();

         for(RecipePetals entry : BotaniaAPI.petalRecipes) {
            if (entry != null && entry.getOutput() != null && StackHelper.matches(this.output, InputHelper.toIItemStack(entry.getOutput()))) {
               result.add(entry);
            }
         }

         if (!result.isEmpty()) {
            this.recipes.addAll(result);
            super.apply();
         } else {
            LogHelper.logWarning(String.format("No %s Recipe found for %s. Command ignored!", "Botania Petal", this.output.toString()));
         }

         CraftTweakerAPI.getLogger().logInfo(super.describe());
      }

      public String describe() {
         return "Attempting to remove apothecary recipe for " + this.output.getItems();
      }
   }
}
