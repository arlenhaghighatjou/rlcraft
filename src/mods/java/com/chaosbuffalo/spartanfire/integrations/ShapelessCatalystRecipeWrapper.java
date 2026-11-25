package com.chaosbuffalo.spartanfire.integrations;

import java.util.Arrays;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.ingredients.VanillaTypes;
import mezz.jei.api.recipe.wrapper.ICraftingRecipeWrapper;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class ShapelessCatalystRecipeWrapper implements ICraftingRecipeWrapper {
   private final ItemStack input;
   private final ItemStack output;
   private final ItemStack catalyst;

   public ShapelessCatalystRecipeWrapper(Item input, Item output, Item catalyst) {
      this.input = new ItemStack(input);
      this.output = new ItemStack(output);
      this.catalyst = new ItemStack(catalyst);
   }

   public void getIngredients(IIngredients ingredients) {
      ingredients.setInputs(VanillaTypes.ITEM, Arrays.asList(this.input, this.catalyst));
      ingredients.setOutput(VanillaTypes.ITEM, this.output);
   }
}
