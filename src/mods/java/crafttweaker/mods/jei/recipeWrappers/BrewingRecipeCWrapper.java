package crafttweaker.mods.jei.recipeWrappers;

import crafttweaker.mc1120.brewing.MultiBrewingRecipe;
import crafttweaker.mods.jei.JEIAddonPlugin;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.ingredients.VanillaTypes;
import mezz.jei.api.recipe.IRecipeWrapper;
import mezz.jei.plugins.vanilla.brewing.BrewingRecipeWrapper;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.brewing.BrewingRecipeRegistry;

public class BrewingRecipeCWrapper extends BrewingRecipeWrapper {
   public final ItemStack output;
   public final List<List<ItemStack>> ingredientList;
   public final List<ItemStack> inputs;

   public BrewingRecipeCWrapper(MultiBrewingRecipe recipe) {
      super(recipe.getIngredients(), (ItemStack)recipe.getInputs().get(0), recipe.getOutput());
      this.inputs = recipe.getInputs();
      this.ingredientList = Arrays.asList(recipe.getInputs(), recipe.getInputs(), recipe.getInputs(), recipe.getIngredients());
      this.output = recipe.getOutput();
   }

   public void getIngredients(IIngredients ingredients) {
      ingredients.setInputLists(VanillaTypes.ITEM, this.ingredientList);
      ingredients.setOutput(VanillaTypes.ITEM, this.output);
   }

   public List<?> getInputs() {
      return this.inputs;
   }

   public static List<IRecipeWrapper> createBrewingRecipes() {
      Stream var10000 = BrewingRecipeRegistry.getRecipes().stream();
      MultiBrewingRecipe.class.getClass();
      var10000 = var10000.filter(MultiBrewingRecipe.class::isInstance);
      MultiBrewingRecipe.class.getClass();
      return (List)var10000.map(MultiBrewingRecipe.class::cast).filter(MultiBrewingRecipe::isVisible).map(BrewingRecipeCWrapper::new).collect(Collectors.toList());
   }

   public static void registerBrewingRecipe() {
      List<IRecipeWrapper> recipes = createBrewingRecipes();
      if (!recipes.isEmpty()) {
         JEIAddonPlugin.modRegistry.addRecipes(recipes, "minecraft.brewing");
      }
   }
}
