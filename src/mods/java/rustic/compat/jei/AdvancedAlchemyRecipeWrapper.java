package rustic.compat.jei;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.ingredients.VanillaTypes;
import mezz.jei.api.recipe.BlankRecipeWrapper;
import net.minecraft.item.ItemStack;
import rustic.common.crafting.ICondenserRecipe;

public class AdvancedAlchemyRecipeWrapper extends BlankRecipeWrapper {
   public ICondenserRecipe recipe;

   AdvancedAlchemyRecipeWrapper(ICondenserRecipe recipe) {
      this.recipe = recipe;
   }

   public void getIngredients(IIngredients ingredients) {
      List<List<ItemStack>> inputs = new ArrayList();
      inputs.add(this.recipe.getBottles());
      if (this.recipe.getModifiers() != null) {
         inputs.add(this.recipe.getModifiers());
      } else {
         inputs.add(Collections.singletonList(ItemStack.EMPTY));
      }

      inputs.addAll(this.recipe.getInputs());
      ingredients.setInputLists(VanillaTypes.ITEM, inputs);
      ingredients.setInput(VanillaTypes.FLUID, this.recipe.getFluid());
      ingredients.setOutput(VanillaTypes.ITEM, this.recipe.getResult());
   }
}
