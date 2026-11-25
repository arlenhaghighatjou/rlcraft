package com.dhanantry.scapeandrunparasites.fluid;

import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.Fluid;

public class DeadBloodFluid extends Fluid {
   public DeadBloodFluid(String fluidName, String still, String flowing) {
      super(fluidName, new ResourceLocation(still), new ResourceLocation(flowing));
      this.setUnlocalizedName(fluidName);
      this.viscosity = 3000;
   }
}
