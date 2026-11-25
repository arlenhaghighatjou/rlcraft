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
import crafttweaker.api.liquid.ILiquidStack;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenMethod;
import vazkii.botania.api.BotaniaAPI;
import vazkii.botania.api.recipe.RecipePureDaisy;

@ZenClass("mods.botania.PureDaisy")
@ModOnly("botania")
@ZenRegister
public class PureDaisy {
   public static final String name = "Botania PureDaisy";

   public PureDaisy() {
   }

   @ZenMethod
   public static void addRecipe(IIngredient blockInput, IItemStack blockOutput) {
      addRecipe(blockInput, blockOutput, 150);
   }

   @ZenMethod
   public static void addRecipe(IIngredient blockInput, IItemStack blockOutput, int time) {
      if (blockInput != null && blockOutput != null) {
         Object input = InputHelper.toObject(blockInput);
         if (blockInput instanceof ILiquidStack || input != null && (!(input instanceof ItemStack) || InputHelper.isABlock((ItemStack)input))) {
            if (input instanceof ItemStack) {
               input = Block.getBlockFromItem(((ItemStack)input).getItem()).getStateFromMeta(((ItemStack)input).getMetadata());
            }

            if (blockInput instanceof ILiquidStack) {
               input = InputHelper.toFluid((ILiquidStack)blockInput).getFluid().getBlock().getDefaultState();
            }

            ItemStack output = InputHelper.toStack(blockOutput);
            RecipePureDaisy recipe = new RecipePureDaisy(input, Block.getBlockFromItem(output.getItem()).getStateFromMeta(output.getMetadata()), time);
            ModTweaker.LATE_ADDITIONS.add(new PureDaisy.Add(recipe));
         } else {
            LogHelper.logError("Input must be a block or an oredict entry.");
         }
      } else {
         LogHelper.logError(String.format("Required parameters missing for %s Recipe.", "Botania PureDaisy"));
      }
   }

   @ZenMethod
   public static void removeRecipe(IIngredient output) {
      ModTweaker.LATE_REMOVALS.add(new PureDaisy.Remove(output));
   }

   private static class Add extends BaseListAddition<RecipePureDaisy> {
      public Add(RecipePureDaisy recipe) {
         super("Botania PureDaisy", BotaniaAPI.pureDaisyRecipes, Collections.singletonList(recipe));
      }

      protected String getRecipeInfo(RecipePureDaisy recipe) {
         return LogHelper.getStackDescription(new ItemStack(recipe.getOutputState().getBlock(), 1));
      }
   }

   private static class Remove extends BaseListRemoval<RecipePureDaisy> {
      final IIngredient output;

      public Remove(IIngredient output) {
         super("Botania PureDaisy", BotaniaAPI.pureDaisyRecipes, Collections.emptyList());
         this.output = output;
      }

      protected String getRecipeInfo(RecipePureDaisy recipe) {
         return LogHelper.getStackDescription(new ItemStack(recipe.getOutputState().getBlock(), 1));
      }

      public void apply() {
         List<RecipePureDaisy> recipes = new LinkedList();

         for(RecipePureDaisy recipe : BotaniaAPI.pureDaisyRecipes) {
            IItemStack out = InputHelper.toIItemStack(new ItemStack(recipe.getOutputState().getBlock(), 1));
            if (StackHelper.matches(this.output, out)) {
               recipes.add(recipe);
            }
         }

         if (!recipes.isEmpty()) {
            this.recipes.addAll(recipes);
            super.apply();
         } else {
            LogHelper.logWarning(String.format("No %s Recipe found for %s. Command ignored!", "Botania PureDaisy", this.output.toString()));
         }

         CraftTweakerAPI.getLogger().logInfo(super.describe());
      }

      public String describe() {
         return "Attempting to remove Pure Daisy recipe for " + this.output.getItems();
      }
   }
}
