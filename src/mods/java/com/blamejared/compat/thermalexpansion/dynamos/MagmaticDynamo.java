package com.blamejared.compat.thermalexpansion.dynamos;

import cofh.thermalexpansion.util.managers.dynamo.MagmaticManager;
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

@ZenClass("mods.thermalexpansion.MagmaticDynamo")
@ModOnly("thermalexpansion")
@ZenRegister
public class MagmaticDynamo {
   public MagmaticDynamo() {
   }

   @ZenMethod
   public static void addFuel(ILiquidStack stack, int energy) {
      ModTweaker.LATE_ADDITIONS.add(new MagmaticDynamo.Add(InputHelper.toFluid(stack), energy));
   }

   @ZenMethod
   public static void removeFuel(ILiquidStack stack) {
      ModTweaker.LATE_REMOVALS.add(new MagmaticDynamo.Remove(InputHelper.toFluid(stack)));
   }

   private static class Add extends BaseAction {
      private FluidStack stack;
      private int energy;

      protected Add(FluidStack stack, int energy) {
         super("MagmaticDynamo");
         this.stack = stack;
         this.energy = energy;
      }

      public void apply() {
         MagmaticManager.addFuel(this.stack.getFluid().getName(), this.energy);
      }

      protected String getRecipeInfo() {
         return LogHelper.getStackDescription(this.stack);
      }
   }

   private static class Remove extends BaseAction {
      private FluidStack stack;

      protected Remove(FluidStack stack) {
         super("MagmaticDynamo");
         this.stack = stack;
      }

      public void apply() {
         MagmaticManager.removeFuel(this.stack.getFluid().getName());
      }

      protected String getRecipeInfo() {
         return LogHelper.getStackDescription(this.stack);
      }
   }
}
