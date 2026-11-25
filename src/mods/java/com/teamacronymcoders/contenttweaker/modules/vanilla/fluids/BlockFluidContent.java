package com.teamacronymcoders.contenttweaker.modules.vanilla.fluids;

import com.teamacronymcoders.base.blocks.BlockFluidBase;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;

public class BlockFluidContent extends BlockFluidBase {
   private FluidRepresentation fluidRepresentation;

   public BlockFluidContent(FluidRepresentation fluidRepresentation) {
      super(fluidRepresentation.getUnlocalizedName(), getFluidContent(fluidRepresentation), fluidRepresentation.getMaterial().getInternal());
      this.fluidRepresentation = fluidRepresentation;
   }

   public ResourceLocation getResourceLocation(IBlockState blockState) {
      return new ResourceLocation("contenttweaker", this.fluidRepresentation.getUnlocalizedName());
   }

   private static Fluid getFluidContent(FluidRepresentation representation) {
      Fluid fluid = new FluidContent(representation);
      FluidRegistry.registerFluid(fluid);
      FluidRegistry.addBucketForFluid(fluid);
      return fluid;
   }
}
