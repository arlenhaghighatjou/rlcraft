package crafttweaker.mc1120.brewing;

import crafttweaker.api.item.IIngredient;
import crafttweaker.api.item.IItemStack;
import crafttweaker.api.item.IngredientAny;
import crafttweaker.api.minecraft.CraftTweakerMC;
import java.util.List;
import java.util.function.Predicate;
import javax.annotation.Nonnull;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Tuple;
import net.minecraftforge.common.brewing.VanillaBrewingRecipe;

public final class VanillaBrewingPlus extends VanillaBrewingRecipe {
   private final List<Tuple<IIngredient, IIngredient>> removedRecipes;

   public VanillaBrewingPlus(List<Tuple<IIngredient, IIngredient>> removedRecipes) {
      this.removedRecipes = removedRecipes;
   }

   public ItemStack getOutput(ItemStack input, ItemStack ingredient) {
      IItemStack _input = CraftTweakerMC.getIItemStackForMatching(input);
      IItemStack _ingredient = CraftTweakerMC.getIItemStackForMatching(ingredient);
      return this.removedRecipes.stream().anyMatch((t) -> {
         return ((IIngredient)t.getFirst()).matches(_input) && ((IIngredient)t.getSecond()).matches(_ingredient);
      }) ? ItemStack.EMPTY : super.getOutput(input, ingredient);
   }

   public boolean isIngredient(@Nonnull ItemStack stack) {
      IItemStack _ingredient = CraftTweakerMC.getIItemStackForMatching(stack);
      return super.isIngredient(stack) && this.removedRecipes.stream().noneMatch((t) -> {
         return t.getFirst() == IngredientAny.INSTANCE && ((IIngredient)t.getSecond()).matches(_ingredient);
      });
   }

   public ItemStack getRealOutput(ItemStack input, ItemStack ingredient) {
      return super.getOutput(input, ingredient);
   }

   public List<Tuple<IIngredient, IIngredient>> getRemovedRecipes() {
      return this.removedRecipes;
   }
}
