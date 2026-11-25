package com.blamejared.compat.thermalexpansion;

import cofh.thermalexpansion.util.managers.machine.RefineryManager;
import com.blamejared.ModTweaker;
import com.blamejared.mtlib.helpers.InputHelper;
import com.blamejared.mtlib.helpers.LogHelper;
import com.blamejared.mtlib.utils.BaseAction;
import crafttweaker.CraftTweakerAPI;
import crafttweaker.annotations.ModOnly;
import crafttweaker.annotations.ZenRegister;
import crafttweaker.api.item.WeightedItemStack;
import crafttweaker.api.liquid.ILiquidStack;
import net.minecraftforge.fluids.FluidStack;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenMethod;

@ZenClass("mods.thermalexpansion.Refinery")
@ModOnly("thermalexpansion")
@ZenRegister
public class Refinery {
   public Refinery() {
   }

   @ZenMethod
   public static void addRecipe(ILiquidStack output, WeightedItemStack outputItem, ILiquidStack input, int energy) {
      ModTweaker.LATE_ADDITIONS.add(new Refinery.Add(InputHelper.toFluid(output), InputHelper.toFluid(input), outputItem, energy));
   }

   @ZenMethod
   public static void addRecipePotion(ILiquidStack output, ILiquidStack input, int energy) {
      ModTweaker.LATE_ADDITIONS.add(new Refinery.AddPotion(InputHelper.toFluid(output), InputHelper.toFluid(input), energy));
   }

   @ZenMethod
   public static void removeRecipe(ILiquidStack input) {
      ModTweaker.LATE_REMOVALS.add(new Refinery.Remove(InputHelper.toFluid(input), false));
   }

   @ZenMethod
   public static void removeRecipePotion(ILiquidStack input) {
      ModTweaker.LATE_REMOVALS.add(new Refinery.Remove(InputHelper.toFluid(input), true));
   }

   private static class Add extends BaseAction {
      private FluidStack output;
      private FluidStack input;
      private WeightedItemStack outputItem;
      private int energy;

      public Add(FluidStack output, FluidStack input, WeightedItemStack outputItem, int energy) {
         super("Refinery");
         this.output = output;
         this.input = input;
         this.outputItem = outputItem;
         this.energy = energy;
      }

      public void apply() {
         if (this.outputItem == null) {
            RefineryManager.addRecipe(this.energy, this.input, this.output);
         } else {
            RefineryManager.addRecipe(this.energy, this.input, this.output, InputHelper.toStack(this.outputItem.getStack()), (int)this.outputItem.getPercent());
         }

      }

      protected String getRecipeInfo() {
         return LogHelper.getStackDescription(this.output);
      }
   }

   private static class AddPotion extends BaseAction {
      private FluidStack output;
      private FluidStack input;
      private int energy;

      public AddPotion(FluidStack output, FluidStack input, int energy) {
         super("Refinery");
         this.output = output;
         this.input = input;
         this.energy = energy;
      }

      public void apply() {
         RefineryManager.addRecipePotion(this.energy, this.input, this.output);
      }

      protected String getRecipeInfo() {
         return LogHelper.getStackDescription(this.output);
      }
   }

   private static class Remove extends BaseAction {
      private FluidStack input;
      private boolean potion;

      public Remove(FluidStack input, boolean potion) {
         super("Refinery");
         this.potion = potion;
         this.input = input;
      }

      public void apply() {
         if (this.potion) {
            if (!RefineryManager.recipeExistsPotion(this.input)) {
               CraftTweakerAPI.logError("No Refinery potion recipe exists for: " + this.input);
               return;
            }

            RefineryManager.removeRecipePotion(this.input);
         } else {
            if (!RefineryManager.recipeExists(this.input)) {
               CraftTweakerAPI.logError("No Refinery recipe exists for: " + this.input);
               return;
            }

            RefineryManager.removeRecipe(this.input);
         }

      }

      protected String getRecipeInfo() {
         return LogHelper.getStackDescription(this.input);
      }
   }
}
