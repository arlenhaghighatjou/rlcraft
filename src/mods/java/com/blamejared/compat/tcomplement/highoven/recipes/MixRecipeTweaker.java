package com.blamejared.compat.tcomplement.highoven.recipes;

import javax.annotation.Nonnull;
import knightminer.tcomplement.library.steelworks.MixRecipe;
import net.minecraftforge.fluids.FluidStack;

public class MixRecipeTweaker extends MixRecipe {
   public MixRecipeTweaker(@Nonnull FluidStack input, @Nonnull FluidStack output, int temp) {
      super(input, output, temp);
   }

   public MixRecipeTweaker(@Nonnull FluidStack input, @Nonnull FluidStack output) {
      super(input, output);
   }
}
