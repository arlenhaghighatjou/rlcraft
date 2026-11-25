package com.blamejared.compat.actuallyaddition;

import com.blamejared.ModTweaker;
import com.blamejared.mtlib.helpers.InputHelper;
import com.blamejared.mtlib.helpers.LogHelper;
import com.blamejared.mtlib.utils.BaseListAddition;
import com.blamejared.mtlib.utils.BaseListRemoval;
import crafttweaker.CraftTweakerAPI;
import crafttweaker.annotations.ModOnly;
import crafttweaker.annotations.ZenRegister;
import crafttweaker.api.item.IIngredient;
import crafttweaker.api.item.IItemStack;
import crafttweaker.api.minecraft.CraftTweakerMC;
import de.ellpeck.actuallyadditions.api.ActuallyAdditionsAPI;
import de.ellpeck.actuallyadditions.api.recipe.CompostRecipe;
import java.util.Collections;
import java.util.List;
import net.minecraft.block.Block;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenMethod;

@ZenClass("mods.actuallyadditions.Compost")
@ModOnly("actuallyadditions")
@ZenRegister
public class Compost {
   public Compost() {
   }

   @ZenMethod
   public static void addRecipe(IItemStack output, IItemStack outputDisplay, IIngredient input, IItemStack inputDisplay) {
      if (InputHelper.isABlock(outputDisplay) && InputHelper.isABlock(inputDisplay)) {
         ModTweaker.LATE_ADDITIONS.add(new Compost.Add(Collections.singletonList(new CompostRecipe(CraftTweakerMC.getIngredient(input), Block.getBlockFromItem(InputHelper.toStack(inputDisplay).getItem()).getStateFromMeta(inputDisplay.getMetadata()), InputHelper.toStack(output), Block.getBlockFromItem(InputHelper.toStack(outputDisplay).getItem()).getStateFromMeta(outputDisplay.getMetadata())))));
      } else {
         CraftTweakerAPI.logError("outputDisplay or InputDisplay is not a block!");
      }
   }

   @ZenMethod
   public static void removeRecipe(IItemStack output) {
      ModTweaker.LATE_REMOVALS.add(new Compost.Remove(output));
   }

   private static class Add extends BaseListAddition<CompostRecipe> {
      protected Add(List<CompostRecipe> recipes) {
         super("Compost", ActuallyAdditionsAPI.COMPOST_RECIPES, recipes);
      }

      public String getRecipeInfo(CompostRecipe recipe) {
         return LogHelper.getStackDescription(recipe.getOutput());
      }
   }

   private static class Remove extends BaseListRemoval<CompostRecipe> {
      private IItemStack output;

      protected Remove(IItemStack output) {
         super("Compost", ActuallyAdditionsAPI.COMPOST_RECIPES);
         this.output = output;
      }

      public void apply() {
         for(CompostRecipe recipe : ActuallyAdditionsAPI.COMPOST_RECIPES) {
            System.out.println(this.output);
            if (this.output.matches(InputHelper.toIItemStack(recipe.getOutput()))) {
               this.recipes.add(recipe);
            }
         }

         super.apply();
      }

      public String getRecipeInfo(CompostRecipe recipe) {
         return LogHelper.getStackDescription(recipe.getOutput());
      }
   }
}
