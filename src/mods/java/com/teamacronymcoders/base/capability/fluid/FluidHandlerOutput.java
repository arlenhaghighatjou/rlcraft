package com.teamacronymcoders.base.capability.fluid;

import javax.annotation.Nullable;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTank;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.IFluidTankProperties;

public class FluidHandlerOutput implements IFluidHandler {
   public FluidTank fluidTank;

   public FluidHandlerOutput(FluidTank fluidTank) {
      this.fluidTank = fluidTank;
   }

   public IFluidTankProperties[] getTankProperties() {
      return this.fluidTank.getTankProperties();
   }

   public int fill(FluidStack resource, boolean doFill) {
      return 0;
   }

   @Nullable
   public FluidStack drain(FluidStack resource, boolean doDrain) {
      return this.fluidTank.drain(resource, doDrain);
   }

   @Nullable
   public FluidStack drain(int maxDrain, boolean doDrain) {
      return this.fluidTank.drain(maxDrain, doDrain);
   }
}
