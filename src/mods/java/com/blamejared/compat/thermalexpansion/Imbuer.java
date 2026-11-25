package com.blamejared.compat.thermalexpansion;

import cofh.thermalexpansion.util.managers.machine.BrewerManager;
import com.blamejared.ModTweaker;
import com.blamejared.mtlib.helpers.InputHelper;
import com.blamejared.mtlib.helpers.LogHelper;
import com.blamejared.mtlib.utils.BaseAction;
import crafttweaker.CraftTweakerAPI;
import crafttweaker.annotations.ModOnly;
import crafttweaker.annotations.ZenRegister;
import crafttweaker.api.item.IItemStack;
import crafttweaker.api.liquid.ILiquidStack;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenMethod;

@ZenClass("mods.thermalexpansion.Imbuer")
@ModOnly("thermalexpansion")
@ZenRegister
public class Imbuer {
   public Imbuer() {
   }

   @ZenMethod
   public static void addRecipe(ILiquidStack output, IItemStack input, ILiquidStack inputFluid, int energy) {
      ModTweaker.LATE_ADDITIONS.add(new Imbuer.Add(InputHelper.toFluid(output), InputHelper.toStack(input), InputHelper.toFluid(inputFluid), energy));
   }

   @ZenMethod
   public static void removeRecipe(IItemStack input, ILiquidStack secondInput) {
      ModTweaker.LATE_REMOVALS.add(new Imbuer.Remove(InputHelper.toStack(input), InputHelper.toFluid(secondInput)));
   }

   private static class Add extends BaseAction {
      private FluidStack output;
      private ItemStack input;
      private FluidStack inputFluid;
      private int energy;

      public Add(FluidStack output, ItemStack input, FluidStack inputFluid, int energy) {
         super("Imbuer");
         this.output = output;
         this.input = input;
         this.inputFluid = inputFluid;
         this.energy = energy;
      }

      public void apply() {
         BrewerManager.addRecipe(this.energy, this.input, this.inputFluid, this.output);
      }

      protected String getRecipeInfo() {
         return LogHelper.getStackDescription(this.input) + " and " + LogHelper.getStackDescription(this.inputFluid);
      }
   }

   private static class Remove extends BaseAction {
      private ItemStack input;
      private FluidStack fluid;

      public Remove(ItemStack input, FluidStack fluid) {
         super("Imbuer");
         this.input = input;
         this.fluid = fluid;
      }

      public void apply() {
         if (!BrewerManager.recipeExists(this.input, this.fluid)) {
            CraftTweakerAPI.logError("No Imbuer recipe exists for: " + this.input + " and " + this.fluid);
         } else {
            BrewerManager.removeRecipe(this.input, this.fluid);
         }
      }

      protected String getRecipeInfo() {
         return LogHelper.getStackDescription(this.input) + " and " + LogHelper.getStackDescription(this.fluid);
      }
   }
}
