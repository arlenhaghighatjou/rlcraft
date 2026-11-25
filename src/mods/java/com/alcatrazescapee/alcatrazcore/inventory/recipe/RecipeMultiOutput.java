package com.alcatrazescapee.alcatrazcore.inventory.recipe;

import com.alcatrazescapee.alcatrazcore.inventory.ingredient.IRecipeIngredient;
import com.alcatrazescapee.alcatrazcore.util.CoreHelpers;
import javax.annotation.ParametersAreNonnullByDefault;
import net.minecraft.item.ItemStack;

@ParametersAreNonnullByDefault
public abstract class RecipeMultiOutput implements IRecipeCore {
   protected final IRecipeIngredient ingredient;
   protected final ItemStack[] outputStacks;
   protected final int inputAmount;

   public RecipeMultiOutput(ItemStack inputStack, ItemStack... outputStacks) {
      this.outputStacks = outputStacks;
      this.inputAmount = inputStack.getCount();
      this.ingredient = IRecipeIngredient.of(inputStack);
   }

   public RecipeMultiOutput(String inputOre, int inputAmount, ItemStack... outputStacks) {
      this.inputAmount = inputAmount;
      this.outputStacks = outputStacks;
      this.ingredient = IRecipeIngredient.of(inputOre, inputAmount);
   }

   public boolean test(Object input) {
      return this.ingredient.test(input);
   }

   /** @deprecated */
   @Deprecated
   public boolean test(Object... inputs) {
      throw new UnsupportedOperationException("This recipe does not support access by multiple inputs");
   }

   public boolean matches(Object input) {
      return input instanceof IRecipeIngredient && this.ingredient.matches((IRecipeIngredient)input);
   }

   /** @deprecated */
   @Deprecated
   public boolean matches(Object... inputs) {
      throw new UnsupportedOperationException("This recipe does not support access by multiple inputs");
   }

   public ItemStack consumeInput(ItemStack stack) {
      return CoreHelpers.consumeItem(stack, this.inputAmount);
   }

   public ItemStack[] getOutput() {
      return this.outputStacks;
   }

   public IRecipeIngredient getInput() {
      return this.ingredient;
   }

   public String getName() {
      return this.ingredient.getName();
   }
}
