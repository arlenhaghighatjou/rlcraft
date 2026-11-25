package com.blamejared.compat.immersivetech;

import com.blamejared.ModTweaker;
import com.blamejared.mtlib.helpers.InputHelper;
import com.blamejared.mtlib.utils.BaseAction;
import crafttweaker.annotations.ModOnly;
import crafttweaker.annotations.ZenRegister;
import crafttweaker.api.liquid.ILiquidStack;
import ferro2000.immersivetech.api.energy.SteamHandler;
import net.minecraftforge.fluids.FluidStack;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenMethod;

@ZenClass("mods.immersivetech.SteamTurbine")
@ZenRegister
@ModOnly("immersivetech")
public class SteamTurbine {
   public SteamTurbine() {
   }

   @ZenMethod
   public static void registerSteam(ILiquidStack input, int burnTime) {
      ModTweaker.LATE_ADDITIONS.add(new SteamTurbine.Add(InputHelper.toFluid(input), burnTime));
   }

   @ZenMethod
   public static void removeSteam(ILiquidStack input) {
      ModTweaker.LATE_REMOVALS.add(new SteamTurbine.Remove(InputHelper.toFluid(input)));
   }

   private static class Add extends BaseAction {
      private FluidStack input;
      private int time;

      public Add(FluidStack input, int time) {
         super("SteamTurbine");
         this.input = input;
         this.time = time;
      }

      public void apply() {
         SteamHandler.registerSteam(this.input.getFluid(), this.time);
      }

      public String describe() {
         return "Registering Steam Turbine steam: " + this.input + " burn time: " + this.time;
      }
   }

   private static class Remove extends BaseAction {
      private FluidStack input;

      public Remove(FluidStack input) {
         super("SteamTurbine");
         this.input = input;
      }

      public void apply() {
         SteamHandler.getSteamValues().remove(this.input.getFluid().getName());
      }

      public String describe() {
         return "Removing Steam Turbine steam: " + this.input;
      }
   }
}
