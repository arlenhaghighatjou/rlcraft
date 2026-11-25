package com.blamejared.compat.tconstruct;

import com.blamejared.ModTweaker;
import com.blamejared.mtlib.helpers.InputHelper;
import com.blamejared.mtlib.helpers.LogHelper;
import com.blamejared.mtlib.utils.BaseAction;
import crafttweaker.annotations.ModOnly;
import crafttweaker.annotations.ZenRegister;
import crafttweaker.api.liquid.ILiquidStack;
import net.minecraftforge.fluids.FluidStack;
import slimeknights.tconstruct.library.TinkerRegistry;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenMethod;

@ZenClass("mods.tconstruct.Fuel")
@ZenRegister
@ModOnly("tconstruct")
public class Fuel {
   public Fuel() {
   }

   @ZenMethod
   public static void registerFuel(ILiquidStack fluid, int duration) {
      ModTweaker.LATE_ADDITIONS.add(new Fuel.Add(InputHelper.toFluid(fluid), duration));
   }

   private static class Add extends BaseAction {
      private FluidStack fuel;
      private int duration;

      public Add(FluidStack fuel, int temp) {
         super("Fuel");
         this.fuel = fuel;
         this.duration = temp;
      }

      public void apply() {
         TinkerRegistry.registerSmelteryFuel(this.fuel, this.duration);
      }

      protected String getRecipeInfo() {
         return LogHelper.getStackDescription(this.fuel);
      }
   }
}
