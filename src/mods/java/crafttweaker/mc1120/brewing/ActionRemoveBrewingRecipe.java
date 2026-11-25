package crafttweaker.mc1120.brewing;

import crafttweaker.api.item.IIngredient;
import crafttweaker.api.minecraft.CraftTweakerMC;
import java.util.List;
import java.util.function.Predicate;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.brewing.IBrewingRecipe;

public class ActionRemoveBrewingRecipe implements IBrewingAction {
   private final IIngredient input;
   private final IIngredient ingredient;
   private final List<IBrewingRecipe> allBrewingRecipes;

   public ActionRemoveBrewingRecipe(IIngredient input, IIngredient ingredient, List<IBrewingRecipe> allBrewingRecipes) {
      this.input = input;
      this.ingredient = ingredient;
      this.allBrewingRecipes = allBrewingRecipes;
   }

   public void apply() {
      ItemStack _input = CraftTweakerMC.getItemStack(this.input);
      ItemStack _ingredient = CraftTweakerMC.getItemStack(this.ingredient);
      this.allBrewingRecipes.removeIf((it) -> {
         return it.getClass() != VanillaBrewingPlus.class && !it.getOutput(_input, _ingredient).isEmpty();
      });
   }

   public String describe() {
      return "Removing Brewing Recipes for input: " + this.input.toCommandString() + ", ingredient: " + this.ingredient.toCommandString();
   }
}
