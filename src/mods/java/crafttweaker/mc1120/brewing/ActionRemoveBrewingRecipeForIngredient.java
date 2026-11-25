package crafttweaker.mc1120.brewing;

import crafttweaker.api.item.IItemStack;
import crafttweaker.api.minecraft.CraftTweakerMC;
import java.util.List;
import java.util.function.Predicate;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.brewing.IBrewingRecipe;

public class ActionRemoveBrewingRecipeForIngredient implements IBrewingAction {
   private final IItemStack ingredient;
   private final List<IBrewingRecipe> allBrewingRecipes;

   public ActionRemoveBrewingRecipeForIngredient(IItemStack ingredient, List<IBrewingRecipe> allBrewingRecipes) {
      this.ingredient = ingredient;
      this.allBrewingRecipes = allBrewingRecipes;
   }

   public void apply() {
      ItemStack _ingredient = CraftTweakerMC.getItemStack(this.ingredient);
      this.allBrewingRecipes.removeIf((it) -> {
         return it.getClass() != VanillaBrewingPlus.class && it.isIngredient(_ingredient);
      });
   }

   public String describe() {
      return "Removing brewing recipes for ingredient: " + this.ingredient.toCommandString();
   }
}
