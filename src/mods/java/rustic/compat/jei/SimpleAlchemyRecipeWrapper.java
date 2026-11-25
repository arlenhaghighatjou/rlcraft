package rustic.compat.jei;

import java.util.ArrayList;
import java.util.List;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.ingredients.VanillaTypes;
import mezz.jei.api.recipe.IRecipeWrapper;
import net.minecraft.item.ItemStack;
import rustic.common.crafting.ICondenserRecipe;

public class SimpleAlchemyRecipeWrapper implements IRecipeWrapper {
   public ICondenserRecipe recipe = null;

   public SimpleAlchemyRecipeWrapper(ICondenserRecipe recipe) {
      this.recipe = recipe;
   }

   public void getIngredients(IIngredients ingredients) {
      List<List<ItemStack>> inputs = new ArrayList();
      inputs.add(this.recipe.getBottles());
      inputs.addAll(this.recipe.getInputs());
      ingredients.setInputLists(VanillaTypes.ITEM, inputs);
      ingredients.setInput(VanillaTypes.FLUID, this.recipe.getFluid());
      ingredients.setOutput(VanillaTypes.ITEM, this.recipe.getResult());
   }
}
