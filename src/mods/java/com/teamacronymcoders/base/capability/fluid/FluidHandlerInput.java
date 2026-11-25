package com.teamacronymcoders.base.capability.fluid;

import javax.annotation.Nullable;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTank;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.IFluidTankProperties;

public class FluidHandlerInput implements IFluidHandler {
   private FluidTank fluidTank;

   public FluidHandlerInput(FluidTank fluidTank) {
      this.fluidTank = fluidTank;
   }

   public IFluidTankProperties[] getTankProperties() {
      return this.fluidTank.getTankProperties();
   }

   public int fill(FluidStack resource, boolean doFill) {
      return this.fluidTank.fill(resource, doFill);
   }

   @Nullable
   public FluidStack drain(FluidStack resource, boolean doDrain) {
      return null;
   }

   @Nullable
   public FluidStack drain(int maxDrain, boolean doDrain) {
      return null;
   }
}
