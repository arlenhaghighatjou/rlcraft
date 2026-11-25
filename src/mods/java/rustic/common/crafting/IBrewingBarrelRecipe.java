package rustic.common.crafting;

import net.minecraftforge.fluids.FluidStack;

public interface IBrewingBarrelRecipe {
   FluidStack getInput();

   FluidStack getOuput();

   boolean matches(FluidStack var1);

   boolean matches(FluidStack var1, FluidStack var2);

   FluidStack getResult(FluidStack var1);

   FluidStack getResult(FluidStack var1, FluidStack var2);
}
