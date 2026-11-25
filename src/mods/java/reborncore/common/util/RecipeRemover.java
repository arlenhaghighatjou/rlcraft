package reborncore.common.util;

import java.util.List;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.ShapedRecipes;
import net.minecraftforge.registries.GameData;
import net.minecraftforge.registries.RegistryManager;

public class RecipeRemover {
   public RecipeRemover() {
   }

   public static void removeShapedRecipes(List<ItemStack> removelist) {
      for(ItemStack stack : removelist) {
         removeShapedRecipe(stack);
      }

   }

   public static void removeAnyRecipe(ItemStack resultItem) {
      for(Object tmpObject : CraftingManager.REGISTRY) {
         if (tmpObject instanceof IRecipe) {
            IRecipe tmpRecipe = (IRecipe)tmpObject;
            ItemStack recipeResult = tmpRecipe.getRecipeOutput();
            if (ItemStack.areItemStacksEqual(resultItem, recipeResult)) {
               removeRecipe(tmpRecipe);
            }
         }
      }

   }

   public static void removeShapedRecipe(ItemStack resultItem) {
      for(Object tmpObject : CraftingManager.REGISTRY) {
         if (tmpObject instanceof ShapedRecipes) {
            ShapedRecipes recipe = (ShapedRecipes)tmpObject;
            ItemStack recipeResult = recipe.getRecipeOutput();
            if (ItemStack.areItemStacksEqual(resultItem, recipeResult)) {
               removeRecipe(recipe);
            }
         }
      }

   }

   public static void removeRecipe(IRecipe recipe) {
      RegistryManager.ACTIVE.getRegistry(GameData.RECIPES).remove(recipe.getRegistryName());
   }
}
