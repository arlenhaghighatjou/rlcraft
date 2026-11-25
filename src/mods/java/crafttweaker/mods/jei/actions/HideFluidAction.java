package crafttweaker.mods.jei.actions;

import crafttweaker.CraftTweakerAPI;
import crafttweaker.IAction;
import crafttweaker.api.liquid.ILiquidStack;
import crafttweaker.api.minecraft.CraftTweakerMC;
import crafttweaker.mods.jei.JEI;
import net.minecraftforge.fluids.FluidStack;

public class HideFluidAction implements IAction {
   private final ILiquidStack stack;

   public HideFluidAction(ILiquidStack stack) {
      this.stack = stack;
   }

   public void apply() {
      if (this.stack == null) {
         CraftTweakerAPI.logError("Cannot hide null liquids!");
      } else {
         FluidStack fluid = CraftTweakerMC.getLiquidStack(this.stack);
         JEI.HIDDEN_LIQUIDS.add(fluid);
      }
   }

   public String describe() {
      return "Hiding item in JEI: " + this.stack;
   }
}
