package crafttweaker.mc1120.recipes;

import crafttweaker.api.item.IIngredient;
import crafttweaker.api.item.IItemStack;
import crafttweaker.api.minecraft.CraftTweakerMC;
import crafttweaker.api.player.IPlayer;
import crafttweaker.api.recipes.IRecipeAction;
import crafttweaker.api.recipes.IRecipeFunction;
import javax.annotation.Nullable;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.common.crafting.IShapedRecipe;

public class MCRecipeWrapper extends MCRecipeBase {
   private final IRecipe recipe;
   private final boolean isShaped;

   MCRecipeWrapper(IRecipe recipe) {
      super(CraftTweakerMC.getIItemStack(recipe.getRecipeOutput()), recipe.getIngredients(), (IRecipeFunction)null, (IRecipeAction)null, recipe.isDynamic());
      this.recipe = recipe;
      this.isShaped = recipe instanceof IShapedRecipe;
   }

   public boolean matches(InventoryCrafting inv, World worldIn) {
      return this.recipe.matches(inv, worldIn);
   }

   public ItemStack getCraftingResult(InventoryCrafting inv) {
      return this.recipe.getCraftingResult(inv);
   }

   public boolean canFit(int width, int height) {
      return this.recipe.canFit(width, height);
   }

   public NonNullList<ItemStack> getRemainingItems(InventoryCrafting inv) {
      return this.recipe.getRemainingItems(inv);
   }

   public String toCommandString() {
      StringBuilder commandString = new StringBuilder("recipes.addShape");
      if (this.isShaped) {
         IShapedRecipe shapedRecipe = (IShapedRecipe)this.recipe;
         int width = shapedRecipe.getRecipeWidth();
         int height = shapedRecipe.getRecipeHeight();
         commandString.append("d(\"").append(this.getRegistryName()).append("\", ");
         IItemStack output = this.getOutput();
         commandString.append(output == null ? "null" : output.toCommandString()).append(", [");
         if (height > 0 && width > 0) {
            for(int row = 0; row < height; ++row) {
               commandString.append("[");

               for(int column = 0; column < width; ++column) {
                  IIngredient ingredient = CraftTweakerMC.getIIngredient(shapedRecipe.getIngredients().get(row * width + column));
                  commandString.append(ingredient == null ? "null" : ingredient.toCommandString()).append(", ");
               }

               commandString.deleteCharAt(commandString.length() - 1);
               commandString.deleteCharAt(commandString.length() - 1);
               commandString.append("], ");
            }

            commandString.deleteCharAt(commandString.length() - 1);
            commandString.deleteCharAt(commandString.length() - 1);
         }
      } else {
         commandString.append("less(\"").append(this.getRegistryName()).append("\", ");
         IItemStack output = this.getOutput();
         commandString.append(output == null ? "null" : output.toCommandString()).append(", [");
         if (!this.recipe.getIngredients().isEmpty()) {
            for(Ingredient ingredient : this.recipe.getIngredients()) {
               IIngredient iIngredient = CraftTweakerMC.getIIngredient(ingredient);
               commandString.append(iIngredient == null ? "null" : iIngredient.toCommandString()).append(", ");
            }

            commandString.deleteCharAt(commandString.length() - 1);
            commandString.deleteCharAt(commandString.length() - 1);
         }
      }

      return commandString.append("]);").toString();
   }

   public boolean hasTransformers() {
      return false;
   }

   public IIngredient[] getIngredients1D() {
      return CraftTweakerMC.getIIngredients(this.ingredientList);
   }

   public IIngredient[][] getIngredients2D() {
      IIngredient[] ingredients = this.getIngredients1D();
      if (ingredients.length == 0) {
         return new IIngredient[0][0];
      } else if (!this.isShaped) {
         return new IIngredient[][]{ingredients};
      } else {
         IShapedRecipe shapedRecipe = (IShapedRecipe)this.recipe;
         int heigth = shapedRecipe.getRecipeHeight();
         int width = shapedRecipe.getRecipeWidth();
         IIngredient[][] out = new IIngredient[heigth][width];

         for(int row = 0; row < heigth; ++row) {
            for(int column = 0; column < width; ++column) {
               out[row][column] = ingredients[row * width + column];
            }
         }

         return out;
      }
   }

   public boolean isShaped() {
      return this.isShaped;
   }

   @Nullable
   public ResourceLocation getRegistryName() {
      return this.recipe.getRegistryName();
   }

   public void applyTransformers(InventoryCrafting inventory, IPlayer byPlayer) {
   }

   public MCRecipeBase update() {
      return this;
   }
}
