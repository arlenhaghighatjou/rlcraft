package crafttweaker.api.recipes;

import crafttweaker.api.item.IIngredient;
import crafttweaker.api.item.IItemStack;
import crafttweaker.api.player.IPlayer;

/** @deprecated */
@Deprecated
public class UnknownRecipe implements ICraftingRecipe {
   private final IItemStack output;

   public UnknownRecipe(IItemStack output) {
      this.output = output;
   }

   public boolean matches(ICraftingInventory inventory) {
      return false;
   }

   public IItemStack getCraftingResult(ICraftingInventory inventory) {
      return this.output;
   }

   public boolean hasTransformers() {
      return false;
   }

   public boolean hasRecipeAction() {
      return false;
   }

   public boolean hasRecipeFunction() {
      return false;
   }

   public void applyTransformers(ICraftingInventory inventory, IPlayer byPlayer) {
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

   public IItemStack getOutput() {
      return this.output;
   }

   public String toCommandString() {
      return "// unknown recipe type for " + this.output.toCommandString();
   }

   public String getName() {
      return "unknown";
   }

   public String getFullResourceName() {
      return null;
   }

   public String getResourceDomain() {
      return null;
   }
}
