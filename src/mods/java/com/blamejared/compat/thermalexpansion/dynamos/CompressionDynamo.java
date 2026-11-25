package com.blamejared.compat.thermalexpansion.dynamos;

import cofh.thermalexpansion.util.managers.dynamo.CompressionManager;
import com.blamejared.ModTweaker;
import com.blamejared.mtlib.helpers.InputHelper;
import com.blamejared.mtlib.helpers.LogHelper;
import com.blamejared.mtlib.utils.BaseAction;
import crafttweaker.annotations.ModOnly;
import crafttweaker.annotations.ZenRegister;
import crafttweaker.api.liquid.ILiquidStack;
import net.minecraftforge.fluids.FluidStack;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenMethod;

@ZenClass("mods.thermalexpansion.CompressionDynamo")
@ModOnly("thermalexpansion")
@ZenRegister
public class CompressionDynamo {
   public CompressionDynamo() {
   }

   @ZenMethod
   public static void addFuel(ILiquidStack stack, int energy) {
      ModTweaker.LATE_ADDITIONS.add(new CompressionDynamo.Add(InputHelper.toFluid(stack), energy));
   }

   @ZenMethod
   public static void removeFuel(ILiquidStack stack) {
      ModTweaker.LATE_REMOVALS.add(new CompressionDynamo.Remove(InputHelper.toFluid(stack)));
   }

   private static class Add extends BaseAction {
      private FluidStack stack;
      private int energy;

      protected Add(FluidStack stack, int energy) {
         super("CompressionDynamo");
         this.stack = stack;
         this.energy = energy;
      }

      public void apply() {
         CompressionManager.addFuel(this.stack.getFluid().getName(), this.energy);
      }

      protected String getRecipeInfo() {
         return LogHelper.getStackDescription(this.stack);
      }
   }

   private static class Remove extends BaseAction {
      private FluidStack stack;

      protected Remove(FluidStack stack) {
         super("CompressionDynamo");
         this.stack = stack;
      }

      public void apply() {
         CompressionManager.removeFuel(this.stack.getFluid().getName());
      }

      protected String getRecipeInfo() {
         return LogHelper.getStackDescription(this.stack);
      }
   }
}
