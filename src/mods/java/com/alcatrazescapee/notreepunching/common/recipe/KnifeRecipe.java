package com.alcatrazescapee.notreepunching.common.recipe;

import com.alcatrazescapee.alcatrazcore.inventory.recipe.RecipeMultiOutput;
import net.minecraft.item.ItemStack;

public class KnifeRecipe extends RecipeMultiOutput {
   public KnifeRecipe(ItemStack inputStack, ItemStack... outputStacks) {
      super(inputStack, outputStacks);
   }

   public KnifeRecipe(String inputOre, int inputAmount, ItemStack... outputStacks) {
      super(inputOre, inputAmount, outputStacks);
   }
}
