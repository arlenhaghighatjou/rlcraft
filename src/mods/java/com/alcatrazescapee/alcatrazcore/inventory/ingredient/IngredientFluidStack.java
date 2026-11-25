package com.alcatrazescapee.alcatrazcore.inventory.ingredient;

import com.alcatrazescapee.alcatrazcore.util.CoreHelpers;
import java.util.List;
import javax.annotation.Nonnull;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.oredict.OreDictionary;

public class IngredientFluidStack implements IRecipeIngredient {
   private final FluidStack stack;

   IngredientFluidStack(@Nonnull Fluid fluid, int amount) {
      this(new FluidStack(fluid, amount));
   }

   IngredientFluidStack(@Nonnull FluidStack stack) {
      this.stack = stack;
   }

   @Nonnull
   public String getName() {
      return this.stack.getUnlocalizedName();
   }

   @Nonnull
   public List<ItemStack> getStacks() {
      return OreDictionary.EMPTY_LIST;
   }

   public boolean test(Object obj) {
      return this.testIgnoreCount(obj) && ((FluidStack)obj).amount >= this.stack.amount;
   }

   public boolean testIgnoreCount(Object obj) {
      return obj instanceof FluidStack && CoreHelpers.doStacksMatch(this.stack, (FluidStack)obj);
   }

   public boolean matches(IRecipeIngredient other) {
      return other instanceof IngredientFluidStack && CoreHelpers.doStacksMatch(this.stack, ((IngredientFluidStack)other).stack);
   }
}
