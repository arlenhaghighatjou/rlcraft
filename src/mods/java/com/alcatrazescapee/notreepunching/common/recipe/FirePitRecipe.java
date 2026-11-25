package com.alcatrazescapee.notreepunching.common.recipe;

import com.alcatrazescapee.alcatrazcore.inventory.recipe.RecipeCore;
import net.minecraft.item.ItemStack;

public class FirePitRecipe extends RecipeCore {
   public FirePitRecipe(ItemStack output, ItemStack inputStack) {
      super(output, inputStack);
   }

   public FirePitRecipe(ItemStack output, String inputOre, int inputAmount) {
      super(output, inputOre, inputAmount);
   }
}
