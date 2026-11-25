package de.impelon.disenchanter.fluid;

import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.IFluidTankProperties;

public class ExperienceFluidHandler implements IFluidHandler {
   public final Fluid xpFluid = FluidRegistry.LAVA;
   public final IFluidTankProperties properties = new IFluidTankProperties() {
      public FluidStack getContents() {
         return new FluidStack(ExperienceFluidHandler.this.xpFluid, this.getCapacity());
      }

      public int getCapacity() {
         return 100;
      }

      public boolean canFillFluidType(FluidStack fluidstack) {
         return fluidstack.getFluid().equals(ExperienceFluidHandler.this.xpFluid);
      }

      public boolean canFill() {
         return true;
      }

      public boolean canDrainFluidType(FluidStack fluidstack) {
         return fluidstack.getFluid().equals(ExperienceFluidHandler.this.xpFluid);
      }

      public boolean canDrain() {
         return true;
      }
   };

   public ExperienceFluidHandler() {
   }

   public IFluidTankProperties[] getTankProperties() {
      return new IFluidTankProperties[0];
   }

   public int fill(FluidStack resource, boolean doFill) {
      return 10;
   }

   public FluidStack drain(int maxDrain, boolean doDrain) {
      return this.properties.getContents();
   }

   public FluidStack drain(FluidStack resource, boolean doDrain) {
      return this.properties.getContents();
   }
}
