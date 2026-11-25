package com.alcatrazescapee.alcatrazcore.inventory.recipe;

import com.alcatrazescapee.alcatrazcore.inventory.ingredient.IRecipeIngredient;
import com.alcatrazescapee.alcatrazcore.util.CoreHelpers;
import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import net.minecraft.item.ItemStack;

@ParametersAreNonnullByDefault
public abstract class RecipeCore implements IRecipeCore {
   protected final IRecipeIngredient ingredient;
   protected final ItemStack outputStack;
   protected final int inputAmount;

   public RecipeCore(ItemStack outputStack, ItemStack inputStack) {
      this.outputStack = outputStack;
      this.inputAmount = inputStack.isEmpty() ? 0 : inputStack.getCount();
      this.ingredient = IRecipeIngredient.of(inputStack);
   }

   public RecipeCore(ItemStack outputStack, String inputOre, int inputAmount) {
      this.outputStack = outputStack;
      this.inputAmount = inputAmount;
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

   @Nonnull
   public ItemStack getOutput() {
      return this.outputStack.copy();
   }

   public IRecipeIngredient getInput() {
      return this.ingredient;
   }

   public String getName() {
      return this.ingredient.getName();
   }
}
