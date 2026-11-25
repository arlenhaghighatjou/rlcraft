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
import crafttweaker.mc1120.item.MCItemStack;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import net.minecraft.item.ItemStack;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenMethod;
import vazkii.botania.api.BotaniaAPI;
import vazkii.botania.api.recipe.RecipeElvenTrade;

@ZenClass("mods.botania.ElvenTrade")
@ModOnly("botania")
@ZenRegister
public class ElvenTrade {
   protected static final String name = "Botania Eleven Trade";

   public ElvenTrade() {
   }

   @ZenMethod
   public static void addRecipe(IItemStack[] outputs, IIngredient[] input) {
      ModTweaker.LATE_ADDITIONS.add(new ElvenTrade.Add(new RecipeElvenTrade(InputHelper.toStacks(outputs), InputHelper.toObjects(input))));
   }

   @ZenMethod
   public static void removeRecipe(IIngredient output) {
      ModTweaker.LATE_REMOVALS.add(new ElvenTrade.Remove(output));
   }

   public static IItemStack[] toStacks(ItemStack[] iIngredient) {
      ArrayList<IItemStack> stacks = new ArrayList();

      for(ItemStack stack : iIngredient) {
         stacks.add(new MCItemStack(stack));
      }

      return (IItemStack[])stacks.toArray(new IItemStack[stacks.size()]);
   }

   private static class Add extends BaseListAddition<RecipeElvenTrade> {
      public Add(RecipeElvenTrade recipe) {
         super("Botania Eleven Trade", BotaniaAPI.elvenTradeRecipes, Collections.singletonList(recipe));
      }

      public String getRecipeInfo(RecipeElvenTrade recipe) {
         return LogHelper.getStackDescription(recipe.getOutputs());
      }
   }

   private static class Remove extends BaseListRemoval<RecipeElvenTrade> {
      final IIngredient output;

      public Remove(IIngredient output) {
         super("Botania Eleven Trade", BotaniaAPI.elvenTradeRecipes);
         this.output = output;
      }

      public String getRecipeInfo(RecipeElvenTrade recipe) {
         return LogHelper.getStackDescription(recipe.getOutputs());
      }

      public void apply() {
         LinkedList<RecipeElvenTrade> recipes = new LinkedList();

         for(RecipeElvenTrade entry : BotaniaAPI.elvenTradeRecipes) {
            if (entry != null && entry.getOutputs() != null && StackHelper.matches(this.output, ElvenTrade.toStacks((ItemStack[])entry.getOutputs().toArray(new ItemStack[entry.getOutputs().size()])))) {
               recipes.add(entry);
            }
         }

         if (!recipes.isEmpty()) {
            this.recipes.addAll(recipes);
            super.apply();
         } else {
            LogHelper.logWarning(String.format("No %s Recipe found for %s. Command ignored!", "Botania Eleven Trade", this.output.toString()));
         }

         CraftTweakerAPI.getLogger().logInfo(super.describe());
      }

      public String describe() {
         return "Attempting to remove Elven Trade recipe for " + this.output.getItems();
      }
   }
}
