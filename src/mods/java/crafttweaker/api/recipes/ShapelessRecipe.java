package crafttweaker.api.recipes;

import crafttweaker.api.item.IIngredient;
import crafttweaker.api.item.IItemStack;
import crafttweaker.api.player.IPlayer;
import crafttweaker.api.world.IWorld;
import java.util.HashMap;
import java.util.Map;

/** @deprecated */
@Deprecated
public class ShapelessRecipe implements ICraftingRecipe {
   private final IItemStack output;
   private final IRecipeFunction function;
   private final IRecipeAction action;
   private final IIngredient[] ingredients;
   private final String name;

   public ShapelessRecipe(IItemStack output, IIngredient[] ingredients, IRecipeFunction function, IRecipeAction action) {
      this("shapeless_" + output.getName() + "_" + output.getDamage(), output, ingredients, function, action);
   }

   public ShapelessRecipe(String name, IItemStack output, IIngredient[] ingredients, IRecipeFunction function, IRecipeAction action) {
      this.output = output;
      this.function = function;
      this.ingredients = ingredients;
      this.action = action;
      this.name = name;
   }

   private static ShapelessRecipe.RecipeMatching matchShapeless(IIngredient[] recipe, ICraftingInventory ingredients) {
      int numItems = 0;

      for(int i = 0; i < ingredients.getSize(); ++i) {
         if (ingredients.getStack(i) != null) {
            ++numItems;
         }
      }

      if (numItems != recipe.length) {
         return null;
      } else {
         IItemStack[] matched = new IItemStack[recipe.length];
         int[] indices = new int[recipe.length];

         for(int i = 0; i < ingredients.getSize(); ++i) {
            IItemStack ingredient = ingredients.getStack(i);
            if (ingredient != null) {
               int j = 0;

               while(true) {
                  if (j >= recipe.length) {
                     return null;
                  }

                  if (matched[j] == null && recipe[j].matches(ingredient)) {
                     matched[j] = ingredient;
                     indices[j] = i;
                     break;
                  }

                  ++j;
               }
            }
         }

         return new ShapelessRecipe.RecipeMatching(matched, indices);
      }
   }

   public int getSize() {
      return this.ingredients.length;
   }

   public IIngredient[] getIngredients() {
      return this.ingredients;
   }

   public IItemStack getOutput() {
      return this.output;
   }

   public boolean matches(ICraftingInventory inventory) {
      return matchShapeless(this.ingredients, inventory) != null;
   }

   public IItemStack getCraftingResult(ICraftingInventory inventory) {
      ShapelessRecipe.RecipeMatching matching = matchShapeless(this.ingredients, inventory);
      IItemStack actualOutput = this.output;
      if (this.function != null) {
         Map<String, IItemStack> map = new HashMap();

         for(int i = 0; i < this.ingredients.length; ++i) {
            if (this.ingredients[i].getMark() != null) {
               map.put(this.ingredients[i].getMark(), matching.inputs[i]);
            }
         }

         actualOutput = this.function.process(actualOutput, map, new CraftingInfo(inventory, (IWorld)null));
      }

      return actualOutput == null ? null : actualOutput;
   }

   public void applyTransformers(ICraftingInventory inventory, IPlayer byPlayer) {
      ShapelessRecipe.RecipeMatching matching = matchShapeless(this.ingredients, inventory);

      for(int i = 0; i < this.ingredients.length; ++i) {
         IIngredient ingredient = this.ingredients[i];
         IItemStack transformed = ingredient.applyNewTransform(matching.inputs[i]);
         if (transformed != matching.inputs[i]) {
            inventory.setStack(matching.indices[i], transformed);
         }
      }

   }

   public IIngredient[] getIngredients1D() {
      return new IIngredient[0];
   }

   public IIngredient[][] getIngredients2D() {
      return new IIngredient[0][];
   }

   public boolean isHidden() {
      return false;
   }

   public boolean isShaped() {
      return false;
   }

   public String toCommandString() {
      StringBuilder result = new StringBuilder();
      result.append("recipes.addShapeless(");
      if (this.output != null) {
         result.append(this.output);
      } else {
         result.append("null");
      }

      result.append(", [");

      for(int i = 0; i < this.ingredients.length; ++i) {
         if (i > 0) {
            result.append(", ");
         }

         result.append(this.ingredients[i]);
      }

      result.append("]);");
      return result.toString();
   }

   public boolean hasTransformers() {
      for(IIngredient ingredient : this.ingredients) {
         if (ingredient.hasNewTransformers()) {
            return true;
         }
      }

      return false;
   }

   public boolean hasRecipeAction() {
      return false;
   }

   public boolean hasRecipeFunction() {
      return false;
   }

   public IRecipeAction getAction() {
      return this.action;
   }

   public String getName() {
      return this.name;
   }

   public String getFullResourceName() {
      return null;
   }

   public String getResourceDomain() {
      return null;
   }

   private static class RecipeMatching {
      public final IItemStack[] inputs;
      public final int[] indices;

      public RecipeMatching(IItemStack[] inputs, int[] indices) {
         this.inputs = inputs;
         this.indices = indices;
      }
   }
}
