package com.alcatrazescapee.alcatrazcore.inventory.ingredient;

import java.util.List;
import java.util.function.Predicate;
import javax.annotation.Nonnull;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;

public interface IRecipeIngredient extends Predicate<Object> {
   static IRecipeIngredient of(ItemStack stack) {
      return new IngredientItemStack(stack);
   }

   static IRecipeIngredient of(String oreName) {
      return new IngredientOreDict(oreName);
   }

   static IRecipeIngredient of(String oreName, int amount) {
      return new IngredientOreDict(oreName, amount);
   }

   static IRecipeIngredient of(Fluid fluid, int amount) {
      return new IngredientFluidStack(fluid, amount);
   }

   static IRecipeIngredient of(FluidStack stack) {
      return new IngredientFluidStack(stack);
   }

   static IRecipeIngredient of(ItemStack stack, boolean useNBT) {
      return (IRecipeIngredient)(useNBT ? new IngredientItemStackNBT(stack) : of(stack));
   }

   @Nonnull
   String getName();

   @Nonnull
   List<ItemStack> getStacks();

   boolean test(Object var1);

   default boolean testIgnoreCount(Object input) {
      return this.test(input);
   }

   boolean matches(IRecipeIngredient var1);
}
