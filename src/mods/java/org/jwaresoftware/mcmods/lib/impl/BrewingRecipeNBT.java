package org.jwaresoftware.mcmods.lib.impl;

import javax.annotation.Nonnull;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.brewing.BrewingRecipe;

public class BrewingRecipeNBT extends BrewingRecipe {
   protected final ItemStack _input;

   public BrewingRecipeNBT(@Nonnull ItemStack input, @Nonnull ItemStack ingredient, @Nonnull ItemStack output) {
      super(input, ingredient, output);
      this._input = input;
   }

   public boolean isInput(@Nonnull ItemStack stack) {
      return super.isInput(stack) && ItemStack.areItemStackTagsEqual(this._input, stack);
   }
}
