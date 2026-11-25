package com.charles445.simpledifficulty.register.crafting;

import javax.annotation.Nonnull;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.brewing.AbstractBrewingRecipe;
import net.minecraftforge.oredict.OreDictionary;

public class FixedBrewingOreRecipe extends AbstractBrewingRecipe<ItemStack> {
   public FixedBrewingOreRecipe(ItemStack input, ItemStack ingredient, ItemStack output) {
      super(input, ingredient, output);
   }

   public boolean isInput(@Nonnull ItemStack stack) {
      return super.isInput(stack) && ItemStack.areItemStackTagsEqual(this.getInput(), stack);
   }

   public boolean isIngredient(ItemStack ingredient) {
      return OreDictionary.itemMatches((ItemStack)this.getIngredient(), ingredient, false);
   }
}
