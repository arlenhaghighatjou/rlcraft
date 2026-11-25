package mezz.jei.plugins.vanilla.crafting;

import java.util.List;
import javax.annotation.Nullable;
import mezz.jei.api.IJeiHelpers;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.ingredients.VanillaTypes;
import mezz.jei.api.recipe.IStackHelper;
import mezz.jei.api.recipe.wrapper.ICraftingRecipeWrapper;
import mezz.jei.recipes.BrokenCraftingRecipeException;
import mezz.jei.util.ErrorUtil;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.util.ResourceLocation;

public class ShapelessRecipeWrapper<T extends IRecipe> implements ICraftingRecipeWrapper {
   private final IJeiHelpers jeiHelpers;
   protected final T recipe;

   public ShapelessRecipeWrapper(IJeiHelpers jeiHelpers, T recipe) {
      this.jeiHelpers = jeiHelpers;
      this.recipe = recipe;
   }

   public void getIngredients(IIngredients ingredients) {
      ItemStack recipeOutput = this.recipe.getRecipeOutput();
      IStackHelper stackHelper = this.jeiHelpers.getStackHelper();

      try {
         List<List<ItemStack>> inputLists = stackHelper.expandRecipeItemStackInputs(this.recipe.getIngredients());
         ingredients.setInputLists(VanillaTypes.ITEM, inputLists);
         ingredients.setOutput(VanillaTypes.ITEM, recipeOutput);
      } catch (RuntimeException var6) {
         String info = ErrorUtil.getInfoFromBrokenCraftingRecipe(this.recipe, this.recipe.getIngredients(), recipeOutput);
         throw new BrokenCraftingRecipeException(info, var6);
      }
   }

   @Nullable
   public ResourceLocation getRegistryName() {
      return this.recipe.getRegistryName();
   }
}
