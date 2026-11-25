package crafttweaker.mc1120.recipes;

import crafttweaker.api.item.IItemStack;
import crafttweaker.api.minecraft.CraftTweakerMC;
import crafttweaker.api.recipes.ICraftingRecipe;
import crafttweaker.api.recipes.IMTRecipe;
import crafttweaker.api.recipes.ShapedRecipe;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.item.crafting.ShapedRecipes;
import net.minecraft.util.NonNullList;
import net.minecraft.world.World;

/** @deprecated */
@Deprecated
public class ShapedRecipeBasic extends ShapedRecipes implements IMTRecipe {
   private final ShapedRecipe recipe;

   public ShapedRecipeBasic(String name, NonNullList<Ingredient> basicInputs, ShapedRecipe recipe) {
      super(name, recipe.getWidth(), recipe.getHeight(), basicInputs, CraftTweakerMC.getItemStack(recipe.getOutput()));
      this.recipe = recipe;
   }

   public boolean matches(InventoryCrafting inventory, World world) {
      return this.recipe.matches(MCCraftingInventory.get(inventory));
   }

   public ItemStack getCraftingResult(InventoryCrafting inventory) {
      IItemStack result = this.recipe.getCraftingResult(MCCraftingInventory.get(inventory));
      return result == null ? ItemStack.EMPTY : CraftTweakerMC.getItemStack(result).copy();
   }

   public ICraftingRecipe getRecipe() {
      return this.recipe;
   }
}
