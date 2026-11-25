package com.charles445.simpledifficulty.fluid;

import com.charles445.simpledifficulty.api.SDFluids;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;

public class FluidBasic extends Fluid {
   public FluidBasic(String fluidName, String still, String flowing, int ARGB) {
      super(fluidName, new ResourceLocation("simpledifficulty", "fluids/" + still), new ResourceLocation("simpledifficulty", "fluids/" + flowing), ARGB);
      SDFluids.fluids.put(fluidName, this);
      FluidRegistry.registerFluid((Fluid)SDFluids.fluids.get(fluidName));
      FluidRegistry.addBucketForFluid((Fluid)SDFluids.fluids.get(fluidName));
   }
}
