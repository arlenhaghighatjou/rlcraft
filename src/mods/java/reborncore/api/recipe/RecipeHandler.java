package reborncore.api.recipe;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.item.ItemStack;
import org.apache.commons.lang3.Validate;

public class RecipeHandler {
   public static final ArrayList<IBaseRecipeType> recipeList = new ArrayList();
   public static ArrayList<String> machineNames = new ArrayList();

   public RecipeHandler() {
   }

   public static List<IBaseRecipeType> getRecipeClassFromName(String name) {
      List<IBaseRecipeType> baseRecipeList = new ArrayList();

      for(IBaseRecipeType baseRecipe : recipeList) {
         if (baseRecipe.getRecipeName().equals(name)) {
            baseRecipeList.add(baseRecipe);
         }
      }

      return baseRecipeList;
   }

   public static String getUserFreindlyName(String name) {
      for(IBaseRecipeType baseRecipe : recipeList) {
         if (baseRecipe.getRecipeName().equals(name)) {
            return baseRecipe.getUserFreindlyName();
         }
      }

      return "";
   }

   public static void addRecipe(IBaseRecipeType recipe) {
      if (recipe != null) {
         if (!recipeList.contains(recipe)) {
            for(Object input : recipe.getInputs()) {
               Validate.notNull(input);
               if (input instanceof ItemStack) {
                  Validate.notNull(((ItemStack)input).getItem());
               }
            }

            for(ItemStack output : recipe.getOutputs()) {
               Validate.notNull(output);
               Validate.notNull(output.getItem());
            }

            if (!machineNames.contains(recipe.getRecipeName())) {
               machineNames.add(recipe.getRecipeName());
            }

            recipeList.add(recipe);
         }
      }
   }
}
