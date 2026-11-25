package reborncore.api.praescriptum.Utils;

import java.util.Iterator;
import java.util.function.Consumer;
import java.util.function.Predicate;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import reborncore.api.praescriptum.fuels.Fuel;
import reborncore.api.praescriptum.fuels.FuelHandler;
import reborncore.api.praescriptum.ingredients.Ingredient;
import reborncore.api.praescriptum.ingredients.input.FluidStackInputIngredient;
import reborncore.api.praescriptum.ingredients.input.InputIngredient;
import reborncore.api.praescriptum.recipes.Recipe;
import reborncore.api.praescriptum.recipes.RecipeHandler;

public class IngredientUtils {
   public IngredientUtils() {
   }

   public static <T extends Ingredient<?>> Predicate<T> isIngredientEmpty() {
      return Ingredient::isEmpty;
   }

   public static <T extends Ingredient<?>> Predicate<T> isIngredientEmpty(Consumer<T> action) {
      return (ingredient) -> {
         boolean isEmpty = ingredient.isEmpty();
         if (isEmpty) {
            action.accept(ingredient);
         }

         return !isEmpty;
      };
   }

   public static <T> Predicate<T> isPartOfRecipe(RecipeHandler recipeHandler) {
      return (object) -> {
         label21:
         for(Recipe recipe : recipeHandler.getRecipes()) {
            Iterator var4 = recipe.getInputIngredients().iterator();

            while(true) {
               if (!var4.hasNext()) {
                  continue label21;
               }

               InputIngredient<?> ingredient = (InputIngredient)var4.next();
               if (ingredient.matches(object)) {
                  break;
               }
            }

            return true;
         }

         return false;
      };
   }

   public static <T> Predicate<T> isPartOfFuel(FuelHandler fuelHandler) {
      return (object) -> {
         for(Fuel fuel : fuelHandler.getFuels()) {
            Iterator var4 = fuel.getInputIngredients().iterator();
            if (var4.hasNext()) {
               InputIngredient<?> ingredient = (InputIngredient)var4.next();
               return ingredient.matches(object);
            }
         }

         return false;
      };
   }

   public static <T extends Fluid> Predicate<T> isFluidPartOfFuel(FuelHandler fuelHandler) {
      return (object) -> {
         label23:
         for(Fuel fuel : fuelHandler.getFuels()) {
            Iterator var4 = fuel.getInputIngredients().iterator();

            while(true) {
               if (!var4.hasNext()) {
                  continue label23;
               }

               InputIngredient<?> ingredient = (InputIngredient)var4.next();
               if (ingredient instanceof FluidStackInputIngredient && ((FluidStack)((FluidStackInputIngredient)ingredient).ingredient).getFluid() == object) {
                  break;
               }
            }

            return true;
         }

         return false;
      };
   }
}
