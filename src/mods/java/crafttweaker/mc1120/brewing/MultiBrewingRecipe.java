package crafttweaker.mc1120.brewing;

import crafttweaker.api.item.IIngredient;
import crafttweaker.api.item.IItemStack;
import crafttweaker.api.minecraft.CraftTweakerMC;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.brewing.IBrewingRecipe;

public class MultiBrewingRecipe implements IBrewingRecipe {
   private final IIngredient input;
   private final IIngredient ingredient;
   private final ItemStack output;
   private final boolean visible;

   public MultiBrewingRecipe(IIngredient input, IIngredient[] ingredients, IItemStack output, boolean visible) {
      this.input = input;
      this.output = CraftTweakerMC.getItemStack(output);
      this.visible = visible;
      this.ingredient = this.readIngredientArray(ingredients);
   }

   public ItemStack getOutput(ItemStack input, ItemStack ingredient) {
      return this.isInput(input) && this.isIngredient(ingredient) ? this.getOutput() : ItemStack.EMPTY;
   }

   public boolean isIngredient(ItemStack tester) {
      return this.ingredient.matches(CraftTweakerMC.getIItemStackForMatching(tester));
   }

   public boolean isInput(ItemStack tester) {
      return this.input.matches(CraftTweakerMC.getIItemStackForMatching(tester));
   }

   public ItemStack getOutput() {
      return this.output.copy();
   }

   public List<ItemStack> getInputs() {
      return (List)this.input.getItems().stream().map(CraftTweakerMC::getItemStack).collect(Collectors.toList());
   }

   public List<ItemStack> getIngredients() {
      return (List)this.ingredient.getItems().stream().map(CraftTweakerMC::getItemStack).collect(Collectors.toList());
   }

   public boolean isVisible() {
      return this.visible;
   }

   public boolean isValid() {
      return !this.input.getItems().isEmpty() && !this.ingredient.getItems().isEmpty() && this.output != ItemStack.EMPTY;
   }

   public IIngredient readIngredientArray(IIngredient[] ingredients) {
      if (ingredients.length == 0) {
         throw new IllegalArgumentException("Brewing ingredient list may not be empty");
      } else if (ingredients.length == 1) {
         return ingredients[0];
      } else {
         IIngredient ing = ingredients[0];

         for(int i = 1; i < ingredients.length; ++i) {
            ing = ing.or(ingredients[i]);
         }

         return ing;
      }
   }
}
