package crafttweaker.mc1120.brewing;

import crafttweaker.api.item.IIngredient;
import java.util.List;
import java.util.function.Predicate;
import net.minecraft.util.Tuple;
import net.minecraftforge.common.brewing.IBrewingRecipe;
import net.minecraftforge.common.brewing.VanillaBrewingRecipe;

public class ActionFixVanillaBrewingRecipes implements IBrewingAction {
   private final List<Tuple<IIngredient, IIngredient>> toRemoveRecipes;
   private final List<IBrewingRecipe> allBrewingRecipes;

   public ActionFixVanillaBrewingRecipes(List<Tuple<IIngredient, IIngredient>> toRemoveRecipes, List<IBrewingRecipe> allBrewingRecipes) {
      this.toRemoveRecipes = toRemoveRecipes;
      this.allBrewingRecipes = allBrewingRecipes;
   }

   public void apply() {
      List var10000 = this.allBrewingRecipes;
      VanillaBrewingRecipe.class.getClass();
      var10000.removeIf(VanillaBrewingRecipe.class::isInstance);
      this.allBrewingRecipes.add(new VanillaBrewingPlus(this.toRemoveRecipes));
   }

   public String describe() {
      return "Fixing Vanilla Brewing Recipes";
   }
}
