package com.teamacronymcoders.base.util.inventory;

import javax.annotation.Nullable;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidUtil;

public class IngredientFluidStack extends Ingredient {
   private final FluidStack fluid;
   ItemStack[] cachedStacks;

   public IngredientFluidStack(FluidStack fluid) {
      super(0);
      this.fluid = fluid;
   }

   public IngredientFluidStack(Fluid fluid, int amount) {
      this(new FluidStack(fluid, amount));
   }

   public FluidStack getFluid() {
      return this.fluid;
   }

   public ItemStack[] getMatchingStacks() {
      if (this.cachedStacks == null) {
         this.cachedStacks = new ItemStack[]{FluidUtil.getFilledBucket(this.fluid)};
      }

      return this.cachedStacks;
   }

   public boolean apply(@Nullable ItemStack stack) {
      if (stack == null) {
         return false;
      } else {
         FluidStack fs = FluidUtil.getFluidContained(stack);
         return fs == null && this.fluid == null || fs != null && fs.containsFluid(this.fluid);
      }
   }
}
