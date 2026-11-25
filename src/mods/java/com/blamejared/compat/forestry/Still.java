package com.blamejared.compat.forestry;

import com.blamejared.ModTweaker;
import com.blamejared.mtlib.helpers.InputHelper;
import com.blamejared.mtlib.helpers.LogHelper;
import com.blamejared.mtlib.helpers.StackHelper;
import com.blamejared.mtlib.utils.BaseAddForestry;
import com.blamejared.mtlib.utils.BaseRemoveForestry;
import crafttweaker.annotations.ModOnly;
import crafttweaker.annotations.ZenRegister;
import crafttweaker.api.liquid.ILiquidStack;
import forestry.api.recipes.IStillRecipe;
import forestry.api.recipes.RecipeManagers;
import forestry.factory.recipes.StillRecipe;
import stanhebben.zenscript.annotations.Optional;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenMethod;

@ZenClass("mods.forestry.Still")
@ModOnly("forestry")
@ZenRegister
public class Still {
   public static final String name = "Forestry Still";

   public Still() {
   }

   @ZenMethod
   public static void addRecipe(ILiquidStack fluidOutput, ILiquidStack fluidInput, int timePerUnit) {
      ModTweaker.LATE_ADDITIONS.add(new Still.Add(new StillRecipe(timePerUnit, InputHelper.toFluid(fluidInput), InputHelper.toFluid(fluidOutput))));
   }

   @ZenMethod
   public static void removeRecipe(ILiquidStack output, @Optional ILiquidStack fluidInput) {
      ModTweaker.LATE_REMOVALS.add(new Still.Remove(output, fluidInput));
   }

   private static class Add extends BaseAddForestry<IStillRecipe> {
      public Add(IStillRecipe recipe) {
         super("Forestry Still", RecipeManagers.stillManager, recipe);
      }

      public String getRecipeInfo() {
         return LogHelper.getStackDescription(((IStillRecipe)this.recipe).getOutput());
      }
   }

   private static class Remove extends BaseRemoveForestry<IStillRecipe> {
      private final ILiquidStack output;
      private final ILiquidStack fluidInput;

      public Remove(ILiquidStack output, ILiquidStack fluidInput) {
         super("Forestry Still", RecipeManagers.stillManager);
         this.output = output;
         this.fluidInput = fluidInput;
      }

      public String getRecipeInfo() {
         return this.fluidInput != null ? this.fluidInput.getDisplayName() + " --> " : "" + this.output.getDisplayName();
      }

      public boolean checkIsRecipe(IStillRecipe r) {
         if (r != null && StackHelper.matches(this.output, InputHelper.toILiquidStack(r.getOutput()))) {
            return this.fluidInput != null ? StackHelper.matches(this.fluidInput, InputHelper.toILiquidStack(r.getInput())) : true;
         } else {
            return false;
         }
      }
   }
}
