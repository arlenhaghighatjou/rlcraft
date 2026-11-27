package mezz.jei.plugins.vanilla.crafting;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import mezz.jei.Internal;
import mezz.jei.api.IJeiHelpers;
import mezz.jei.api.recipe.IRecipeWrapper;
import mezz.jei.api.recipe.IRecipeWrapperFactory;
import mezz.jei.startup.StackHelper;
import mezz.jei.util.ErrorUtil;
import mezz.jei.util.Log;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.item.crafting.ShapedRecipes;
import net.minecraft.item.crafting.ShapelessRecipes;
import net.minecraftforge.oredict.OreIngredient;
import net.minecraftforge.oredict.ShapedOreRecipe;
import net.minecraftforge.oredict.ShapelessOreRecipe;

public final class CraftingRecipeChecker {
   private CraftingRecipeChecker() {
   }

   public static List<IRecipe> getValidRecipes(IJeiHelpers jeiHelpers) {
      CraftingRecipeChecker.CraftingRecipeValidator<ShapedOreRecipe> shapedOreRecipeValidator = new CraftingRecipeChecker.CraftingRecipeValidator<ShapedOreRecipe>((recipe) -> {
         return new ShapedOreRecipeWrapper(jeiHelpers, recipe);
      });
      CraftingRecipeChecker.CraftingRecipeValidator<ShapedRecipes> shapedRecipesValidator = new CraftingRecipeChecker.CraftingRecipeValidator<ShapedRecipes>((recipe) -> {
         return new ShapedRecipesWrapper(jeiHelpers, recipe);
      });
      CraftingRecipeChecker.CraftingRecipeValidator<ShapelessOreRecipe> shapelessOreRecipeValidator = new CraftingRecipeChecker.CraftingRecipeValidator<ShapelessOreRecipe>((recipe) -> {
         return new ShapelessRecipeWrapper<IRecipe>(jeiHelpers, recipe);
      });
      CraftingRecipeChecker.CraftingRecipeValidator<ShapelessRecipes> shapelessRecipesValidator = new CraftingRecipeChecker.CraftingRecipeValidator<ShapelessRecipes>((recipe) -> {
         return new ShapelessRecipeWrapper<IRecipe>(jeiHelpers, recipe);
      });
      StackHelper stackHelper = Internal.getStackHelper();
      Iterator<IRecipe> recipeIterator = CraftingManager.REGISTRY.iterator();
      List<IRecipe> validRecipes = new ArrayList();

      while(recipeIterator.hasNext()) {
         IRecipe recipe = (IRecipe)recipeIterator.next();
         if (recipe instanceof ShapedOreRecipe) {
            if (shapedOreRecipeValidator.isRecipeValid((ShapedOreRecipe)recipe, stackHelper)) {
               validRecipes.add(recipe);
            }
         } else if (recipe instanceof ShapedRecipes) {
            if (shapedRecipesValidator.isRecipeValid((ShapedRecipes)recipe, stackHelper)) {
               validRecipes.add(recipe);
            }
         } else if (recipe instanceof ShapelessOreRecipe) {
            if (shapelessOreRecipeValidator.isRecipeValid((ShapelessOreRecipe)recipe, stackHelper)) {
               validRecipes.add(recipe);
            }
         } else if (recipe instanceof ShapelessRecipes) {
            if (shapelessRecipesValidator.isRecipeValid((ShapelessRecipes)recipe, stackHelper)) {
               validRecipes.add(recipe);
            }
         } else {
            validRecipes.add(recipe);
         }
      }

      return validRecipes;
   }

   private static final class CraftingRecipeValidator<T extends IRecipe> {
      private static final int INVALID_COUNT = -1;
      private static final int CANT_DISPLAY = -2;
      private final IRecipeWrapperFactory<T> recipeWrapperFactory;

      public CraftingRecipeValidator(IRecipeWrapperFactory<T> recipeWrapperFactory) {
         this.recipeWrapperFactory = recipeWrapperFactory;
      }

      public boolean isRecipeValid(T recipe, StackHelper stackHelper) {
         ItemStack recipeOutput = recipe.getRecipeOutput();
         if (recipeOutput != null && !recipeOutput.isEmpty()) {
            List<Ingredient> ingredients = recipe.getIngredients();
            if (ingredients == null) {
               String recipeInfo = this.getInfo(recipe);
               Log.get().error("Recipe has no input Ingredients. {}", recipeInfo);
               return false;
            } else {
               int inputCount = getInputCount(ingredients, stackHelper);
               if (inputCount == -2) {
                  String recipeInfo = this.getInfo(recipe);
                  Log.get().warn("Recipe contains ingredients that can't be understood or displayed by JEI: {}", recipeInfo);
                  return false;
               } else if (inputCount == -1) {
                  return false;
               } else if (inputCount > 9) {
                  String recipeInfo = this.getInfo(recipe);
                  Log.get().error("Recipe has too many inputs. {}", recipeInfo);
                  return false;
               } else if (inputCount == 0) {
                  String recipeInfo = this.getInfo(recipe);
                  Log.get().error("Recipe has no inputs. {}", recipeInfo);
                  return false;
               } else {
                  return true;
               }
            }
         } else {
            if (!recipe.isDynamic()) {
               String recipeInfo = this.getInfo(recipe);
               Log.get().error("Recipe has no output. {}", recipeInfo);
            }

            return false;
         }
      }

      private String getInfo(T recipe) {
         IRecipeWrapper recipeWrapper = this.recipeWrapperFactory.getRecipeWrapper(recipe);
         return ErrorUtil.getInfoFromRecipe(recipe, recipeWrapper);
      }

      protected static int getInputCount(List<Ingredient> ingredientList, StackHelper stackHelper) {
         int inputCount = 0;

         for(Ingredient ingredient : ingredientList) {
            List<ItemStack> input = stackHelper.getMatchingStacks(ingredient);
            if (input == null) {
               return -1;
            }

            if (ingredient instanceof OreIngredient && input.isEmpty()) {
               return -1;
            }

            if (!ingredient.isSimple() && input.isEmpty()) {
               return -2;
            }

            ++inputCount;
         }

         return inputCount;
      }
   }
}
